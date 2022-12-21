package com.example.bekind_v2.UILayer.ui.home;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.R;
import com.example.bekind_v2.Utilities.MyCallback;
import com.example.bekind_v2.Utilities.ProposalRecyclerViewAdapter;
import com.example.bekind_v2.Utilities.ProposalsViewModel;
import com.example.bekind_v2.Utilities.Types;
import com.example.bekind_v2.Utilities.Utilities;

import java.util.ArrayList;

public class AcceptedFragment extends Fragment /*implements ProposalRecyclerViewInterface */{
    private final ProposalsViewModel proposalsViewModel;

    public AcceptedFragment() {
        this.proposalsViewModel = Utilities.SharedViewModel.proposalsViewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accepted, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_proposal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Context context = this.getContext();

        final Observer<ArrayList<ProposalRepository.Proposal>> acceptedObserver = new Observer<ArrayList<ProposalRepository.Proposal>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<ProposalRepository.Proposal> accepted) {
                ProposalRecyclerViewAdapter adapter = new ProposalRecyclerViewAdapter(accepted, getContext(), Types.ACCEPTED, new MyCallback<Boolean>() {
                    @Override
                    public void onCallback(Boolean result) {
                        if(result)
                            Toast.makeText(context,"Ritiro dall'attività avvenuto correttamente", Toast.LENGTH_SHORT).show();
                            //TODO: add function refresh to utilities
                        else
                            Toast.makeText(context, "Errore nel ritira dall'attività", Toast.LENGTH_SHORT).show();
                    }
                });
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };

        proposalsViewModel.getAccepted().observe(getViewLifecycleOwner(),acceptedObserver);

        return view;
    }

    /*@Override
    public void onItemClick(int position, View view) {
        Log.e("ACCEPTED", "drop down");
        ImageButton reject = view.findViewById(R.id.reject_button);
        LinearLayout linearLayout = view.findViewById(R.id.buttons_container_recycler_accepted);

        linearLayout.setVisibility(View.VISIBLE);
    }*/
}