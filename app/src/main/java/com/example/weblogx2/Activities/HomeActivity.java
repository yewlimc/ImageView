package com.example.weblogx2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.weblogx2.Adapters.PostAdapter;
import com.example.weblogx2.Models.Post;
import com.example.weblogx2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

    RecyclerView postRecyclerView;
    PostAdapter postAdapter;
    FirebaseDatabase firebaseDB;
    DatabaseReference dbReference;
    List<Post> postList;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            // If user is already logged in upon opening, go to home intent
            Intent login = new Intent(this, com.example.weblogx2.Activities.LoginActivity.class);
            startActivity(login);
            finish();
        }

        // Get posts from Firebase DB and creates the adapters
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList = new ArrayList<>();

                for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                    Post post = postsnap.getValue(Post.class);
                    postList.add(post);
                    Log.v("PostKey", postsnap.getKey());
                }
                postAdapter = new PostAdapter(getApplicationContext(), postList);
                postRecyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Floating post button
        FloatingActionButton postButton = findViewById(R.id.home_postButton);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send us to a page to select image, enter description, and post
                Intent newPostIntent = new Intent(com.example.weblogx2.Activities.HomeActivity.this, PostActivity.class);
                startActivity(newPostIntent);
            }
        });

        // Layout for post adapters to go in
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

        postRecyclerView = findViewById(R.id.recycleViewHome);
        postRecyclerView.setAdapter(postAdapter);
        postRecyclerView.setLayoutManager(manager);

        firebaseDB = FirebaseDatabase.getInstance();
        dbReference = firebaseDB.getReference("Posts");

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // If user clicks on the logout option, user will be logged out
            case R.id.menu_logout: {
                logout();
                return true;
            }
            default:
                return false;
        }
    }

    // Logout function
    private void logout() {
        // When user is logged out, they will be sent to the login page
        mAuth.signOut();
        Toast.makeText(HomeActivity.this, "Logged out. ", Toast.LENGTH_SHORT).show();
        loginUI();
    }

    // Login intent
    private void loginUI() {
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}

