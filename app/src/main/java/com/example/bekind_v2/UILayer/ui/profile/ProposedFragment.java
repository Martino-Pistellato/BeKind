package com.example.bekind_v2.UILayer.ui.profile;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.BottomBar;
import com.example.bekind_v2.UILayer.ui.home.HomeViewModel;
import com.example.bekind_v2.Utilities.GetProposals;
import com.example.bekind_v2.Utilities.MyCallback;
import com.example.bekind_v2.Utilities.ProposalRecyclerViewAdapter;
import com.example.bekind_v2.Utilities.ProposalsViewModel;
import com.example.bekind_v2.Utilities.ScheduleBar;
import com.example.bekind_v2.Utilities.Types;
import com.example.bekind_v2.Utilities.Utilities;

import java.util.ArrayList;

public class ProposedFragment extends Fragment {
    private final ProposalsViewModel proposalsViewModel;

    public ProposedFragment() {
        this.proposalsViewModel = Utilities.SharedViewModel.proposalsViewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proposed, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_proposal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Context context = this.getContext();

        final Observer<ArrayList<ProposalRepository.Proposal>> proposedObserver = new Observer<ArrayList<ProposalRepository.Proposal>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<ProposalRepository.Proposal> proposed) {
                ProposalRecyclerViewAdapter adapter = new ProposalRecyclerViewAdapter(proposed, getContext(), Types.PROPOSED, new MyCallback<Boolean>() {
                    @Override
                    public void onCallback(Boolean result) {
                        if(result) {
                            Toast.makeText(context, "Attività eliminata correttamente", Toast.LENGTH_SHORT).show();
                            GetProposals.getProposalsDate(ScheduleBar.ScheduleDate.getScheduleLocalDate(), UserManager.getUserId(), HomeViewModel.filters, Types.AVAILABLE);
                        }else
                            Toast.makeText(context, "Errore nell'eliminazione dell'attività", Toast.LENGTH_SHORT).show();
                    }
                });
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };

        proposalsViewModel.getProposed().observe(getViewLifecycleOwner(),proposedObserver);

        return view;
    }
}