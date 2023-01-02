package com.example.bekind_v2.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bekind_v2.DataLayer.PostRepository;
import com.example.bekind_v2.DataLayer.UserDatabaseRepository;
import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.R;
import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.UILayer.ui.available.AvailableViewModel;
import com.example.bekind_v2.UILayer.ui.dashboard.DashboardViewModel;
import com.example.bekind_v2.UILayer.ui.home.HomeViewModel;
import com.example.bekind_v2.UILayer.ui.profile.ProfileViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class ProposalRecyclerViewAdapter extends RecyclerView.Adapter<ProposalRecyclerViewAdapter.MyViewHolder> {
    private ProposalsViewModel proposalsViewModel;
    ArrayList<ProposalRepository.Proposal> proposals;
    Context context;
    Types type;

    public ProposalRecyclerViewAdapter(ArrayList<ProposalRepository.Proposal> proposals, Context context, Types type) {
        this.proposals = proposals;
        this.context = context;
        this.type = type;
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
                            if(proposal.getRepublishTypes() == RepublishTypes.NEVER) {
                                ProposalRepository.deleteProposal(documentId, new MyCallback<Boolean>() {
                                    @Override
                                    public void onCallback(Boolean result) {
                                        if (result) {
                                            Toast.makeText(context, "Attività cancellata correttamente", Toast.LENGTH_SHORT).show();
                                            Utilities.getProposals(Utilities.day, UserManager.getUserId(), ProfileViewModel.proposedFilters, Types.PROPOSED);
                                        } else
                                            Toast.makeText(context, "Impossibile cancellare attività", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else{
                                Dialog dialog = new Dialog(context);
                                CheckBox singleProposal, totalProposal;
                                Button closeBtn, confirmBtn;
                                dialog.setContentView(R.layout.delete_periodic_proposal);
                                dialog.setCanceledOnTouchOutside(false);

                                singleProposal = dialog.findViewById(R.id.single_proposal_checkbox);
                                totalProposal = dialog.findViewById(R.id.total_proposal_checkbox);

                                closeBtn = dialog.findViewById(R.id.close_btn);
                                confirmBtn = dialog.findViewById(R.id.confirm_btn);

                                closeBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });

                                confirmBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(!singleProposal.isChecked() && !totalProposal.isChecked())
                                            Toast.makeText(context, "Prima di confermare scegli una delle opzioni", Toast.LENGTH_SHORT).show();
                                        else{
                                            if(totalProposal.isChecked()){
                                                ProposalRepository.deleteProposal(documentId, new MyCallback<Boolean>() {
                                                    @Override
                                                    public void onCallback(Boolean result) {
                                                        if (result) {
                                                            Toast.makeText(context, "Attività cancellata correttamente", Toast.LENGTH_SHORT).show();
                                                            Utilities.getProposals(Utilities.day, UserManager.getUserId(), ProfileViewModel.proposedFilters, Types.PROPOSED);
                                                        } else
                                                            Toast.makeText(context, "Impossibile cancellare attività", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                            else{
                                                ProposalRepository.updatePeriodicProposal(proposal, new MyCallback<Boolean>() {
                                                    @Override
                                                    public void onCallback(Boolean result) {
                                                        if (result) {
                                                            Toast.makeText(context, "Attività cancellata correttamente", Toast.LENGTH_SHORT).show();
                                                            Utilities.getProposals(Utilities.day, UserManager.getUserId(), ProfileViewModel.proposedFilters, Types.PROPOSED);
                                                        } else
                                                            Toast.makeText(context, "Impossibile cancellata attività", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                            dialog.dismiss();
                                        }
                                    }
                                });
                            dialog.show();
                            }

                        }
                    });

                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("CLICK", "I clicked the dit button");
                            Dialog firstDialog = new Dialog(context);
                            firstDialog.setContentView(R.layout.add_proposal_popup);
                            firstDialog.setCanceledOnTouchOutside(false);
                            Dialog secondDialog = new Dialog(context);
                            secondDialog.setContentView(R.layout.add_proposal_popup2);
                            secondDialog.setCanceledOnTouchOutside(false);
                            Date date = proposal.getExpiringDate();
                            LocalDateTime expiringDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
                            ArrayList<String> filters = proposal.getFilters(), newFilters = new ArrayList<>();

                            TextView text = firstDialog.findViewById(R.id.new_activity_header);
                            TextInputEditText activityTitle = firstDialog.findViewById(R.id.activity_title), activityBody = firstDialog.findViewById(R.id.activity_body),
                                              maxPartecipants = secondDialog.findViewById(R.id.activity_maxparticipants);
                            DatePicker expiringDate = firstDialog.findViewById(R.id.date_picker);
                            TimePicker expiringHour = firstDialog.findViewById(R.id.time_picker);
                            Chip shoppingChip = firstDialog.findViewById(R.id.shopping_chip_popup), houseworkChip = firstDialog.findViewById(R.id.houseworks_chip_popup),
                                    cleaningChip = firstDialog.findViewById(R.id.cleaning_chip_popup), transportChip = firstDialog.findViewById(R.id.transport_chip_popup),
                                    randomChip = firstDialog.findViewById(R.id.random_chip_popup);
                            Button closeButton = firstDialog.findViewById(R.id.close_btn) , continueButton = firstDialog.findViewById(R.id.continue_btn),
                                    publishButton = secondDialog.findViewById(R.id.publish_btn), backButton = secondDialog.findViewById(R.id.back_btn);
                            CheckBox groupCheckbox = secondDialog.findViewById(R.id.group_checkbox), periodicCheckbox = secondDialog.findViewById(R.id.periodic_checkbox);
                            ListView periodicChoices = secondDialog.findViewById(R.id.periodic_choices);

                            text.setText("Modifica attività");
                            activityTitle.setText(proposal.getTitle());
                            activityBody.setText(proposal.getBody());
                            expiringDate.updateDate(expiringDateTime.getYear(), expiringDateTime.getMonthValue() - 1, expiringDateTime.getDayOfMonth());
                            expiringHour.setIs24HourView(true);
                            expiringHour.setHour(expiringDateTime.getHour() + 1);
                            expiringHour.setMinute(expiringDateTime.getMinute());
                            if(filters.contains(shoppingChip.getText().toString()))
                                shoppingChip.setChecked(true);
                            if(filters.contains(houseworkChip.getText().toString()))
                                houseworkChip.setChecked(true);
                            if(filters.contains(cleaningChip.getText().toString()))
                                cleaningChip.setChecked(true);
                            if(filters.contains(transportChip.getText().toString()))
                                transportChip.setChecked(true);
                            if(filters.contains(randomChip.getText().toString()))
                                randomChip.setChecked(true);

                            if(proposal.getMaxParticipants() != 1){
                                groupCheckbox.setChecked(true);
                                maxPartecipants.setVisibility(View.VISIBLE);
                                maxPartecipants.setText(Integer.toString(proposal.getMaxParticipants()));
                            }

                            ArrayList<String> types = new ArrayList<>(Arrays.asList(RepublishTypes.DAILY.getNameToDisplay(), RepublishTypes.WEEKLY.getNameToDisplay(), RepublishTypes.MONTHLY.getNameToDisplay(), RepublishTypes.ANNUALLY.getNameToDisplay()));
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_single_choice, types);
                            periodicChoices.setAdapter(adapter);
                            RepublishTypes[] choice = new RepublishTypes[1];
                            choice[0] = RepublishTypes.NEVER;

                            if(proposal.getRepublishTypes() != RepublishTypes.NEVER){
                                periodicCheckbox.setChecked(true);
                                periodicChoices.setVisibility(View.VISIBLE);
                                switch(proposal.getRepublishTypes()){
                                    case DAILY: periodicChoices.setItemChecked(0, true); break;
                                    case WEEKLY: periodicChoices.setItemChecked(1, true); break;
                                    case MONTHLY: periodicChoices.setItemChecked(2, true); break;
                                    case ANNUALLY: periodicChoices.setItemChecked(3, true); break;
                                }
                            }

                            periodicChoices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    choice[0] = RepublishTypes.getValue((String) periodicChoices.getItemAtPosition(i));
                                }
                            });

                            groupCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if(isChecked) {
                                        maxPartecipants.setVisibility(View.VISIBLE);
                                        maxPartecipants.setText(Integer.toString(proposal.getMaxParticipants()));
                                        Log.e("MAX GROUP", maxPartecipants.getText().toString());

                                    }else{
                                        maxPartecipants.setVisibility(View.GONE);
                                        maxPartecipants.setText("");
                                        Log.e("MAX NO GROUP", maxPartecipants.getText().toString());
                                    }
                                }
                            });

                            periodicCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if(isChecked)
                                        periodicChoices.setVisibility(View.VISIBLE);
                                    else
                                        periodicChoices.setVisibility(View.GONE);
                                }
                            });

                            firstDialog.show();

                            closeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    firstDialog.dismiss();
                                }
                            });

                            continueButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    firstDialog.dismiss();
                                    secondDialog.show();
                                }
                            });

                            backButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    secondDialog.dismiss();
                                    firstDialog.show();

                                }
                            });

                            publishButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(shoppingChip.isChecked())
                                        newFilters.add(shoppingChip.getText().toString());
                                    if(houseworkChip.isChecked())
                                        newFilters.add(houseworkChip.getText().toString());
                                    if(cleaningChip.isChecked())
                                        newFilters.add(cleaningChip.getText().toString());
                                    if(transportChip.isChecked())
                                        newFilters.add(transportChip.getText().toString());
                                    if(randomChip.isChecked())
                                        newFilters.add(randomChip.getText().toString());

                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(expiringDate.getYear(), expiringDate.getMonth(), expiringDate.getDayOfMonth(), expiringHour.getHour() - 1, expiringHour.getMinute());
                                    int maxPartecipantsNumber;

                                    if(!groupCheckbox.isChecked())
                                        maxPartecipantsNumber = 1;
                                    else {
                                        Log.e("HERE", "sono qui anche se non dovrei");
                                        maxPartecipantsNumber = Integer.parseInt(maxPartecipants.getText().toString());
                                    }

                                    ProposalRepository.editProposal(documentId, activityTitle.getText().toString().trim(), activityBody.getText().toString().trim(), calendar.getTime(), proposal.getPublishingDate(), newFilters, maxPartecipantsNumber, choice[0], new MyCallback<Boolean>() {
                                        @Override
                                        public void onCallback(Boolean result) {

                                            if(result){
                                                Toast.makeText(context, "Attività modificata correttamente", Toast.LENGTH_SHORT).show();
                                                Utilities.getProposals(Utilities.day, UserManager.getUserId(), ProfileViewModel.proposedFilters, Types.PROPOSED);
                                            }
                                            else
                                                Toast.makeText(context, "Impossibile modificare attività", Toast.LENGTH_SHORT).show();
                                            secondDialog.dismiss();
                                        }
                                    });
                                }
                            });
                            //ProposalRepository.editProposal();
                            //Toast.makeText(context, "MODIFICA ATTIVITA'", Toast.LENGTH_SHORT).show();
                        }
                    });

                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(proposal.getRepublishTypes() == RepublishTypes.NEVER) {
                                ProposalRepository.deleteProposal(documentId, new MyCallback<Boolean>() {
                                    @Override
                                    public void onCallback(Boolean result) {
                                        if (result) {
                                            Toast.makeText(context, "Attività terminata correttamente", Toast.LENGTH_SHORT).show();
                                            Utilities.getProposals(Utilities.day, UserManager.getUserId(), ProfileViewModel.proposedFilters, Types.PROPOSED);
                                        } else
                                            Toast.makeText(context, "Impossibile terminare attività", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else{
                                ProposalRepository.updatePeriodicProposal(proposal, new MyCallback<Boolean>() {
                                    @Override
                                    public void onCallback(Boolean result) {
                                        if (result) {
                                            Toast.makeText(context, "Attività terminata correttamente", Toast.LENGTH_SHORT).show();
                                            Utilities.getProposals(Utilities.day, UserManager.getUserId(), ProfileViewModel.proposedFilters, Types.PROPOSED);
                                        } else
                                            Toast.makeText(context, "Impossibile terminare attività", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
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
                            ProposalRepository.rejectProposal(documentId, userId, new MyCallback<Boolean>() {
                                @Override
                                public void onCallback(Boolean result) {
                                    if(result) {
                                        Toast.makeText(context, "Ritiro dall'attività avvenuto correttamente", Toast.LENGTH_SHORT).show();
                                        Utilities.getProposals(Utilities.day, UserManager.getUserId(), HomeViewModel.filters, Types.ACCEPTED);
                                    }else
                                        Toast.makeText(context, "Errore nel ritiro dall'attività", Toast.LENGTH_SHORT).show();
                                }
                            });
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

                    if(linearLayout.getVisibility() == View.GONE) {
                        linearLayout.setVisibility(View.VISIBLE);
                        ProposalRepository.hasUserFlagged(documentId, userId, new MyCallback<Boolean>() {
                            @Override
                            public void onCallback(Boolean result) {
                                if(result)
                                    flag.setImageResource(R.drawable.ic_flag_filled);
                            }
                        });
                    }else
                        linearLayout.setVisibility(View.GONE);

                    accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ProposalRepository.acceptProposal(documentId, userId, new MyCallback<Boolean>() {
                                @Override
                                public void onCallback(Boolean result) {
                                    if(result) {
                                        Toast.makeText(context, "Attività accettata correttamente", Toast.LENGTH_SHORT).show();
                                        Utilities.getProposals(Utilities.day, UserManager.getUserId(), AvailableViewModel.filters, Types.AVAILABLE);
                                    }else
                                        Toast.makeText(context, "Errore nell'accettazione dell'attività", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                    flag.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(flag.getTag().equals("to_flag"))
                                ProposalRepository.addFlagUser(documentId, pubId, new MyCallback<Boolean>() {
                                    @Override
                                    public void onCallback(Boolean result) {
                                        if (result) {
                                            Toast.makeText(context, "Attività segnalato con successo.", Toast.LENGTH_SHORT).show();
                                            flag.setImageResource(R.drawable.ic_flag_filled);
                                            flag.setTag("flagged");
                                        } else {
                                            Toast.makeText(context, "Impossibile segnalare l'attività.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            else
                                ProposalRepository.deleteFlagUser(documentId, pubId, new MyCallback<Boolean>() {
                                    @Override
                                    public void onCallback(Boolean result) {
                                        if (result) {
                                            Toast.makeText(context, "Attività non segnalata con successo.", Toast.LENGTH_SHORT).show();
                                            flag.setImageResource(R.drawable.ic_flag);
                                            flag.setTag("to_flag");
                                        } else {
                                            Toast.makeText(context, "Impossibile non segnalare l'attività.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
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

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userProfilePic = itemView.findViewById(R.id.user_profile_pic);
            proposalPublisher = itemView.findViewById(R.id.proposal_publisher);
            proposalTitle = itemView.findViewById(R.id.proposal_title);
            proposalBody = itemView.findViewById(R.id.proposal_body);
            proposalParticipants = itemView.findViewById(R.id.proposal_participants);
            expiringHour = itemView.findViewById(R.id.expiring_hour);
            expiringDate = itemView.findViewById(R.id.expiring_date);
        }
    }
}
