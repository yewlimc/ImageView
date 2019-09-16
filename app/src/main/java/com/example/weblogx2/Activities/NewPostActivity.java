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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewPostActivity extends AppCompatActivity {

    Toolbar newPostToolbar;
    private ImageView postImage;
    private Button postCamera;
    private Button postButton;
    private Button postGallery;
    private ProgressBar loadingBar;
    private EditText descText;
    private static final int requestCode = 1;
    private static final int permissionCode = 1;
    private Uri postUri = null;
    static final int REQUEST_IMAGE_CAPTURE = 0;
    static final int REQUEST_IMAGE_GALLERY = 1;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        newPostToolbar = findViewById(R.id.newPostToolbar);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("New Post");

        loadingBar = findViewById(R.id.newPostProgressBar);
        loadingBar.setVisibility(View.INVISIBLE);

        postCamera = findViewById(R.id.newPost_cameraButton);
        postCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == postCamera) {
                    dispatchTakePictureIntent();
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
//        postButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // get URI from another method
//
//
//
//                loadingBar.setVisibility(View.VISIBLE);
//                postButton.setVisibility(View.INVISIBLE);
//
//                // Check all input
//                if (!descText.getText().toString().isEmpty() && postUri != null)
//                {
//                    // TODO:  Create post object and add into Firebase DB
//                    // Upload image to Firebase
//                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("post_images");
//                    final StorageReference imageFilePath = storageReference.child(postUri.getLastPathSegment());
//                    imageFilePath.putFile(postUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    String imageURL = uri.toString();
//
//                                    // Create a post object
//                                    Post post = new Post(imageURL, descText.getText().toString(), FirebaseAuth.getInstance().getUid(), FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
//
//                                    // Add post object to Firebase DB
//                                    uploadPost(post);
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(NewPostActivity.this, "Error uploading post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    loadingBar.setVisibility(View.INVISIBLE);
//                                    postButton.setVisibility(View.VISIBLE);
//                                }
//                            });
//                        }
//                    });
//
//                }
//                else
//                {
//                    loadingBar.setVisibility(View.INVISIBLE);
//                    postButton.setVisibility(View.VISIBLE);
//                    Toast.makeText(NewPostActivity.this, "Please make sure image is selected and description is filled. ", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });



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
            case REQUEST_IMAGE_CAPTURE:
                if(resultCode == RESULT_OK){
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    postImage.setImageBitmap(imageBitmap);

                    // get Uri
                    Uri cameraUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),imageBitmap,"title",null));
                    postUri = cameraUri;
                    Log.v("URI Camera: ", postUri.toString());
                    // Toast.makeText(NewPostActivity.this, postUri.toString(), Toast.LENGTH_SHORT).show();
                }

            case REQUEST_IMAGE_GALLERY: {
                if (resultCode == RESULT_OK && requestCode == requestCode && data != null) {
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
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void clickButton(View view) {
        // Log the URI
        if (postUri == null)
        {
            Log.v("URI Status", "null. ");
        }
        else
        {
            Log.v("URI Status", postUri.toString());
        }
    }
}
