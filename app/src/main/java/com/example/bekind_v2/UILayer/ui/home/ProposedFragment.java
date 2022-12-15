package com.example.bekind_v2.UILayer.ui.home;

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
import com.example.bekind_v2.UILayer.BottomBar;
import com.example.bekind_v2.Utilities.ProposalRecyclerViewAdapter;
import com.example.bekind_v2.Utilities.ProposalsViewModel;

import java.util.ArrayList;

public class ProposedFragment extends Fragment {

    private ProposalsViewModel proposalsViewModel;

    public ProposedFragment() {
        this.proposalsViewModel = BottomBar.SharedViewModel.proposalsViewModel;
    }

    public ProposedFragment(ProposalsViewModel proposalsViewModel){
        this.proposalsViewModel = proposalsViewModel;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proposed, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RecyclerView recyclerView = this.getView().findViewById(R.id.recycler_view_proposal);
        Log.e("HERE", "quack");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final Observer<ArrayList<ProposalRepository.Proposal>> proposedObserver = new Observer<ArrayList<ProposalRepository.Proposal>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<ProposalRepository.Proposal> proposed) {
                Log.e("ON CHANGED", "proposed is changed");
                for (ProposalRepository.Proposal prop : proposed)
                    Log.e("PRPSD", prop.toString());
                ProposalRecyclerViewAdapter adapter = new ProposalRecyclerViewAdapter(proposed, getContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };

        Log.e("PROPOSED", "call");
        Log.e("PROPOSED", "is null: " + (proposalsViewModel == null));
        this.proposalsViewModel = BottomBar.SharedViewModel.proposalsViewModel;
        Log.e("PROPOSED", "is null: " + (proposalsViewModel == null));
        proposalsViewModel.getProposed().observe(getViewLifecycleOwner(),proposedObserver);
    }
}