package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONArray;

public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = "SignUpActivity";

    TextView tvName;
    TextView tvUsername;
    TextView tvPassword;
    TextView tvEmail;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        tvName = findViewById(R.id.tvName);
        tvUsername = findViewById(R.id.tvUsername);
        tvPassword = findViewById(R.id.tvPassword);
        tvEmail = findViewById(R.id.tvEmail);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "clicked");
                String name = tvName.getText().toString();
                String username = tvUsername.getText().toString();
                String password = tvPassword.getText().toString();
                String email = tvEmail.getText().toString();
                JSONArray postsLiked = new JSONArray();

                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                user.put("name", name);
                user.put("posts_liked", postsLiked);
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null){
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            Log.i(TAG, "Signed up");
                        } else {
                            Log.e(TAG, "Could not create account", e);
                        }
                    }
                });
            }
        });
    }
}
