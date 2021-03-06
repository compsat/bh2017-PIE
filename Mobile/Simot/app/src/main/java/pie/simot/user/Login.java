package pie.simot.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import pie.simot.asynctasks.LoginAsync;
import pie.simot.tabbedfragments.Dashboard;
import pie.simot.R;

/**
 * A login screen that offers login via email/password.
 */
public class Login extends AppCompatActivity{


    private String username, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameInput = (EditText)findViewById(R.id.username);
        final EditText passwordInput = (EditText)findViewById(R.id.password);

        Button loginButton = (Button)findViewById(R.id.loginButton);
        Button registerButton = (Button)findViewById(R.id.registerButton);

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(Login.this, Dashboard.class);
                Login.this.startActivity(next);
                finish();
//                username = usernameInput.getText().toString();
//                password = passwordInput.getText().toString();
//
//                LoginAsync la = new LoginAsync(Login.this.getBaseContext(), Login.this, username, password);
//                la.execute();
            }
        });
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterAs ask = new RegisterAs();
//                RegisterAs.setSize(Login.this, ask);
                ask.show(getSupportFragmentManager(), "ask_role");

            }
        });

    }
}

