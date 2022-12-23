package com.example.bekind_v2.Utilities;

import android.content.Context;
import android.util.Log;
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

import com.example.bekind_v2.DataLayer.UserDatabaseRepository;
import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.R;
import com.example.bekind_v2.DataLayer.ProposalRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class ProposalRecyclerViewAdapter extends RecyclerView.Adapter<ProposalRecyclerViewAdapter.MyViewHolder> {
    private ProposalsViewModel proposalsViewModel;
    ArrayList<ProposalRepository.Proposal> proposals;
    Context context;
    Types type;
    private final MyCallback<Boolean> myCallback;

    public ProposalRecyclerViewAdapter(ArrayList<ProposalRepository.Proposal> proposals, Context context, Types type, MyCallback<Boolean> myCallback) {
        this.proposals = proposals;
        this.context = context;
        this.type = type;
        this.myCallback = myCallback;
    }

    @NonNull
    @Override
    public ProposalRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_element, parent, false);

        return new ProposalRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProposalRecyclerViewAdapter.MyViewHolder holder, int position) {
        String documentId = proposals.get(position).getId();
        String userId = UserManager.getUserId();

        ProposalRepository.Proposal proposal = proposals.get(position);
        String pubId = proposal.getPublisherID();
        UserManager.getUser(pubId, new MyCallback<UserDatabaseRepository.User>() {
            @Override
            public void onCallback(UserDatabaseRepository.User result) {
                holder.proposalPublisher.setText(result.getName()+" "+result.getSurname());
                holder.proposalTitle.setText(proposal.getTitle());
                holder.proposalBody.setText(proposal.getBody());
                if(proposal.getMaxParticipants() > 1)
                    holder.proposalParticipants.setText("N. partecipanti attuali: "+proposal.getAcceptersID().size()+"/"+proposal.getMaxParticipants());
                else
                    holder.proposalParticipants.setText("");
                Date date = proposal.getExpiringDate();
                LocalDateTime expiring_date_time = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
                holder.expiringHour.setText((String.format("%02d", expiring_date_time.getHour()+1)) + ":" + (String.format("%02d", expiring_date_time.getMinute())));
                holder.expiringDate.setText((String.format("%02d", expiring_date_time.getDayOfMonth())) + "/" + (String.format("%02d", expiring_date_time.getMonthValue())) + "/" + expiring_date_time.getYear());
            }
        });

       ConstraintLayout constraintLayout = holder.itemView.findViewById(R.id.proposal);

        switch(type){
            case PROPOSED: constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageButton delete = holder.itemView.findViewById(R.id.delete_button),
                                edit = holder.itemView.findViewById(R.id.edit_button),
                                confirm = holder.itemView.findViewById(R.id.confirm_button);
                    LinearLayout linearLayout = holder.itemView.findViewById(R.id.buttons_container_recycler_proposed);

                    if(linearLayout.getVisibility() == View.GONE)
                        linearLayout.setVisibility(View.VISIBLE);
                    else
                        linearLayout.setVisibility(View.GONE);

                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ProposalRepository.deleteProposal(documentId, myCallback);
                            Toast.makeText(context, "ELIMINA ATTIVITA'", Toast.LENGTH_SHORT).show();
                        }
                    });

                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //ProposalRepository.editProposal();
                            //Toast.makeText(context, "MODIFICA ATTIVITA'", Toast.LENGTH_SHORT).show();
                        }
                    });

                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ProposalRepository.deleteProposal(documentId);
                            Toast.makeText(context, "CONFERMA FINE ATTIVITA'", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }); break;
            case ACCEPTED: constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("ACCEPTED", "drop down");
                    ImageButton reject = holder.itemView.findViewById(R.id.reject_button);
                    LinearLayout linearLayout = holder.itemView.findViewById(R.id.buttons_container_recycler_accepted);

                    if(linearLayout.getVisibility() == View.GONE)
                        linearLayout.setVisibility(View.VISIBLE);
                    else
                        linearLayout.setVisibility(View.GONE);

                    reject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ProposalRepository.rejectProposal(documentId, userId, myCallback);
                        }
                    });
                }
            }); break;
            case AVAILABLE: constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageButton accept = holder.itemView.findViewById(R.id.accept_button),
                                flag = holder.itemView.findViewById(R.id.flag_button),
                                map = holder.itemView.findViewById(R.id.map_button);
                    LinearLayout linearLayout = holder.itemView.findViewById(R.id.buttons_container_recycler_available);

                    if(linearLayout.getVisibility() == View.GONE)
                        linearLayout.setVisibility(View.VISIBLE);
                    else
                        linearLayout.setVisibility(View.GONE);

                    accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ProposalRepository.acceptProposal(documentId, userId, myCallback);
                        }
                    });

                    flag.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO: evolution flag
                            Toast.makeText(context, "SEGNALA ATTIVITA'", Toast.LENGTH_LONG).show();
                        }
                    });

                    map.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO: evolution geo-localization
                            Toast.makeText(context, "MAPPA ATTIVITA'", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }); break;
        }
    }

    @Override
    public int getItemCount() {
        return proposals.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfilePic;
        TextView proposalPublisher, proposalTitle, proposalBody, proposalParticipants, expiringHour, expiringDate;
        ConstraintLayout constraintLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userProfilePic = itemView.findViewById(R.id.user_profile_pic);
            proposalPublisher = itemView.findViewById(R.id.proposal_publisher);
            proposalTitle = itemView.findViewById(R.id.proposal_title);
            proposalBody = itemView.findViewById(R.id.proposal_body);
            proposalParticipants = itemView.findViewById(R.id.proposal_participants);
            expiringHour = itemView.findViewById(R.id.expiring_hour);
            expiringDate = itemView.findViewById(R.id.expiring_date);
            constraintLayout = itemView.findViewById(R.id.proposal);
        }
    }
}
