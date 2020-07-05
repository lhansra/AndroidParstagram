package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    Button onSignUp;
    Button onSignIn;
    TextView tvUsername;
    TextView tvPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        onSignIn = findViewById(R.id.login);
        onSignUp = findViewById(R.id.signUp);
        tvUsername = findViewById(R.id.tvUsername);
        tvPassword = findViewById(R.id.tvPassword);

        if (ParseUser.getCurrentUser() != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

        } else {

            onSignIn.setVisibility(View.VISIBLE);
            onSignUp.setVisibility(View.VISIBLE);
            tvUsername.setVisibility(View.VISIBLE);
            tvPassword.setVisibility(View.VISIBLE);


            onSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                    startActivity(intent);
                }
            });

            onSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = tvUsername.getText().toString();
                    String password = tvPassword.getText().toString();
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                tvUsername.setText("");
                                tvPassword.setText("");
                                startActivity(intent);
                            } else {
                                Log.e(TAG, "Error logging in user: " + user, e);
                            }
                        }
                    });
                }
            });
        }
    }
}