package com.example.bekind_v2.UILayer.ui.available;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.Utilities.MyCallback;
import com.example.bekind_v2.Utilities.Types;
import com.example.bekind_v2.Utilities.Utilities;

import java.util.ArrayList;

public class AvailableViewModel extends ViewModel {
    public static ArrayList<String> filters = new ArrayList<>();

    public void manageFilter(String filter){
        Utilities.manageFilter(filter, filters);

        ProposalRepository.getProposals(Utilities.day, UserManager.getUserId(), filters, Types.AVAILABLE, new MyCallback<ArrayList<ProposalRepository.Proposal>>() {
                    @Override
                    public void onCallback(ArrayList<ProposalRepository.Proposal> result) {
                        Utilities.SharedViewModel.proposalsViewModel.getAvailable().setValue(result);
                    }
                }
        );
    }
}