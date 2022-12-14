package com.example.bekind_v2.UILayer.ui.home;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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

import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.R;
import com.example.bekind_v2.Utilities.ProposalRecyclerViewAdapter;
import com.example.bekind_v2.Utilities.ProposalsViewModel;

import java.util.ArrayList;

public class ProposalFragment extends Fragment {

    private ProposalsViewModel proposalsViewModel;

    public ProposalFragment() {
        this.proposalsViewModel = new ViewModelProvider(this).get(ProposalsViewModel.class);
    }

    public ProposalFragment(ProposalsViewModel proposalsViewModel){
        this.proposalsViewModel = proposalsViewModel;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proposal, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        proposalsViewModel = new ViewModelProvider(this).get(ProposalsViewModel.class);
        RecyclerView recyclerView = this.getView().findViewById(R.id.recycler_view_proposal);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final Observer<ArrayList<ProposalRepository.Proposal>> proposedObserver = new Observer<ArrayList<ProposalRepository.Proposal>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<ProposalRepository.Proposal> proposed) {
                ProposalRecyclerViewAdapter adapter = new ProposalRecyclerViewAdapter(proposed, getContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };
        proposalsViewModel.getProposed().observe(getViewLifecycleOwner(),proposedObserver);
    }
}