package pie.simot.asynctasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.Window;
import android.view.WindowManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import pie.simot.CheckInternet;
import pie.simot.FinalsClass;
import pie.simot.tabbedfragments.Dashboard;

/**
 * Created by elysi on 2/18/2017.
 */

public class RegisterAsyncTask extends AsyncTask<Void, Void, String> {
    private String latlng, orgName, password, address, repName, contactInfo, orgDesc, type;
    private Context c;
    private String registerLink = "http://9653bc79.ngrok.io/users";
    private static ProgressDialog progressDialog;
    private Activity act;

    public RegisterAsyncTask(Context c, Activity act, String latlng, String orgName, String password,
                             String address, String repName, String contactInfo, String orgDesc) {
        this.latlng = latlng;
        this.orgName = orgName;
        this.password = password;
        this.address = address;
        this.repName = repName;
        this.contactInfo = contactInfo;
        this.orgDesc = orgDesc;
        SharedPreferences prefs = c.getSharedPreferences(FinalsClass.PREFS_NAME, Context.MODE_PRIVATE);
        int roleType = prefs.getInt(FinalsClass.ROLE_TYPE, -1);
        if(roleType == 0){
            type = "donator";
        } else{
            type = "donatee";
        }
        this.act = act;
        this.progressDialog = new ProgressDialog(c);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setTitle("Creating account . . .");
        progressDialog.setMessage("Please make sure you have a stable internet connection");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        if (CheckInternet.hasActiveInternetConnection(act)) {
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("company_name", orgName));
            urlParameters.add(new BasicNameValuePair("password", password));
            urlParameters.add(new BasicNameValuePair("address", address));
            urlParameters.add(new BasicNameValuePair("representative_name", repName));
            urlParameters.add(new BasicNameValuePair("company_description", orgDesc));
            urlParameters.add(new BasicNameValuePair("representative_contact_info", contactInfo));
            urlParameters.add(new BasicNameValuePair("type", type));
//        urlParameters.add(new BasicNameValuePair("deviceToken", "12345"));

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost();
            HttpResponse response;
            String json;
            JSONObject req = null;
            String success = "";

            try {
                post.setURI(new URI(registerLink));
                post.setEntity(new UrlEncodedFormEntity(urlParameters));
                response = client.execute(post);
                json = EntityUtils.toString(response.getEntity());
                req = new JSONObject(json);
                success = req.getString("user_id");


                if (success!=null) {
                    SharedPreferences prefs = act.getSharedPreferences(FinalsClass.PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString(FinalsClass.AUTHTOKEN, new JSONObject().getString("auth_token"));
                    edit.putString(FinalsClass.USERID, new JSONObject().getString("user_id"));
                    edit.commit();
                    int roleType;
                    String role = new JSONObject().getString("type");
                    if(role.equals("Benefactor")){
                        roleType = 0;
                    }else{
                        roleType = 1;
                    }
                    edit.putInt(FinalsClass.ROLE_TYPE, roleType);
//
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return success;
        } else {
            return "";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (!result.isEmpty()) {

            SharedPreferences prefs = act.getSharedPreferences(FinalsClass.PREFS_NAME, Context.MODE_PRIVATE);
            int roleType = prefs.getInt(FinalsClass.ROLE_TYPE, 0);

            if(roleType == 0) {
                GetAllCallsTask gat = new GetAllCallsTask(c, act);
                gat.execute();
            } else{
                GetAllItemsTask gat = new GetAllItemsTask(c, act);
                gat.execute();
            }
        } else if (result.trim().isEmpty()) {
        } else {

        }

    }
}