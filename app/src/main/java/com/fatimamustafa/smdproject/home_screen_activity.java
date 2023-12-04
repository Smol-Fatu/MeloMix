package com.fatimamustafa.smdproject;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class home_screen_activity extends Activity {
    ImageView home,search, library,add;
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

        home = findViewById(R.id.home);
        search = findViewById(R.id.search);
        library = findViewById(R.id.playlist);
        add = findViewById(R.id.add);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_screen_activity.this, home_screen_activity.class);
                startActivity(intent);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_screen_activity.this, search_screen_activity.class);
                startActivity(intent);
            }
        });
        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_screen_activity.this, library_playlist__screen_activity.class);
                startActivity(intent);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_screen_activity.this, add__record__screen_activity.class);
                startActivity(intent);
            }
        });

    }
}