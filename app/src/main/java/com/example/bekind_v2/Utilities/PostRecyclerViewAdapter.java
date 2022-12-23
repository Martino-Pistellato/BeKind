package com.example.bekind_v2.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bekind_v2.DataLayer.PostRepository;
import com.example.bekind_v2.DataLayer.UserDatabaseRepository;
import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.R;

import java.util.ArrayList;

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.MyViewHolder> {
    ArrayList<PostRepository.Post> posts;
    Context context;

    public PostRecyclerViewAdapter(ArrayList<PostRepository.Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public PostRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_post_element, parent, false);

        return new PostRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostRecyclerViewAdapter.MyViewHolder holder, int position) {

        PostRepository.Post post = posts.get(position);
        String pubId = post.getPublisherID();
        UserManager.getUser(pubId, new MyCallback<UserDatabaseRepository.User>() {
            @Override
            public void onCallback(UserDatabaseRepository.User result) {
                holder.proposal_publisher.setText(result.getName()+" "+result.getSurname());
                holder.proposal_title.setText(post.getTitle());
                holder.proposal_body.setText(post.getBody());
            }
        });

        ConstraintLayout constraintLayout = holder.itemView.findViewById(R.id.post);
    }

    @Override
    public int getItemCount() {
        return this.posts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView user_profile_pic;
        TextView proposal_title, proposal_publisher, proposal_body;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            user_profile_pic = itemView.findViewById(R.id.user_profile_pic);
            proposal_publisher = itemView.findViewById(R.id.post_publisher);
            proposal_title = itemView.findViewById(R.id.post_title);
            proposal_body = itemView.findViewById(R.id.post_body);
        }
    }
}
