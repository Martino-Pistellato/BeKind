package com.example.bekind_v2.UILayer.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bekind_v2.R;
import com.example.bekind_v2.Utilities.ProposalsViewModel;
import com.example.bekind_v2.databinding.FragmentHomeBinding;


public class AcceptedFragment extends Fragment {
    private ProposalsViewModel proposalsViewModel;

    public AcceptedFragment() {
        this.proposalsViewModel = new ViewModelProvider(this).get(ProposalsViewModel.class);
    }

    public AcceptedFragment(ProposalsViewModel proposalsViewModel) {
        this.proposalsViewModel = proposalsViewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_accepted, container, false);
    }
}