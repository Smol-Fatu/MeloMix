package com.fatimamustafa.smdproject;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class login_screen_activity extends Activity {

    EditText username, password;
    View loginButton;
    TextView signupButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        signupButton = findViewById(R.id.sign_up);
        signupButton.setOnClickListener(v -> {
//             Start the Signup activity
             Intent intent = new Intent(this, signup_screen_activity.class);
             startActivity(intent);
        });
        loginButton.setOnClickListener(v -> {
            getUser();
        });

    }
    private void getUser() {
        String url = "http://192.168.10.51/smdproj/getuser.php";
        String enteredUsername = username.getText().toString().trim();
        String enteredPassword = password.getText().toString().trim();

        GetUserAsyncTask getUserAsyncTask = new GetUserAsyncTask();
        getUserAsyncTask.execute(url, enteredUsername, enteredPassword);
    }

    public class GetUserAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            try {
                // Create the URL
                URL url = new URL(params[0]);

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
                OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");

                // Construct the POST data using input values
                String postData = "username=" + URLEncoder.encode(params[1], "UTF-8") +
                        "&password=" + URLEncoder.encode(params[2], "UTF-8");

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
                response = builder.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Handle the response as needed
            // You might want to update the UI or perform other actions based on the server response
            Log.d("response", "PostExecute: " + s);
            if (s != null && !s.isEmpty()) {
                try {
                    // Parse the response as a JSON object
                    JSONObject jsonResponse = new JSONObject(s);

                    // Check if the response contains the "Status" key
                    if (jsonResponse.has("Status")) {
                        // Retrieve the value of the "Status" key
                        int status = jsonResponse.getInt("Status");

                        if (status == 1) {
                            // The response indicates success
                            Toast.makeText(login_screen_activity.this, "User Found", Toast.LENGTH_SHORT).show();

                            // Access the "User" field and then get the "id" field
                            JSONObject userObject = jsonResponse.getJSONObject("User");
                            int userId = userObject.getInt("id");

                            SharedPreferences sharedPreferences = getSharedPreferences("loginPreferences", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("userId", userId);
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();
                            Intent intent = new Intent(login_screen_activity.this, home_screen_activity.class);
                            startActivity(intent);
                        } else {
                            // The response indicates failure
                            Toast.makeText(login_screen_activity.this, "User Not Found", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        // Handle the case where the "Status" key is not present in the response
                        Toast.makeText(login_screen_activity.this, "Invalid Response Format", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // Handle JSON parsing error
                    Log.d("error",e.toString());
                    Toast.makeText(login_screen_activity.this, "JSON Parsing Error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                // Handle the case where the response is empty or null
                Toast.makeText(login_screen_activity.this, "Empty or Null Response", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

