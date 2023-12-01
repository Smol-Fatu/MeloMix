package com.fatimamustafa.smdproject;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class signup_screen_activity extends Activity {

    EditText username, name, email, password;
    View signup;

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);

        username = findViewById(R.id.username);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        signup= findViewById(R.id.signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the method to send a POST request
                addUser();
                Intent intent = new Intent(signup_screen_activity.this, login_screen_activity.class);
                startActivity(intent);
            }
        });

    }
    private void addUser() {
        String url = "http://172.16.48.81/assign3smd/insert.php";

        // Retrieve input values
        final String newUsername = username.getText().toString().trim(); // Assuming you have an EditText field for the username
        final String newName = name.getText().toString().trim();
        final String newEmail = email.getText().toString().trim();
        final String newPassword = password.getText().toString().trim();

        // Check for empty fields
        if (newUsername.isEmpty() || newName.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(signup_screen_activity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a StringRequest for the POST request
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response after adding the user
                        Toast.makeText(signup_screen_activity.this, response, Toast.LENGTH_SHORT).show();
                        // You may want to navigate back to the main activity or handle the response accordingly

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        Toast.makeText(signup_screen_activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Add parameters to the request body
                Map<String, String> params = new HashMap<>();
                params.put("username", newUsername);
                params.put("name", newName);
                params.put("email", newEmail);
                params.put("password", newPassword);
                return params;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

        // Optionally, you can execute the AsyncTask as well (if needed)
        PostData postData = new PostData(newUsername, newName, newEmail, newPassword);
        postData.execute();
    }

    public class PostData extends AsyncTask<Void, Void, String> {
        String res;
        String newUsername, newName, newEmail, newPassword;

        // Constructor to receive input values
        public PostData(String newUsername, String newName, String newEmail, String newPassword) {
            this.newUsername = newUsername;
            this.newName = newName;
            this.newEmail = newEmail;
            this.newPassword = newPassword;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Create the URL
                URL url = new URL("http://192.168.10.51/smdproj/insert.php");

                // Open a connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set the request method to POST
                connection.setRequestMethod("POST");

                // Set the timeout for both the connection and the read
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                // Enable input/output streams and set input as true because you are sending data
                connection.setDoInput(true);
                connection.setDoOutput(true);

                // Add your data to the request
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                // Construct the POST data using input values
                String postData = "username=" + URLEncoder.encode(newUsername, "UTF-8") +
                        "&name=" + URLEncoder.encode(newName, "UTF-8") +
                        "&email=" + URLEncoder.encode(newEmail, "UTF-8") +
                        "&password=" + URLEncoder.encode(newPassword, "UTF-8");

                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();

                // Connect to the server
                connection.connect();

                // Read the response
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                BufferedReader br = new BufferedReader(reader);
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                br.close();
                reader.close();
                connection.disconnect();

                // Log and store the response
                Log.d("response", builder.toString());
                res = builder.toString();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Handle the response as needed
            // You might want to update the UI or perform other actions based on the server response
            Toast.makeText(signup_screen_activity.this, "User successfully created", Toast.LENGTH_SHORT).show();

        }
    }


}
