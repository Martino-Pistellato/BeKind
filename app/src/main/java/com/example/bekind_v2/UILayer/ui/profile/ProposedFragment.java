package com.example.bekind_v2.UILayer.ui.profile;

import androidx.lifecycle.Observer;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.R;
import com.example.bekind_v2.Utilities.MapViewModel;
import com.example.bekind_v2.Utilities.ProposalRecyclerViewAdapter;
import com.example.bekind_v2.Utilities.ProposalsViewModel;
import com.example.bekind_v2.Utilities.Types;
import com.example.bekind_v2.Utilities.Utilities;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;

public class ProposedFragment extends Fragment {
    private final ProposalsViewModel proposalsViewModel;
    private MapViewModel mapViewModel;

    public ProposedFragment(){
        this.proposalsViewModel = Utilities.SharedViewModel.proposalsViewModel;
        this.mapViewModel = Utilities.SharedViewModel.mapViewModel;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proposed, container, false);

        Chip shoppingChip, houseworksChip, cleaningChip, transportChip, randomChip;
        shoppingChip = view.findViewById(R.id.shopping_chip);
        houseworksChip = view.findViewById(R.id.houseworks_chip);
        cleaningChip = view.findViewById(R.id.cleaning_chip);
        transportChip = view.findViewById(R.id.transport_chip);
        randomChip = view.findViewById(R.id.random_chip);

        shoppingChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileViewModel.manageProposedFilter(shoppingChip.getText().toString());
            }

        });

        houseworksChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ProfileViewModel.manageProposedFilter(houseworksChip.getText().toString()); }
        });

        cleaningChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ProfileViewModel.manageProposedFilter(cleaningChip.getText().toString()); }
        });

        transportChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ProfileViewModel.manageProposedFilter(transportChip.getText().toString()); }
        });

        randomChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ProfileViewModel.manageProposedFilter(randomChip.getText().toString()); }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_proposal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final Observer<ArrayList<ProposalRepository.Proposal>> proposedObserver = new Observer<ArrayList<ProposalRepository.Proposal>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<ProposalRepository.Proposal> proposed) {
                ProposalRecyclerViewAdapter adapter = new ProposalRecyclerViewAdapter(mapViewModel,null, null, proposed, getContext(),getActivity(), Types.PROPOSED);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };

        proposalsViewModel.getProposed().observe(getViewLifecycleOwner(),proposedObserver);

        return view;
    }
}