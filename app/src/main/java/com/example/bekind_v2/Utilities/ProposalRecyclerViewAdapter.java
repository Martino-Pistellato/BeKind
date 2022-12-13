package com.example.bekind_v2.Utilities;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bekind_v2.R;
import com.example.bekind_v2.DataLayer.ProposalRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class ProposalRecyclerViewAdapter extends RecyclerView.Adapter<ProposalRecyclerViewAdapter.MyViewHolder> {
    private ProposalsViewModel proposalsViewModel;
    private Context context;
    private Fragment owner;

    @NonNull
    @Override
    public ProposalRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_element, parent, false);
        proposalsViewModel = new ViewModelProvider((ViewModelStoreOwner) owner).get(ProposalsViewModel.class);

        final Observer<ArrayList<ProposalRepository.Proposal>> proposedObserver = new Observer<ArrayList<ProposalRepository.Proposal>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<ProposalRepository.Proposal> proposed) {

            }
        };

        final Observer<ArrayList<ProposalRepository.Proposal>> acceptedObserver = new Observer<ArrayList<ProposalRepository.Proposal>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<ProposalRepository.Proposal> accepted) {

            }
        };

        final Observer<ArrayList<ProposalRepository.Proposal>> availableObserver = new Observer<ArrayList<ProposalRepository.Proposal>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<ProposalRepository.Proposal> available) {

            }
        };

        proposalsViewModel.getProposed().observe((LifecycleOwner) owner,proposedObserver);
        proposalsViewModel.getAccepted().observe((LifecycleOwner) owner,acceptedObserver);
        proposalsViewModel.getAvailable().observe((LifecycleOwner) owner,availableObserver);

        return new ProposalRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProposalRecyclerViewAdapter.MyViewHolder holder, int position) {
        String documentID = proposals.get(position).getId();

        holder.proposal_title.setText(proposals.get(position).getTitle());
        holder.proposal_body.setText(proposals.get(position).getBody());
        Date date = proposals.get(position).getExpiringDate();
        LocalDateTime expiring_date_time = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        holder.expiring_hour.setText((String.format("%02d", expiring_date_time.getHour()+1)) + ":" + (String.format("%02d", expiring_date_time.getMinute())));
        holder.expiring_date.setText((String.format("%02d", expiring_date_time.getDayOfMonth())) + "/" + (String.format("%02d", expiring_date_time.getMonthValue())) + "/" + expiring_date_time.getYear());
        ConstraintLayout constraintLayout = holder.itemView.findViewById(R.id.proposal);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
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

