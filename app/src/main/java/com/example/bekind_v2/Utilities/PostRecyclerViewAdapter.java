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

import com.bumptech.glide.Glide;
import com.example.bekind_v2.DataLayer.PostRepository;
import com.example.bekind_v2.DataLayer.UserDatabaseRepository;
import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.R;
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
        String userId = UserManager.getUserId();

        UserManager.getUser(pubId, new MyCallback<UserDatabaseRepository.User>() {
            @Override
            public void onCallback(UserDatabaseRepository.User result) {
                if(!result.getImage().isEmpty()) Glide.with(context).load(result.getImage()).into(holder.userProfilePic);

                if(type != PostTypes.MYPOSTS)
                    holder.postPublisher.setText(result.getName()+" "+result.getSurname());
                else
                    holder.postPublisher.setText("Tu");

                holder.postTitle.setText(post.getTitle());
                holder.postBody.setText(post.getBody());

                if(post.getPriority()) {holder.constraintLayout.setBackgroundResource(R.drawable.list_element_roundcorner_priority);}

                if(post.getUsersFlag().size() >= 1 && type == PostTypes.MYPOSTS){
                    holder.postPublisher.setVisibility(View.INVISIBLE);
                    holder.postFlagged.setText("Post segnalato");
                    holder.postFlagged.setVisibility(View.VISIBLE);
                    holder.constraintLayout.setBackgroundResource(R.drawable.list_element_roundcorner_red);
                }
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
                      ImageButton flag = holder.itemView.findViewById(R.id.flag_button), like = holder.itemView.findViewById(R.id.like_button);
                      LinearLayout linearLayout = holder.itemView.findViewById(R.id.buttons_container_recycler_otherpost);

                      if(linearLayout.getVisibility() == View.GONE) {
                          linearLayout.setVisibility(View.VISIBLE);

                          PostRepository.hasUserFlagged(postId, pubId, new MyCallback<Boolean>() {
                              @Override
                              public void onCallback(Boolean result) {
                                  if (result) {
                                      flag.setImageResource(R.drawable.ic_flag_filled);
                                      flag.setTag("flagged");
                                  }
                              }
                          });
                          
                          PostRepository.hasUserLiked(postId, pubId, new MyCallback<Boolean>() {
                              @Override
                              public void onCallback(Boolean result) {
                                  if(result) {
                                      like.setImageResource(R.drawable.ic_thumbsup_filled);
                                      like.setTag("thumbsup_filled");
                                  }
                              }
                          });
                      }else
                          linearLayout.setVisibility(View.GONE);

                      flag.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View view) {
                              if(flag.getTag().equals("to_flag"))
                                  PostRepository.addUserFlag(postId, userId, new MyCallback<Boolean>() {
                                      @Override
                                      public void onCallback(Boolean result) {
                                          if (result) {
                                              Toast.makeText(context, "Post segnalato con successo.", Toast.LENGTH_SHORT).show();
                                              flag.setImageResource(R.drawable.ic_flag_filled);
                                              flag.setTag("flagged");
                                          } else {
                                              Toast.makeText(context, "Impossibile segnalare il post.", Toast.LENGTH_SHORT).show();
                                          }
                                      }
                                  });
                              else
                                  PostRepository.deleteUserFlag(postId, userId, new MyCallback<Boolean>() {
                                      @Override
                                      public void onCallback(Boolean result) {
                                          if (result) {
                                              Toast.makeText(context, "Post non segnalato con successo.", Toast.LENGTH_SHORT).show();
                                              flag.setImageResource(R.drawable.ic_flag);
                                              flag.setTag("to_flag");
                                          } else {
                                              Toast.makeText(context, "Impossibile non segnalare il post.", Toast.LENGTH_SHORT).show();
                                          }
                                      }
                                  });
                          }
                      });

                      like.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View view) {
                              if(like.getTag().equals("thumbsup")){
                                  PostRepository.addUserLike(postId, pubId, new MyCallback<Boolean>() {
                                      @Override
                                      public void onCallback(Boolean result) {
                                          if (result) {
                                              Toast.makeText(context, "Post likkato con successo.", Toast.LENGTH_SHORT).show();
                                              like.setImageResource(R.drawable.ic_thumbsup_filled);
                                              like.setTag("thumbsup_filled");
                                          } else {
                                              Toast.makeText(context, "Impossibile likkare il post.", Toast.LENGTH_SHORT).show();
                                          }
                                      }
                                  });
                              }
                              else{
                                  PostRepository.deleteUserLike(postId, pubId, new MyCallback<Boolean>() {
                                      @Override
                                      public void onCallback(Boolean result) {
                                          if (result) {
                                              Toast.makeText(context, "Post unlikkato con successo.", Toast.LENGTH_SHORT).show();
                                              like.setImageResource(R.drawable.ic_thumbsup);
                                              like.setTag("thumbsup");
                                          } else {
                                              Toast.makeText(context, "Impossibile unlikkare il post.", Toast.LENGTH_SHORT).show();
                                          }
                                      }
                                  });
                              }
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
        ImageView userProfilePic;
        TextView postTitle, postPublisher, postBody, postFlagged;
        ConstraintLayout constraintLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userProfilePic = itemView.findViewById(R.id.post_user_pic);
            postPublisher = itemView.findViewById(R.id.post_publisher);
            postTitle = itemView.findViewById(R.id.post_title);
            postBody = itemView.findViewById(R.id.post_body);
            constraintLayout = itemView.findViewById(R.id.outer_constraintlayout);
            postFlagged = itemView.findViewById(R.id.post_flagged);
        }
    }
}
