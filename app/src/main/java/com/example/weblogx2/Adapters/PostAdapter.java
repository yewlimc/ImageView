package com.example.weblogx2.Adapters;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weblogx2.Models.Post;
import com.example.weblogx2.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context mContext;
    List<Post> mData;

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

        holder.viewDesc.setText(mData.get(position).getDescription());
        Picasso.get().load(mData.get(position).getImage()).into(holder.postImage);
        Picasso.get().load(mData.get(position).getUserImage()).into(holder.post_userImage);
        holder.userName.setText(mData.get(position).getUserName());
        holder.datetime.setText(mData.get(position).getDateTime());
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

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            viewDesc = itemView.findViewById(R.id.postDesc);
            postImage = itemView.findViewById(R.id.postImage);
            post_userImage = itemView.findViewById(R.id.post_uImage);
            userName = itemView.findViewById(R.id.postUsername);
            datetime = itemView.findViewById(R.id.timeView);


        }

    }



}
