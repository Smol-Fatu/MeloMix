package com.fatimamustafa.smdproject;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class add__upload__screen_activity extends AppCompatActivity {

    private Uri selectedAudioUri;
    private Bitmap selectedImageBitmap;

    private static final int AUDIO_GALLERY = 1;
    private static final int IMAGE_GALLERY = 2;
    private static final int DP_REQUEST_CODE = 200;
    private RelativeLayout getAudioFromGalleryButton;
    private View uploadDataOnServerButton;
    //    private VideoView showSelectedAudio;
    private ImageView showSelectedImage;
    private EditText nameEditText, artistNameEditText;

    private ByteArrayOutputStream byteArrayOutputStream;
    private byte[] audioByteArray;
    private byte[] imageByteArray;
    private String audioConvertString;
    private String imageConvertString;
    View create;
    ImageView home,search, library,add;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_upload_screen);

        getAudioFromGalleryButton = findViewById(R.id.buttonSelect);
        uploadDataOnServerButton = findViewById(R.id.buttonUpload);
//        showSelectedAudio = findViewById(R.id.audioView); // Assuming you have an appropriate component for audio playback
        showSelectedImage = findViewById(R.id.uploadedimage);
        nameEditText = findViewById(R.id.songname);
        artistNameEditText = findViewById(R.id.artistname);
        create = findViewById(R.id.create);

        home = findViewById(R.id.home);
        search = findViewById(R.id.search);
        library = findViewById(R.id.playlist);
        add = findViewById(R.id.add);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(add__upload__screen_activity.this, home_screen_activity.class);
                startActivity(intent);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(add__upload__screen_activity.this, search_screen_activity.class);
                startActivity(intent);
            }
        });
        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(add__upload__screen_activity.this, library_playlist__screen_activity.class);
                startActivity(intent);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(add__upload__screen_activity.this, add__upload__screen_activity.class);
                startActivity(intent);
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(add__upload__screen_activity.this, add__record__screen_activity.class);
                startActivity(intent);
            }
        });

        byteArrayOutputStream = new ByteArrayOutputStream();

        getAudioFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseAudioFromGallery();
            }
        });

        showSelectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        uploadDataOnServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedAudioUri != null && selectedImageBitmap != null) {
                    uploadDataToServer(selectedAudioUri, selectedImageBitmap);
                } else {
                    Toast.makeText(add__upload__screen_activity.this, "Please select audio and image files", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void chooseAudioFromGallery() {
        Intent audioIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(audioIntent, AUDIO_GALLERY);
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
                                chooseImageFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void chooseImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, IMAGE_GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, IMAGE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == AUDIO_GALLERY) {
                selectedAudioUri = data.getData();
                uploadDataOnServerButton.setVisibility(View.VISIBLE);
            } else if (requestCode == IMAGE_GALLERY) {
                Uri contentURI = data.getData();
                try {
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    showSelectedImage.setImageBitmap(selectedImageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(add__upload__screen_activity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void uploadDataToServer(Uri audioUri, Bitmap imageBitmap) {
        // Stop audio playback for uploading
//        showSelectedAudio.stopPlayback();

        // Convert audio and image to Base64
        String audioPath = getRealPathFromURI(audioUri);

        if (audioPath != null) {
            new UploadDataAsyncTask().execute(audioPath, imageBitmap);
        } else {
            Toast.makeText(add__upload__screen_activity.this, "Error retrieving audio path", Toast.LENGTH_SHORT).show();
        }
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(column_index);
            cursor.close();
            return filePath;
        }

        return null;
    }

    private class UploadDataAsyncTask extends AsyncTask<Object, Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(add__upload__screen_activity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Uploading Data...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Object... params) {
            String audioPath = (String) params[0];
            Bitmap imageBitmap = (Bitmap) params[1];

            try {
                URL url = new URL("http://192.168.10.51/smdproj/addsong.php"); // Replace with your server URL
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(20000);
                httpURLConnection.setConnectTimeout(20000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                ByteArrayOutputStream audioByteArrayOutputStream = new ByteArrayOutputStream();
                audioByteArray = Files.readAllBytes(Paths.get(audioPath));
                audioConvertString = Base64.encodeToString(audioByteArray, Base64.DEFAULT);

                // Convert image to Base64
                ByteArrayOutputStream imageByteArrayOutputStream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 40, imageByteArrayOutputStream);
                imageByteArray = imageByteArrayOutputStream.toByteArray();
                imageConvertString = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

                // Additional parameters
                String name = nameEditText.getText().toString();
                String artistName = artistNameEditText.getText().toString();

                Map<String, String> paramsMap = new HashMap<>();
                paramsMap.put("audio_data", audioConvertString);
                paramsMap.put("audio_tag", "audio_name"); // Replace with your desired audio name
                paramsMap.put("image_data", imageConvertString);
                paramsMap.put("name", name);
                paramsMap.put("artist_name", artistName);
                paramsMap.put("image_path", imageConvertString); // Adjust the image path as needed
                paramsMap.put("song_path", audioConvertString); // Adjust the song path as needed

                bufferedWriter.write(getPostDataString(paramsMap));

                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                int responseCode = httpURLConnection.getResponseCode();
                Log.d("HTTP Response Code", String.valueOf(responseCode));
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line);
                    }

                    bufferedReader.close();
                    return response.toString();
                } else {
                    Log.d("Error", "HTTP response code: " + responseCode);
                    return "Error uploading data. HTTP response code: " + responseCode;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Error", "Error uploading data: " + e.getMessage());

                return "Error uploading data: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Toast.makeText(add__upload__screen_activity.this, result, Toast.LENGTH_SHORT).show();
        }

        private String getPostDataString(Map<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();

            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    result.append("&");
                }

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }
    }

}