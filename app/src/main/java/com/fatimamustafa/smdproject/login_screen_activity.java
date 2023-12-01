package com.fatimamustafa.smdproject;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class login_screen_activity extends Activity {


    View loginButton;
    TextView signupButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        loginButton = findViewById(R.id.login);
        signupButton = findViewById(R.id.sign_up);
        signupButton.setOnClickListener(v -> {
//             Start the Signup activity
             Intent intent = new Intent(this, signup_screen_activity.class);
             startActivity(intent);
        });

    }
}

