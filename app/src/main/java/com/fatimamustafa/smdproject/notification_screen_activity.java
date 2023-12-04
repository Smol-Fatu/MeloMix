package com.fatimamustafa.smdproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class notification_screen_activity extends AppCompatActivity {
    ImageView home,search, library,add;
    private List<Notification> notificationList;
    private NotificationAdapter notificationAdapter;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_screen);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(notificationAdapter);

        fetchDataFromServer();

        home = findViewById(R.id.home);
        search = findViewById(R.id.search);
        library = findViewById(R.id.playlist);
        add = findViewById(R.id.add);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(notification_screen_activity.this, home_screen_activity.class);
                startActivity(intent);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(notification_screen_activity.this, search_screen_activity.class);
                startActivity(intent);
            }
        });
        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(notification_screen_activity.this, library_playlist__screen_activity.class);
                startActivity(intent);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(notification_screen_activity.this, add__upload__screen_activity.class);
                startActivity(intent);
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button sendNotificationButton = findViewById(R.id.sendNotificationButton);

        sendNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call a method to send the push notification
                sendPushNotification();
            }
        });
    }

    private void sendPushNotification() {
        // Replace 'http://localhost/smdproj/notify.php' with the actual URL of your PHP script
        String phpScriptUrl = "http://192.168.10.51/smdproj/notify.php";

        try {
            // Create a URL object with the PHP script URL
            URL url = new URL(phpScriptUrl);

            // Open an HttpURLConnection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                // Read the response from the PHP script
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();

                // Print the response (optional)
                System.out.println(stringBuilder.toString());
            } finally {
                // Disconnect the HttpURLConnection
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fetchDataFromServer() {
        String url = "http://192.168.10.51/smdproj/getnotify.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseJsonResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(notification_screen_activity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void parseJsonResponse(JSONArray response) {
        notificationList.clear();

        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonNotification = response.getJSONObject(i);
                String title = jsonNotification.getString("title");
                String message = jsonNotification.getString("message");

                Notification notification = new Notification(title, message);
                notificationList.add(notification);
            }

            notificationAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Toast.makeText(this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
