package com.example.bekind_v2.Utilities;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bekind_v2.DataLayer.ProposalRepository.*;

import java.util.ArrayList;

public class ProposalsViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Proposal>> proposed;
    private MutableLiveData<ArrayList<Proposal>> accepted;
    private MutableLiveData<ArrayList<Proposal>> available;

    public MutableLiveData<ArrayList<Proposal>> getProposed() {
        if (proposed == null) {
            proposed = new MutableLiveData<ArrayList<Proposal>>();
        }
        return proposed;
    }

    public MutableLiveData<ArrayList<Proposal>> getAccepted() {
        if (accepted == null) {
            accepted = new MutableLiveData<ArrayList<Proposal>>();
        }
        return accepted;
    }

    public MutableLiveData<ArrayList<Proposal>> getAvailable() {
        if (available == null) {
            available = new MutableLiveData<ArrayList<Proposal>>();
        }
        return available;
    }
}
