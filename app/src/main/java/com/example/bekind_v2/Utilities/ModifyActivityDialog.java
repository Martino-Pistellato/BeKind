package com.example.bekind_v2.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.BottomBarViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class ModifyActivityDialog extends DialogFragment {

    private View view;
    private ProposalRepository.Proposal proposal;
    private String documentID;
    private ModifyActivityFirstPage modifyActivity1;
    private ModifyActivitySecondPage modifyActivity2;
    private String activityTitle, activityBody, activityCity, activityStreet, activityStreetNumber;
    private Date activityExpd;
    private ArrayList<String> filters;
    private MapViewModel mapViewModel;

    public ModifyActivityDialog(ProposalRepository.Proposal proposal, String documentID, MapViewModel mapViewModel){
        this.proposal = proposal;
        this.modifyActivity1 = new ModifyActivityFirstPage(this, proposal);
        this.modifyActivity2 = new ModifyActivitySecondPage(this, mapViewModel, proposal, documentID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.view = inflater.inflate(R.layout.add_proposal_popup_container,container,false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.add_proposal_container, modifyActivity1);
        transaction.addToBackStack("childFragment1");
        transaction.commit();
    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }

    public void changeFragment(int fragmentId){
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        if(fragmentId == R.layout.add_proposal_popup) {
            fragmentTransaction.replace(R.id.add_proposal_container, modifyActivity2);
            fragmentTransaction.addToBackStack("childFragment2");
            fragmentTransaction.commit();
        }else{
            fragmentTransaction.replace(R.id.add_proposal_container, modifyActivity1);
            fragmentTransaction.addToBackStack("childFragment1");
            fragmentTransaction.commit();
        }
    }

    public boolean checkDateConstraint(Date expiringDate){
        LocalDateTime current = LocalDateTime.now(ZoneId.of("ECT"));
        Date currentDate = BottomBarViewModel.toDate(current.getYear(), current.getMonthValue() - 1, current.getDayOfMonth(), current.getHour(), current.getMinute());

        return expiringDate.after(currentDate);
    }

    public boolean checkConstraints(TextInputEditText title, String proposalTitle, TextInputEditText body, String proposalBody, Date proposalExpiringDate, Context context){
        if(proposalTitle.isEmpty()){ //checks if the title is empty, in that case it blocks the creation until it is filled
            title.setError(context.getString(R.string.empty_field));
            title.requestFocus();
            return false;
        }
        else if(proposalBody.isEmpty()){ //checks if the body is empty, in that case it blocks the creation until it is filled
            body.setError(context.getString(R.string.empty_field));
            body.requestFocus();
            return false;
        }
        else return checkDateConstraint(proposalExpiringDate);
    }

    public void saveProposalData(String proposalTitle, String proposalBody, Date proposalExpd, ArrayList<String> filters) {
        this.activityTitle=proposalTitle;
        this.activityBody=proposalBody;
        this.activityExpd = proposalExpd;
        this.filters = filters;
    }

    public void saveProposalLocationData(String city, String street, String streetNumber){
        this.activityCity = city;
        this.activityStreet = street;
        this.activityStreetNumber = streetNumber;
    }

    public boolean checkAddress(TextInputEditText city, String cityText, TextInputEditText street, String streetText, TextInputEditText streetNumber, String streetNumb, Context context) {
        boolean res = true;

        if(cityText.isEmpty()){
            city.requestFocus();
            city.setError(context.getString(R.string.empty_field));
            res = false;
        }
        if(streetNumb.isEmpty()){
            streetNumber.requestFocus();
            streetNumber.setError(context.getString(R.string.empty_field));
            res = false;
        }
        if(streetText.isEmpty()){
            street.requestFocus();
            street.setError(context.getString(R.string.empty_field));
            res = false;
        }
        return res;
    }

    public boolean checkGroupProposalConstraints(TextInputEditText maxparticipants, String proposalPartcipants, Context context) {
        if(proposalPartcipants.isEmpty()){
            maxparticipants.setError(context.getString(R.string.empty_field));
            maxparticipants.requestFocus();
            return false;
        }
        if(Integer.valueOf(proposalPartcipants) <=1){
            maxparticipants.setError(context.getString(R.string.min_partecipants));
            maxparticipants.requestFocus();
            return false;
        }
        if(Integer.valueOf(proposalPartcipants) >20){
            maxparticipants.setError(context.getString(R.string.max_partecipants));
            maxparticipants.requestFocus();
            return false;
        }
        return true;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public String getActivityBody() {
        return activityBody;
    }

    public Date getActivityExpd() {
        return activityExpd;
    }

    public String getActivityCity() {
        return activityCity;
    }

    public String getActivityStreet() {
        return activityStreet;
    }

    public String getActivityStreetNumber() {
        return activityStreetNumber;
    }

    public ArrayList<String> getFilters() {
        return filters;
    }
}
