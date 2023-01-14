package com.example.bekind_v2.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.DataLayer.UserDatabaseRepository;
import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.ui.available.AvailableViewModel;
import com.example.bekind_v2.UILayer.ui.home.HomeViewModel;
import com.example.bekind_v2.UILayer.ui.profile.ProfileViewModel;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class ProposalRecyclerViewAdapter extends RecyclerView.Adapter<ProposalRecyclerViewAdapter.MyViewHolder> {
    ArrayList<ProposalRepository.Proposal> proposals;
    Context context;
    Types type;
    MapViewModel mapViewModel;
    SupportMapFragment mapFragment;
    Dialog mapDialog;
    FragmentActivity activity;

    public ProposalRecyclerViewAdapter(MapViewModel mapViewModel, Dialog mapdialog, SupportMapFragment mapFragment, ArrayList<ProposalRepository.Proposal> proposals, Context context, FragmentActivity activity, Types type) {
        this.proposals = proposals;
        this.context = context;
        this.type = type;
        this.mapDialog = mapdialog;
        this.mapFragment = mapFragment;
        this.mapViewModel = mapViewModel;
        this.activity = activity;
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
                if(!result.getImage().isEmpty()) Glide.with(context).load(result.getImage()).into(holder.userProfilePic);
                
                if(type != Types.PROPOSED)
                    holder.proposalPublisher.setText(result.getName()+" "+result.getSurname());
                else
                    holder.proposalPublisher.setText("Tu");

                holder.proposalTitle.setText(proposal.getTitle());
                holder.proposalBody.setText(proposal.getBody());

                if(proposal.getMaxParticipants() > 1)
                    holder.proposalParticipants.setText(context.getString(R.string.number_partecipants)+proposal.getAcceptersID().size()+"/"+proposal.getMaxParticipants());
                else
                    holder.proposalParticipants.setText("");

                Date date = proposal.getExpiringDate();
                LocalDateTime expiring_date_time = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
                holder.expiringHour.setText((String.format("%02d", expiring_date_time.getHour()+1)) + ":" + (String.format("%02d", expiring_date_time.getMinute())));
                holder.expiringDate.setText((String.format("%02d", expiring_date_time.getDayOfMonth())) + "/" + (String.format("%02d", expiring_date_time.getMonthValue())) + "/" + expiring_date_time.getYear());

                if(proposal.getPriority()) holder.constraintLayout.setBackgroundResource(R.drawable.list_element_roundcorner_priority);

                if(proposal.getFlagsUsers().size() >= 1 && type == Types.PROPOSED){
                    holder.proposalPublisher.setVisibility(View.INVISIBLE);
                    holder.proposalFlagged.setText(context.getString(R.string.flagged_activity));
                    holder.proposalFlagged.setVisibility(View.VISIBLE);
                    holder.constraintLayout.setBackgroundResource(R.drawable.list_element_roundcorner_red);
                }
            }
        });

       ConstraintLayout constraintLayout = holder.itemView.findViewById(R.id.proposal);

        switch(type){
            case PROPOSED:constraintLayout.setOnClickListener(new View.OnClickListener() {
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
                                            Toast.makeText(context, context.getString(R.string.delete_activity), Toast.LENGTH_SHORT).show();
                                            Utilities.getProposals(Utilities.day, UserManager.getUserId(), ProfileViewModel.proposedFilters, Types.PROPOSED);
                                        } else
                                            Toast.makeText(context, context.getString(R.string.error_delete_activity), Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(context, context.getString(R.string.options), Toast.LENGTH_SHORT).show();
                                        else{
                                            if(totalProposal.isChecked()){
                                                ProposalRepository.deleteProposal(documentId, new MyCallback<Boolean>() {
                                                    @Override
                                                    public void onCallback(Boolean result) {
                                                        if (result) {
                                                            Toast.makeText(context, R.string.delete_activity, Toast.LENGTH_SHORT).show();
                                                            Utilities.getProposals(Utilities.day, UserManager.getUserId(), ProfileViewModel.proposedFilters, Types.PROPOSED);
                                                        } else
                                                            Toast.makeText(context, R.string.error_delete_activity, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                            else{
                                                ProposalRepository.updatePeriodicProposal(proposal, new MyCallback<Boolean>() {
                                                    @Override
                                                    public void onCallback(Boolean result) {
                                                        if (result) {
                                                            Toast.makeText(context, R.string.delete_activity, Toast.LENGTH_SHORT).show();
                                                            Utilities.getProposals(Utilities.day, UserManager.getUserId(), ProfileViewModel.proposedFilters, Types.PROPOSED);
                                                        } else
                                                            Toast.makeText(context, R.string.error_delete_activity, Toast.LENGTH_SHORT).show();
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

                            text.setText(R.string.update_activity);
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

                                    }else{
                                        maxPartecipants.setVisibility(View.GONE);
                                        maxPartecipants.setText("");
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
                                        maxPartecipantsNumber = Integer.parseInt(maxPartecipants.getText().toString());
                                    }

                                    ProposalRepository.editProposal(documentId, activityTitle.getText().toString().trim(), activityBody.getText().toString().trim(), calendar.getTime(), proposal.getPublishingDate(), newFilters, maxPartecipantsNumber, choice[0], new MyCallback<Boolean>() {
                                        @Override
                                        public void onCallback(Boolean result) {

                                            if(result){
                                                Toast.makeText(context, R.string.updated_activity, Toast.LENGTH_SHORT).show();
                                                Utilities.getProposals(Utilities.day, UserManager.getUserId(), ProfileViewModel.proposedFilters, Types.PROPOSED);
                                            }
                                            else
                                                Toast.makeText(context, R.string.error_updated_activity, Toast.LENGTH_SHORT).show();
                                            secondDialog.dismiss();
                                        }
                                    });
                                }
                            });
                        }
                    });

                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!proposal.getAcceptersID().isEmpty()) {
                                if (proposal.getRepublishTypes() == RepublishTypes.NEVER) {
                                    ProposalRepository.deleteProposal(documentId, new MyCallback<Boolean>() {
                                        @Override
                                        public void onCallback(Boolean result) {
                                            if (result) {
                                                Toast.makeText(context, R.string.completed_activity, Toast.LENGTH_SHORT).show();
                                                Utilities.getProposals(Utilities.day, UserManager.getUserId(), ProfileViewModel.proposedFilters, Types.PROPOSED);
                                            } else
                                                Toast.makeText(context, R.string.error_completed_activity, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    ProposalRepository.updatePeriodicProposal(proposal, new MyCallback<Boolean>() {
                                        @Override
                                        public void onCallback(Boolean result) {
                                            if (result) {
                                                Toast.makeText(context, R.string.completed_activity, Toast.LENGTH_SHORT).show();
                                                Utilities.getProposals(Utilities.day, UserManager.getUserId(), ProfileViewModel.proposedFilters, Types.PROPOSED);
                                            } else
                                                Toast.makeText(context, R.string.error_completed_activity, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }else{
                                Toast.makeText(context, R.string.error_completion,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }); break;
            
            case ACCEPTED: constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageButton reject = holder.itemView.findViewById(R.id.reject_button),
                                map = holder.itemView.findViewById(R.id.map_accepted_button);
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
                                        Toast.makeText(context, R.string.withdrawal_activity, Toast.LENGTH_SHORT).show();
                                        Utilities.getProposals(Utilities.day, UserManager.getUserId(), HomeViewModel.filters, Types.ACCEPTED);
                                    }else
                                        Toast.makeText(context, R.string.error_withdrawal_activity, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                    map.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mapDialog.findViewById(R.id.map_container).setVisibility(View.VISIBLE);

                            LatLng coord = new LatLng(proposal.getLatitude(), proposal.getLongitude());

                            UserManager.getUser(proposal.getPublisherID(), new MyCallback<UserDatabaseRepository.User>() {
                                @Override
                                public void onCallback(UserDatabaseRepository.User result) {
                                    LatLng home_coord = mapViewModel.getCoordinatesFromAddress(context, result.getCity(), result.getStreet(), result.getStreet_number());
                                    Location home = new Location(""), other = new Location("");
                                    if(home_coord != null){
                                        home.setLatitude(home_coord.latitude);
                                        home.setLongitude(home_coord.longitude);
                                        other.setLatitude(coord.latitude);
                                        other.setLongitude(coord.longitude);
                                        if(home.distanceTo(other) != 0.0)
                                            mapViewModel.initializeMap(activity, context,null, mapFragment,null,null,null, coord, home_coord);
                                        else
                                            mapViewModel.initializeMap(activity, context,null, mapFragment,null,null,null, coord, null);

                                    }
                                }
                            });
                            mapDialog.show();
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
                                if(result) {
                                    flag.setImageResource(R.drawable.ic_flag_filled);
                                    flag.setTag("flagged");
                                }
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
                                        Toast.makeText(context, R.string.accept_activity, Toast.LENGTH_SHORT).show();
                                        Utilities.getProposals(Utilities.day, UserManager.getUserId(), AvailableViewModel.filters, Types.AVAILABLE);
                                    }else
                                        Toast.makeText(context, R.string.error_accept_activity, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                    flag.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(flag.getTag().equals("to_flag"))
                                ProposalRepository.addFlagUser(documentId, userId, new MyCallback<Boolean>() {
                                    @Override
                                    public void onCallback(Boolean result) {
                                        if (result) {
                                            Toast.makeText(context, R.string.flag_activity, Toast.LENGTH_SHORT).show();
                                            flag.setImageResource(R.drawable.ic_flag_filled);
                                            flag.setTag("flagged");
                                        } else {
                                            Toast.makeText(context, R.string.error_flag_activity, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            else
                                ProposalRepository.deleteFlagUser(documentId, userId, new MyCallback<Boolean>() {
                                    @Override
                                    public void onCallback(Boolean result) {
                                        if (result) {
                                            Toast.makeText(context, R.string.unflag_activity, Toast.LENGTH_SHORT).show();
                                            flag.setImageResource(R.drawable.ic_flag);
                                            flag.setTag("to_flag");
                                        } else {
                                            Toast.makeText(context, R.string.error_unflag_activity, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        }
                    });

                    map.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mapDialog.findViewById(R.id.map_container).setVisibility(View.VISIBLE);

                            LatLng coord = new LatLng(proposal.getLatitude(), proposal.getLongitude());

                            UserManager.getUser(proposal.getPublisherID(), new MyCallback<UserDatabaseRepository.User>() {
                                @Override
                                public void onCallback(UserDatabaseRepository.User result) {
                                    LatLng home_coord = mapViewModel.getCoordinatesFromAddress(context, result.getCity(), result.getStreet(), result.getStreet_number());
                                    Location home = new Location(""), other = new Location("");
                                    if(home_coord != null){
                                        home.setLatitude(home_coord.latitude);
                                        home.setLongitude(home_coord.longitude);
                                        other.setLatitude(coord.latitude);
                                        other.setLongitude(coord.longitude);
                                        if(home.distanceTo(other) != 0)
                                            mapViewModel.initializeMap(activity, context,null, mapFragment,null,null,null, coord, home_coord);
                                        else
                                            mapViewModel.initializeMap(activity, context,null, mapFragment,null,null,null, coord, null);

                                    }
                                }
                            });

                            mapDialog.show();
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
        TextView proposalPublisher, proposalTitle, proposalBody, proposalParticipants, expiringHour, expiringDate, proposalFlagged;
        ConstraintLayout constraintLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userProfilePic = itemView.findViewById(R.id.proposal_user_pic);
            proposalPublisher = itemView.findViewById(R.id.proposal_publisher);
            proposalTitle = itemView.findViewById(R.id.proposal_title);
            proposalBody = itemView.findViewById(R.id.proposal_body);
            proposalParticipants = itemView.findViewById(R.id.proposal_participants);
            expiringHour = itemView.findViewById(R.id.expiring_hour);
            expiringDate = itemView.findViewById(R.id.expiring_date);
            constraintLayout = itemView.findViewById(R.id.outer_constraintlayout);
            proposalFlagged = itemView.findViewById(R.id.proposal_flagged);
        }
    }
}
