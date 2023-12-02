package com.fatimamustafa.smdproject;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;


public class home_screen_activity extends Activity {

    ImageView notification;
    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        notification = findViewById(R.id.notification);
        notification.setOnClickListener(v -> {
            // Start the Notification activity
            Intent intent = new Intent(this, notification_screen_activity.class);
            startActivity(intent);
        });

    }
}