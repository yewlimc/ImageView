package com.example.weblogx2.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.weblogx2.Models.Post;
import com.example.weblogx2.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class NewPostActivity extends AppCompatActivity {

    Toolbar newPostToolbar;
    private ImageView postImage;
    private Button postCamera;
    private Button postButton;
    private Button postGallery;
    private ProgressBar loadingBar;
    private EditText descText;
    private Uri camUri;
    private static final int requestCode = 1;
    private static final int permissionCode = 1;
    private Uri postUri = null;
    static final int REQUEST_IMAGE_CAPTURE = 0;
    static final int REQUEST_IMAGE_GALLERY = 1;
    static final int CAPTURE_CODE = 2;
    String currentPhotoPath;
    PlacesClient placesClient;

    String placeString = "";
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        final String apiKey = "AIzaSyBVumoYBdoVkYzHZTgXJXyjFDkAk9i3TqI";


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//            Toast.makeText(RegisterActivity.this, " We are in portrait mode",
//                    Toast.LENGTH_SHORT).show();
            newPostToolbar = findViewById(R.id.newPostToolbar);
            setSupportActionBar(newPostToolbar);
            getSupportActionBar().setTitle("New Post");
        }else{
//            Toast.makeText(RegisterActivity.this, "We are in Landscape mode",
//                    Toast.LENGTH_SHORT).show();
        }

        loadingBar = findViewById(R.id.newPostProgressBar);
        loadingBar.setVisibility(View.INVISIBLE);

        postCamera = findViewById(R.id.newPost_cameraButton);
        postCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED )
                    {
                        String [] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, permissionCode);
                    }
                    else
                    {
                        // Permission is granted
                        dispatchTakePictureIntent();
                    }
                }
                else
                {
                    // Version below Marshmellow
                }
            }
        });

        postImage = findViewById(R.id.newPost_image);

        postGallery = findViewById(R.id.newPost_galleryButton);
        postGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NewPostActivity.this, "Image clicked.  ", Toast.LENGTH_SHORT).show();
                checkRequestForPermissionGallery();
            }
        });

        descText = findViewById(R.id.newPostDesc);

        postButton = findViewById(R.id.postButton);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get URI from another method
                clickButton();

                loadingBar.setVisibility(View.VISIBLE);
                postButton.setVisibility(View.INVISIBLE);

                // Check all input
                // TODO:  Create post object and add into Firebase DB
                if (!descText.getText().toString().isEmpty() && postUri != null)
                    {
                    // Upload image to Firebase
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");
                    Date currentTime = Calendar.getInstance().getTime();
                    DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                    final String posttime = simpleDateFormat.toString();
                    final String postDate = dateFormat.format(currentTime);
                    String locationName;
                    Log.v("Timestamp", posttime);

                    if (placeString.equals(""))
                    {
                        locationName = "";
                        Log.v("Location post", "nil");
                    }
                    else
                    {
                        locationName = placeString;
                        Log.v("Location post", placeString);
                    }

                    final String lName = locationName;

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("post_images").child((userID + "_" + postDate + posttime + ".jpg"));
                    final StorageReference imageFilePath = storageReference.child(postUri.getLastPathSegment());
                    imageFilePath.putFile(postUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageURL = uri.toString();

                                    // Create a post object
                                    Post post = new Post(imageURL, descText.getText().toString(), FirebaseAuth.getInstance().getUid(), FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), postDate, lName);

                                    // Add post object to Firebase DB
                                    uploadPost(post);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(NewPostActivity.this, "Error uploading post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    loadingBar.setVisibility(View.INVISIBLE);
                                    postButton.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });

                }
                else
                {
                    loadingBar.setVisibility(View.INVISIBLE);
                    postButton.setVisibility(View.VISIBLE);
                    Toast.makeText(NewPostActivity.this, "Please make sure image is selected and description is filled. ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (!Places.isInitialized())
        {
            Places.initialize(getApplicationContext(), apiKey);
        }
        placesClient = Places.createClient(this);

        final AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Autocomplete returns value
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                placeString = place.getName();
                latLng = place.getLatLng();
                Log.v("Place LatLng", latLng.latitude + ", " +latLng.longitude);
                Log.v("Place Name", placeString);

            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(NewPostActivity.this, "An error occurred: " + status, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadPost(Post post) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference reference = database.getReference("Posts").push();

        // Get post's unique ID and update the post key
        String postKey = reference.getKey();
        post.setPostKey(postKey);

        // Add post data into Firebase DB
        reference.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(NewPostActivity.this, "Post complete. ", Toast.LENGTH_SHORT).show();
                loadingBar.setVisibility(View.INVISIBLE);
                postButton.setVisibility(View.VISIBLE);
                homeUI();
            }
        });
    }



    private void openGallery() {
        //TODO: Open the gallery for user to select an image
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
    }

    private void checkRequestForPermissionGallery() {
        //TODO: Check and request permission of device before user selects an image from the gallery.

        // If permission is not granted, user will be asked to allow access.
        if (ContextCompat.checkSelfPermission(NewPostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(NewPostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                Toast.makeText(NewPostActivity.this, "Please allow the application to access the gallery. ", Toast.LENGTH_SHORT).show();

            }
            else
            {

                ActivityCompat.requestPermissions(NewPostActivity.this, new String []{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
            }
        }
        else
        {
            openGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case CAPTURE_CODE:
                if(resultCode == RESULT_OK && requestCode == CAPTURE_CODE && data != null){
                    File file = new File(currentPhotoPath);

                    Uri uri = Uri.fromFile(file);
                    postUri = uri;
                    postImage.setImageURI(uri);
                    // postImage.setImageBitmap(imageBitmap);

                    Log.v("URI CameraOAR: ", postUri.toString());
                    // Toast.makeText(NewPostActivity.this, postUri.toString(), Toast.LENGTH_SHORT).show();
                }

            case REQUEST_IMAGE_GALLERY: {
                if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_GALLERY && data != null) {
                    // User has picked an image
                    // Save the image URI
                    postUri = data.getData();
                    postImage.setImageURI(postUri);
                }
            }
        }
    }

    private void homeUI() {
        Intent homeIntent = new Intent (getApplicationContext(), HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAPTURE_CODE);
            }
        }
    }

        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults){
            switch (requestCode) {
                case permissionCode: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permissions is allowed
                        dispatchTakePictureIntent();
                    } else {
                        Toast.makeText(NewPostActivity.this, "Please allow the application to access the camera. ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

        public void clickButton () {
            // Log the URI
            if (postUri == null) {
                Log.v("URI Status", "null. ");
            } else {
                Log.v("URI Status", postUri.toString());
            }
        }

    }




