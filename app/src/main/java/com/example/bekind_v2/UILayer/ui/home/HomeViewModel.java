package com.example.bekind_v2.UILayer.ui.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.Utilities.MyCallback;
import com.example.bekind_v2.Utilities.ProposalsViewModel;
import com.example.bekind_v2.Utilities.Types;
import com.example.bekind_v2.Utilities.Utilities;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {
    public static ArrayList<String> filters = new ArrayList<>();

    public void manageFilter(String filter){
        Utilities.manageFilter(filter, filters);

        ProposalRepository.getProposals(Utilities.SharedViewModel.day, UserManager.getUserId(), filters, Types.PROPOSED, new MyCallback<ArrayList<ProposalRepository.Proposal>>() {
                    @Override
                    public void onCallback(ArrayList<ProposalRepository.Proposal> result) {
                        Utilities.SharedViewModel.proposalsViewModel.getProposed().setValue(result);
                    }
                }
        );

        ProposalRepository.getProposals(Utilities.SharedViewModel.day, UserManager.getUserId(), filters, Types.ACCEPTED, new MyCallback<ArrayList<ProposalRepository.Proposal>>() {
                    @Override
                    public void onCallback(ArrayList<ProposalRepository.Proposal> result) {
                        Utilities.SharedViewModel.proposalsViewModel.getAccepted().setValue(result);
                    }
                }
        );
    }
}