package com.example.bekind_v2.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.bekind_v2.UILayer.ui.home.HomeViewModel;
import com.example.bekind_v2.UILayer.ui.profile.ProfileViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

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
        String postId = posts.get(position).getId();
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
                                  PostRepository.deletePost(postId, new MyCallback<Boolean>() {
                                      @Override
                                      public void onCallback(Boolean result) {
                                          if(result){
                                              Toast.makeText(context, "Post cancellato correttamente", Toast.LENGTH_SHORT).show();
                                              Utilities.getPosts(Utilities.day, UserManager.getUserId(), ProfileViewModel.postsFilters, PostTypes.MYPOSTS);
                                          }
                                          else
                                              Toast.makeText(context, "Impossibile cancellare post", Toast.LENGTH_SHORT).show();
                                      }
                                  });
                              }
                          });

                          edit.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  Dialog dialog = new Dialog(context);
                                  dialog.setContentView(R.layout.add_post_popup);
                                  dialog.setCanceledOnTouchOutside(false);

                                  ArrayList<String> filters = post.getFilters(), newFilters = new ArrayList<>();
                                  TextView text = dialog.findViewById(R.id.new_post_header);
                                  TextInputEditText postTitle = dialog.findViewById(R.id.post_title), postBody = dialog.findViewById(R.id.post_body);
                                  Chip eventChip = dialog.findViewById(R.id.event_chip_popup), utilitiesChip = dialog.findViewById(R.id.utilities_chip_popup), 
                                          animalChip = dialog.findViewById(R.id.animal_chip_popup), transportChip = dialog.findViewById(R.id.transport_chip_popup), 
                                          criminalChip = dialog.findViewById(R.id.criminal_chip_popup), randomChip = dialog.findViewById(R.id.random_chip_popup);
                                  Button closeButton = dialog.findViewById(R.id.close_btn), publishButton = dialog.findViewById(R.id.publish_btn);
                                  text.setText("Modifica post");
                                  postTitle.setText(post.getTitle());
                                  postBody.setText(post.getBody());
                                  if(filters.contains(eventChip.getText().toString()))
                                      eventChip.setChecked(true);
                                  if(filters.contains(utilitiesChip.getText().toString()))
                                      utilitiesChip.setChecked(true);
                                  if(filters.contains(animalChip.getText().toString()))
                                      animalChip.setChecked(true);
                                  if(filters.contains(transportChip.getText().toString()))
                                      transportChip.setChecked(true);
                                  if(filters.contains(criminalChip.getText().toString()))
                                      criminalChip.setChecked(true);
                                  if(filters.contains(randomChip.getText().toString()))
                                      randomChip.setChecked(true);

                                  dialog.show();

                                  closeButton.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          dialog.dismiss();
                                      }
                                  });

                                  publishButton.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          if(eventChip.isChecked())
                                              newFilters.add(eventChip.getText().toString());
                                          if(utilitiesChip.isChecked())
                                              newFilters.add(utilitiesChip.getText().toString());
                                          if(animalChip.isChecked())
                                              newFilters.add(animalChip.getText().toString());
                                          if(transportChip.isChecked())
                                              newFilters.add(transportChip.getText().toString());
                                          if(criminalChip.isChecked())
                                              newFilters.add(criminalChip.getText().toString());
                                          if(randomChip.isChecked())
                                              newFilters.add(randomChip.getText().toString());

                                          PostRepository.editPost(postId, postTitle.getText().toString().trim(), postBody.getText().toString().trim(), newFilters, new MyCallback<Boolean>() {
                                              @Override
                                              public void onCallback(Boolean result) {
                                                  if (result){
                                                      Toast.makeText(context, "Post aggiornato correttamente", Toast.LENGTH_SHORT).show();
                                                    Utilities.getPosts(Utilities.day, UserManager.getUserId(), ProfileViewModel.postsFilters, PostTypes.MYPOSTS);
                                                  }else
                                                      Toast.makeText(context, "Impossibile modificare post", Toast.LENGTH_SHORT).show();
                                              }
                                          });

                                          dialog.dismiss();
                                      }
                                  });
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
