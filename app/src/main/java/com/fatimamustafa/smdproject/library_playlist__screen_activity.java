package com.fatimamustafa.smdproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.HashMap;
import java.util.Map;

public class library_playlist__screen_activity extends AppCompatActivity {
    RelativeLayout edit;
    TextView Name;
    CircleImageView profilepic;
    int userId;
    ImageView home,search, library,add;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_playlist_screen);

        edit= findViewById(R.id.editprofile);
        edit.setOnClickListener(v -> {
            // Start the Notification activity
            Intent intent = new Intent(this, edit_profile_screen_activity.class);
            startActivity(intent);
        });
        home = findViewById(R.id.home);
        search = findViewById(R.id.search);
        library = findViewById(R.id.playlist);
        add = findViewById(R.id.add);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(library_playlist__screen_activity.this, home_screen_activity.class);
                startActivity(intent);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(library_playlist__screen_activity.this, search_screen_activity.class);
                startActivity(intent);
            }
        });
        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(library_playlist__screen_activity.this, library_playlist__screen_activity.class);
                startActivity(intent);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(library_playlist__screen_activity.this, add__upload__screen_activity.class);
                startActivity(intent);
            }
        });
        profilepic= findViewById(R.id.profilepic);
        Name= findViewById(R.id.Name);

        SharedPreferences sharedPreferences = getSharedPreferences("loginPreferences", MODE_PRIVATE);

        userId = sharedPreferences.getInt("userId", -1);

        fetchDataFromServer();
    }

    private void fetchDataFromServer() {
        String url = "http://192.168.10.51/smdproj/getuser2.php";

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int status = jsonResponse.getInt("Status");
                            if (status == 1) {
                                JSONObject user = jsonResponse.getJSONObject("User");
                                String name = user.getString("name");
                                String imageURL = user.getString("profile_pic");
                                // Set the retrieved data to your UI elements
                                Name.setText(name);
                                Picasso.get()
                                        .load("http://192.168.10.51/smdproj/" + imageURL)
                                        .error(R.drawable.imageonline_co_transparentimage_1) // Replace with your error placeholder
                                        .into(profilepic);
                            } else {
                                Toast.makeText(library_playlist__screen_activity.this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(library_playlist__screen_activity.this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(library_playlist__screen_activity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Add your POST parameters here
                params.put("id", String.valueOf(userId));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}