package com.example.weblogx2.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.weblogx2.Adapters.PostAdapter;
import com.example.weblogx2.Models.Post;
import com.example.weblogx2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

    RecyclerView postRecyclerView;
    PostAdapter postAdapter;
    FirebaseDatabase firebaseDB;
    DatabaseReference dbReference;
    private Button postDelete;
    final int DELETE_MENU = 1;
    List<Post> postList;

    @Override
    protected void onStart() {
        super.onStart();
        // Get posts
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList = new ArrayList<>();

                for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                    Post post = postsnap.getValue(Post.class);
                    postList.add(post);
                    Log.v("Post", "");

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

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        FloatingActionButton postButton = findViewById(R.id.postButton);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Post button clicked. ", Toast.LENGTH_LONG).show();
                // Send us to a page to select image, enter description, and post
                Intent newPostIntent = new Intent(com.example.weblogx2.Activities.HomeActivity.this, NewPostActivity.class);
                startActivity(newPostIntent);
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

        postRecyclerView = findViewById(R.id.recycleViewHome);
        // postRecyclerView = getLayoutInflater().inflate(R.layout.activity_home, null).findViewById(R.id.recycleViewHome);
        postRecyclerView.setAdapter(postAdapter);
        postRecyclerView.setLayoutManager(manager);


//        postRecyclerView.setLayoutManager(manager);
//        postRecyclerView.setHasFixedSize(true);
//        postAdapter = new PostAdapter(getApplicationContext(), postList);


        firebaseDB = FirebaseDatabase.getInstance();
        dbReference = firebaseDB.getReference("Posts");

    }
}
