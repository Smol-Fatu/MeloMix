package com.fatimamustafa.smdproject;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class android_small___1_activity extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.android_small___1);

        SharedPreferences sharedPreferences = getSharedPreferences("loginPreferences", MODE_PRIVATE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if the user is already logged in
                if (sharedPreferences.getBoolean("isLoggedIn", false)) {
                    // User is logged in, navigate to home screen
                    Intent intent = new Intent(android_small___1_activity.this, home_screen_activity.class);
                    // Start the new activity
                    startActivity(intent);
                    finish();  // Finish the splash screen activity
                } else {
                    // User is not logged in, proceed with login or show login screen
                    Intent intent = new Intent(android_small___1_activity.this, login_screen_activity.class);
                    // Start the new activity
                    startActivity(intent);
                }
            }
        }, 5000); // 5000 milliseconds (5 seconds)

    }
}

