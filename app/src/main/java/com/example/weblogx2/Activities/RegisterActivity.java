package com.example.weblogx2.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.weblogx2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class RegisterActivity extends AppCompatActivity {

    ImageView userImage;
    static int requestCode = 1;
    static int ARrequestCode = 1;
    Uri profileUri;

    private EditText userName;
    private EditText userEmail;
    private EditText userPassword;
    private EditText userCfmPassword;
    private ProgressBar loadingBar;
    private Button registerButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Init
        userImage = (ImageView) findViewById(R.id.registerImage);

        userName = findViewById(R.id.registerName);
        userEmail = findViewById(R.id.registerEmail);
        userPassword = findViewById(R.id.registerPassword);
        userCfmPassword = findViewById(R.id.registerCfmPassword);

        mAuth = FirebaseAuth.getInstance();

        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerButton.setVisibility(View.INVISIBLE);
                loadingBar.setVisibility(View.VISIBLE);

                final String name = userName.getText().toString();
                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                final String cfmPassword = userCfmPassword.getText().toString();

                // If the register button is pressed, the fields are checked to see if they are empty
                // If any fields are empty, a "Toast" will be displayed to the user asking him/her to fill in all fields
                // If all fields are filled in, the method "CreateUserAccount" will be started
                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || cfmPassword.isEmpty() || profileUri == null)
                {
                    Log.v("Name", name);
                    Toast.makeText(RegisterActivity.this, "Please fill in all fields. ", Toast.LENGTH_SHORT).show();
                    registerButton.setVisibility(View.VISIBLE);
                    loadingBar.setVisibility(View.INVISIBLE);
                }
                else
                {
                    CreateUserAccount(name, email, password);
                }
            }
        });

        loadingBar = findViewById(R.id.register_progressBar);
        loadingBar.setVisibility(View.INVISIBLE);

        /* When user clicks on the image to choose a profile image,
         * the build version of the device will be checked.
         * If build is below API 22, the phone will check if file permissions are allowed
         * If allowed, the gallery can be accessed.
         */
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT>=22)
                {
                    checkRequestForPermission();
                }
                else
                {
                    openGallery();
                }
            }
        });
    }

    // This method is to create a user account and to add the account into Firebase
    private void CreateUserAccount(final String name, String email, String password) {

        // Creates a user and save it to Firebase
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    Log.v("Username set to", userName.toString());
                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(userName.toString()).build();
                    // If the account is successfully created, a toast is displayed to let the user know that the account has been created
                    Toast.makeText(RegisterActivity.this, "Account created. ", Toast.LENGTH_SHORT).show();
                    // This statement is to check whether if the user has selected an image to be attached to the profile.
                    // If an image is selected during the registration process, an Uri will be assigned to the variable "profileUri" (which is not null)
                    // The updateUserInfo method will be started
                    // note: "image.getDrawable()" is not used as a Uri will be assigned no matter if an image is selected or not.
                    Log.v("Image is available: ", "Upload");
                    updateUserInfo(name, profileUri, mAuth.getCurrentUser());
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "Account failed to create: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    registerButton.setVisibility(View.VISIBLE);
                    loadingBar.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    // This method is to upload the image onto FirebaseStorage
    private void updateUserInfo(final String name, Uri profileUri, final FirebaseUser currentUser) {

        String userID = mAuth.getCurrentUser().getDisplayName();

        // This is to specify which folder the photo is uploaded to.
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("user_photos").child(userID + ".jpg");

        // This is to retrieve the file path of the image
//        final StorageReference imageFilePath = mStorage.child(profileUri.getLastPathSegment());
        final StorageReference imageFilePath = mStorage;

        // This is to put the file into Firebase
        imageFilePath.putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Update the information of the profile: attach the photo to the account
                        Log.v("Name UUI", name);
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(uri).build();
                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {

                                        Toast.makeText(RegisterActivity.this, "Register completed. ", Toast.LENGTH_SHORT).show();
                                        homeUI();

                                }

                            }
                        });
                    }
                });
            }
        });

    }

    private void homeUI() {
        Intent homeIntent = new Intent (getApplicationContext(), HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }

    private void openGallery() {
        //TODO: Open the gallery for user to select an image
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, requestCode);
    }

    private void checkRequestForPermission() {
        //TODO: Check and request permission of device before user selects an image.

        // If permission is not granted, user will be asked to allow access.
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                Toast.makeText(RegisterActivity.this, "Please allow the application to access the gallery. ", Toast.LENGTH_SHORT).show();

            }
            else
            {
                ActivityCompat.requestPermissions(RegisterActivity.this, new String []{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
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

        if (resultCode == RESULT_OK && requestCode == requestCode && data != null)
        {
            // User has picked an image
            // Save the image URI
            profileUri = data.getData();
            userImage.setImageURI(profileUri);
        }
    }
}