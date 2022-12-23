package com.example.bekind_v2.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bekind_v2.DataLayer.PostRepository;
import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.DataLayer.UserDatabaseRepository;
import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.R;

import java.util.ArrayList;

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.MyViewHolder> {
    ArrayList<PostRepository.Post> posts;
    Context context;
    PostTypes type;

    public PostRecyclerViewAdapter(ArrayList<PostRepository.Post> posts, Context context, PostTypes type) {
        this.posts = posts;
        this.context = context;
        this.type = type;
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

        switch(type){
            case MYPOSTS: constraintLayout.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          ImageButton delete = holder.itemView.findViewById(R.id.delete_button),
                                      edit = holder.itemView.findViewById(R.id.edit_button);

                          LinearLayout linearLayout = holder.itemView.findViewById(R.id.buttons_container_recycler_mypost);

                          if(linearLayout.getVisibility() == View.GONE)
                              linearLayout.setVisibility(View.VISIBLE);
                          else
                              linearLayout.setVisibility(View.GONE);

                          delete.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  Toast.makeText(context, "CANCELLAZIONE POST", Toast.LENGTH_SHORT).show();
                              }
                          });

                          edit.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  Toast.makeText(context, "MODIFICA POST", Toast.LENGTH_SHORT).show();
                              }
                          });
                      }
                  }); break;
            case OTHERSPOSTS: constraintLayout.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      ImageButton flag = holder.itemView.findViewById(R.id.flag_button);
                      LinearLayout linearLayout = holder.itemView.findViewById(R.id.buttons_container_recycler_otherpost);

                      if(linearLayout.getVisibility() == View.GONE)
                          linearLayout.setVisibility(View.VISIBLE);
                      else
                          linearLayout.setVisibility(View.GONE);

                      flag.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              Toast.makeText(context, "FLAG POST", Toast.LENGTH_SHORT).show();
                          }
                      });
                  }
            }); break;
        }
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
