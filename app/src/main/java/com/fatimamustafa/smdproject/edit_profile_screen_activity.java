package com.fatimamustafa.smdproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class edit_profile_screen_activity extends AppCompatActivity {
    ImageView uploadphoto;
    TextView updateProfile;
    EditText userName, name, email;
    int DP_REQUEST_CODE = 200;
    TextView UploadImageOnServerButton;
    RelativeLayout GetImageFromGalleryButton;
    ImageView ShowSelectedImage;

    Bitmap FixBitmap;

    String ImageTag = "profile_pic";
    String ImageName = "image_data";
    String UserNameTag = "username";
    String NameTag = "name";
    String EmailTag = "email";

    ProgressDialog progressDialog;

    ByteArrayOutputStream byteArrayOutputStream;
    byte[] byteArray;
    String ConvertImage;
    String GetImageNameFromEditText;
    HttpURLConnection httpURLConnection;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter;
    int RC;
    BufferedReader bufferedReader;
    StringBuilder stringBuilder;
    boolean check = true;
    int userId;
    ImageView img;
    ImageView home, search, library, add;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_screen);

        userName = findViewById(R.id.username);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);

        GetImageFromGalleryButton = findViewById(R.id.uploadphoto);
        UploadImageOnServerButton = findViewById(R.id.savechanges);
        ShowSelectedImage = findViewById(R.id.uploadedimage);

        SharedPreferences sharedPreferences = getSharedPreferences("loginPreferences", MODE_PRIVATE);

        userId = sharedPreferences.getInt("userId", -1);

        byteArrayOutputStream = new ByteArrayOutputStream();

        home = findViewById(R.id.home);
        search = findViewById(R.id.search);
        library = findViewById(R.id.playlist);
        add = findViewById(R.id.add);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(edit_profile_screen_activity.this, home_screen_activity.class);
                startActivity(intent);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(edit_profile_screen_activity.this, search_screen_activity.class);
                startActivity(intent);
            }
        });
        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(edit_profile_screen_activity.this, library_playlist__screen_activity.class);
                startActivity(intent);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(edit_profile_screen_activity.this, add__upload__screen_activity.class);
                startActivity(intent);
            }
        });
        GetImageFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        UploadImageOnServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetImageNameFromEditText = userName.getText().toString();
                UploadImageToServer();
            }
        });


        if (ContextCompat.checkSelfPermission(edit_profile_screen_activity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);
            }
        }

    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Photo Gallery", "Camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, DP_REQUEST_CODE);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, DP_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == DP_REQUEST_CODE) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    FixBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    ShowSelectedImage.setImageBitmap(FixBitmap);
                    UploadImageOnServerButton.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(edit_profile_screen_activity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void UploadImageToServer() {
        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byteArray = byteArrayOutputStream.toByteArray();
        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {
            // Utility method to check if the activity is finishing or already finished
            private boolean isActivityValid(Activity activity) {
                return activity != null && !activity.isFinishing() && !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed());
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (isActivityValid(edit_profile_screen_activity.this)) {
                    progressDialog = ProgressDialog.show(edit_profile_screen_activity.this, "Image is Uploading", "Please Wait", false, false);
                }
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                if (isActivityValid(edit_profile_screen_activity.this)) {
                    progressDialog.dismiss();

                    // Display the message from PHP in a Toast
                    Log.d("UploadImageToServer", "Response: " + string1);
                    Toast.makeText(edit_profile_screen_activity.this, string1, Toast.LENGTH_LONG).show();

                    // Check if the profile was updated successfully
                    try {
                        // Check if the response is an array
                        if (string1.startsWith("[")) {
                            JSONArray jsonArray = new JSONArray(string1);
                            // Handle JSONArray if needed
                        } else {
                            // It's assumed to be an object
                            JSONObject jsonResponse = new JSONObject(string1);
                            int status = jsonResponse.getInt("Status");
                            if (status == 1) {
                                Toast.makeText(edit_profile_screen_activity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(edit_profile_screen_activity.this, "Profile updation failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                edit_profile_screen_activity.ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String, String> HashMapParams = new HashMap<>();
                HashMapParams.put("id", String.valueOf(userId));  // Pass the user ID here
                HashMapParams.put(ImageTag, ConvertImage);
                HashMapParams.put(ImageName, userName.getText().toString());
                HashMapParams.put(UserNameTag, userName.getText().toString());
                HashMapParams.put(NameTag, name.getText().toString());
                HashMapParams.put(EmailTag, email.getText().toString());

                try {
                    String FinalData = imageProcessClass.ImageHttpRequest("http://192.168.10.51/smdproj/editprofile.php", HashMapParams);
                    Log.d("UploadImageToServer", "FinalData: " + FinalData);
                    return FinalData;
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error during image upload and profile update";
                }
            }
        }

        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass {
        public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                URL url = new URL(requestURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(20000);
                httpURLConnection.setConnectTimeout(20000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(
                        new OutputStreamWriter(outputStream, "UTF-8"));
                bufferedWriter.write(bufferedWriterDataFN(PData));
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                int RC = httpURLConnection.getResponseCode();
                if (RC == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String RC2;
                    while ((RC2 = bufferedReader.readLine()) != null) {
                        stringBuilder.append(RC2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException, UnsupportedEncodingException {
            StringBuilder stringBuilder = new StringBuilder();
            boolean check = true;
            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilder.append("&");
                stringBuilder.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
                stringBuilder.append("=");
                stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }
            return stringBuilder.toString();
        }
    }

    private static final String PROGRESS_DIALOG_VISIBLE_KEY = "progressDialogVisible";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            outState.putBoolean(PROGRESS_DIALOG_VISIBLE_KEY, true);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getBoolean(PROGRESS_DIALOG_VISIBLE_KEY)) {
            showProgressDialog();
        }
    }

    private void showProgressDialog() {
        progressDialog = ProgressDialog.show(edit_profile_screen_activity.this, "Image is Uploading", "Please Wait", false, false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        // Close the database helper when the activity is destroyed
    }
}