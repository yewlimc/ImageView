package com.example.weblogx2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weblogx2.Models.Post;
import com.example.weblogx2.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context mContext;
    List<Post> mData;

    int buttonClicked = 0;

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String pid = mData.get(position).getPostKey();
        final String post_userID = mData.get(position).getUserID();
        final String imageURL = mData.get(position).getImage();
        holder.viewDesc.setText(mData.get(position).getDescription());
        Picasso.get().load(mData.get(position).getImage()).into(holder.postImage);
        Picasso.get().load(mData.get(position).getUserImage()).into(holder.post_userImage);
        holder.userName.setText(mData.get(position).getUserName());
        holder.location.setText(mData.get(position).getLocation());

        holder.datetime.setText(mData.get(position).getDateTime());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!post_userID.equals(userID)) {
                    Toast.makeText(mContext, "Not your post. ", Toast.LENGTH_SHORT).show();
                    buttonClicked = buttonClicked + 1;
                    Log.v("Button clicked", "" + buttonClicked);

                    switch (buttonClicked) {
                        case 5: {
                            Toast.makeText(mContext, "It's not your post. Stop it. ", Toast.LENGTH_SHORT).show();
                        }
                        break;
                        case 10: {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
                            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(browserIntent);
                        }
                        break;
                    }
                } else {
                    deletePost(pid, imageURL);
                }
            }
        });
    }

    private void deletePost(final String pid, String imageURL) {
        StorageReference pictureReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageURL);
        pictureReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Image delete complete, delete entry from database
                Query find = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("postKey").equalTo(pid);
                find.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            // Remove when postKey matches
                            data.getRef().removeValue();
                        }
                        Toast.makeText(mContext, "Deleted successfully. ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView viewDesc;
        ImageView postImage;
        ImageView post_userImage;
        TextView userName;
        TextView datetime;
        Button delete;
        TextView location;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            viewDesc = itemView.findViewById(R.id.postDesc);
            postImage = itemView.findViewById(R.id.postImage);
            post_userImage = itemView.findViewById(R.id.post_uImage);
            userName = itemView.findViewById(R.id.postUsername);
            datetime = itemView.findViewById(R.id.posttimeView);
            delete = itemView.findViewById(R.id.post_deleteButton);
            location = itemView.findViewById(R.id.post_locationtext);

        }

    }


}
