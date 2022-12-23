package com.example.bekind_v2.Utilities;

import android.util.Log;

import com.example.bekind_v2.DataLayer.ProposalRepository;

import java.time.LocalDate;
import java.util.ArrayList;

public class GetProposals {

      public static void getProposalsDate(LocalDate day, String userId, ArrayList<String> filters, Types type){
        ProposalRepository.getProposals(day, userId, filters, type, new MyCallback<ArrayList<ProposalRepository.Proposal>>() {
            @Override
            public void onCallback(ArrayList<ProposalRepository.Proposal> result) {
                switch(type){
                    case ACCEPTED:
                        Log.e("IN ACC CASE GETPROPOSALDATE", "accepted by "+ userId+ " = "+(Utilities.SharedViewModel.proposalsViewModel.toString()));
                        Utilities.SharedViewModel.proposalsViewModel.getAccepted().setValue(result);
                        Log.e("AFTER SETVALUE IN GETPROPOSALDATE", Utilities.SharedViewModel.proposalsViewModel.toString());
                        break;
                    case AVAILABLE: Utilities.SharedViewModel.proposalsViewModel.getAvailable().setValue(result); break;
                    case PROPOSED: Utilities.SharedViewModel.proposalsViewModel.getProposed().setValue(result); break;
                }
            }
        });
    }
}
