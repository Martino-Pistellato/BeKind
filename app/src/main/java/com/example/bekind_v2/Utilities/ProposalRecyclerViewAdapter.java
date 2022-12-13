package com.example.bekind_v2.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ProposalRecyclerViewAdapter extends RecyclerView.Adapter<ProposalRecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ProposalRepository.Proposal> proposals = new ArrayList<>();
    private String class_name = "";

    public ProposalRecyclerViewAdapter(Context context, ArrayList<ProposalRepository.Proposal> activities) {
        this.context = context;
        this.proposals = activities;
    }

    public ProposalRecyclerViewAdapter(Context context, ArrayList<ProposalRepository.Proposal> activities, String class_name) {
        this.context = context;
        this.proposals = activities;
        this.class_name = class_name;
    }

    @NonNull
    @Override
    public ProposalRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("TEST", "sono dentro");
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item, parent, false);

        return new ProposalRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProposalRecyclerViewAdapter.MyViewHolder holder, int position) {
        //Query user_profile_pics = FirebaseFirestore.getInstance().collection("Users").whereEqualTo("userID", proposals.get(position).getPublisherID());
        String documentID = proposals.get(position).getId();

        holder.proposal_title.setText(proposals.get(position).getTitle());
        holder.proposal_body.setText(proposals.get(position).getBody());
        Date date = proposals.get(position).getExpiringDate();
        LocalDateTime expiring_date_time = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        holder.expiring_hour.setText((String.format("%02d", expiring_date_time.getHour()+1)) + ":" + (String.format("%02d", expiring_date_time.getMinute())));
        holder.expiring_date.setText((String.format("%02d", expiring_date_time.getDayOfMonth())) + "/" + (String.format("%02d", expiring_date_time.getMonthValue())) + "/" + expiring_date_time.getYear());
        ConstraintLayout constraintLayout = holder.itemView.findViewById(R.id.proposal);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(context.getClass().equals(ActivitiesActivity.class)){
                    LinearLayout buttons = holder.itemView.findViewById(R.id.buttons_container_recycler_free);
                    buttons.setVisibility(buttons.getVisibility()==View.GONE? View.VISIBLE : View.GONE);

                    ImageButton accept_button, map_button, flag_button;
                    accept_button = holder.itemView.findViewById(R.id.accept_button);
                    map_button = holder.itemView.findViewById(R.id.map_button);
                    flag_button = holder.itemView.findViewById(R.id.flag_button);

                    accept_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid(); //gets the current user ID
                            FirebaseFirestore.getInstance().collection("Proposals").document(documentID).update("accepterID", userID);
                            proposals.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            Toast.makeText(context, "Attività accettata correttamente", Toast.LENGTH_SHORT).show();
                            buttons.setVisibility(View.GONE);
                        }
                    });
                }
                else if(class_name.equals("ProposalFragment")){
                    LinearLayout buttons = holder.itemView.findViewById(R.id.buttons_container_recycler_myproposal);
                    buttons.setVisibility(buttons.getVisibility()==View.GONE? View.VISIBLE : View.GONE);

                    ImageButton cancel_button, change_button, confirm_button;
                    cancel_button = holder.itemView.findViewById(R.id.cancel_button);
                    change_button = holder.itemView.findViewById(R.id.change_button);
                    confirm_button = holder.itemView.findViewById(R.id.confirm_button);

                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showDialog(holder, documentID, "cancel", buttons);
                            //buttons.setVisibility(View.GONE);
                        }
                    });

                    confirm_button.setOnClickListener(new View.OnClickListener() {
                      @Override
                          public void onClick(View v) {
                          showDialog(holder, documentID, "confirm", buttons);
                         // buttons.setVisibility(View.GONE);
                      }
                    });

                    change_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                            public void onClick(View v) {
                            showDialog(holder, documentID, "change", buttons);
                            //buttons.setVisibility(View.GONE);

                        }
                    });
                }
                else if(class_name.equals("AcceptedFragment")){
                    LinearLayout buttons = holder.itemView.findViewById(R.id.buttons_container_recycler_accepted);
                    buttons.setVisibility(buttons.getVisibility()==View.GONE? View.VISIBLE : View.GONE);

                    ImageButton reject_button = holder.itemView.findViewById(R.id.reject_button);

                    reject_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog(holder, documentID, "reject", buttons);
                           // buttons.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }


    private void showDialog(@NonNull ProposalRecyclerViewAdapter.MyViewHolder holder, String documentID, String Action, LinearLayout buttons) {
        Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);

        if(Action == "change"){
            TextInputEditText title, body;
            DatePicker expiringDate;
            TimePicker expiringHour;
            TextView header;

            dialog.setContentView(R.layout.new_activity_dialog); //set content of dialog (look in layout folder for new_activity_dialog file)

            header = dialog.findViewById(R.id.new_activity_header);
            header.setText("Modifica attvità");

            Button closeBtn = dialog.findViewById(R.id.close_btn); //button for closing dialog
            Button publishBtn = dialog.findViewById(R.id.publish_btn);

            title = dialog.findViewById(R.id.activity_title);
            body = dialog.findViewById(R.id.activity_body);
            expiringDate = dialog.findViewById(R.id.date_picker);
            expiringHour = dialog.findViewById(R.id.time_picker);

            title.setText(holder.proposal_title.getText());
            body.setText(holder.proposal_body.getText());

            expiringHour.setIs24HourView(true);

            Date expD = proposals.get(holder.getAdapterPosition()).getExpiringDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(expD);

            expiringHour.setHour(calendar.get(Calendar.HOUR_OF_DAY)+1);
            expiringHour.setMinute(calendar.get(Calendar.MINUTE));

            expiringDate.updateDate(calendar.YEAR,calendar.MONTH,calendar.DAY_OF_MONTH);
            expiringDate.setMinDate(System.currentTimeMillis() - 1000);

            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss(); //close dialog
                }
            });


            publishBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    calendar.set(expiringDate.getYear(),expiringDate.getMonth(), expiringDate.getDayOfMonth(), expiringHour.getHour() - 1, expiringHour.getMinute());
                    Date activity_expiringDate = calendar.getTime();

                    String activity_title = title.getText().toString().trim();
                    String activity_body = body.getText().toString().trim();

                    if(activity_title.isEmpty()){
                        title.setError("Questo campo non può essere vuoto");
                        title.requestFocus();
                    }
                    if(activity_body.isEmpty()){
                        body.setError("Questo campo non può essere vuoto");
                        body.requestFocus();
                    }
                    else{
                        FirebaseFirestore.getInstance().collection("Proposals").document(documentID).update("title", activity_title, "body", activity_body, "expiringDate", activity_expiringDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Attività modificata correttamente", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Errore nella modifica dall'attività", Toast.LENGTH_SHORT).show();
                            }
                        });

                        proposals.get(holder.getAdapterPosition()).setTitle(activity_title);
                        proposals.get(holder.getAdapterPosition()).setBody(activity_body);
                        proposals.get(holder.getAdapterPosition()).setExpiringDate(activity_expiringDate);

                        notifyItemChanged(holder.getAdapterPosition());
                        buttons.setVisibility(View.GONE);

                        dialog.dismiss();
                    }
                }
            });
        }
        else{
            TextView text;
            Button confirm_button, back_button;

            dialog.setContentView(R.layout.cancel_confirm_activity);


            text = dialog.findViewById(R.id.text_to_display);
            confirm_button=dialog.findViewById(R.id.publish_btn);
            back_button = dialog.findViewById(R.id.close_btn);

            back_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            if(Action.equals("cancel")){
                text.setText("Sei sicuro di voler cancellare questa attività? Così facendo nessuno potrà svolgerla!");
                confirm_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO: time constraints?
                        FirebaseFirestore.getInstance().collection("Proposals").document(documentID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Attività cancellata correttamente", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Errore nella cancellazione dell'attività", Toast.LENGTH_SHORT).show();
                            }
                        });
                        proposals.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        buttons.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });
            }
            else if(Action.equals("confirm")){
                text.setText("Confermando questa attività confermi che essa è stata svolta interamente e correttamente da chi l'ha presa in carico");
                confirm_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO: time constraints?
                        FirebaseFirestore.getInstance().collection("Proposals").document(documentID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Attività evasa correttamente", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Errore nell'evasione dell'attività", Toast.LENGTH_SHORT).show();
                            }
                        });
                        proposals.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        buttons.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });
            }
            else{
                text.setText("Vuoi davvero ritirarti da questa attività?");
                confirm_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO: add time constraint
                        FirebaseFirestore.getInstance().collection("Proposals").document(documentID).update("accepterID", null).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Attività rifiutata correttamente", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Errore nel ritiro dall'attività", Toast.LENGTH_SHORT).show();
                            }
                        });
                        proposals.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        buttons.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });
            }
        }
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return proposals.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView user_profile_pic;
        TextView proposal_title, proposal_body, expiring_hour, expiring_date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            user_profile_pic = itemView.findViewById(R.id.user_profile_pic);
            proposal_title = itemView.findViewById(R.id.proposal_title);
            proposal_body = itemView.findViewById(R.id.proposal_body);
            expiring_hour = itemView.findViewById(R.id.expiring_hour);
            expiring_date = itemView.findViewById(R.id.expiring_date);
        }
    }
}

