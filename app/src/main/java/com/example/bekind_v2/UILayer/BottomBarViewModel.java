package com.example.bekind_v2.UILayer;

import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.lifecycle.ViewModel;

import com.example.bekind_v2.DataLayer.PostRepository;
import com.example.bekind_v2.DataLayer.UserDatabaseRepository;
import com.example.bekind_v2.Utilities.Utilities;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;

import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.DataLayer.UserManager;

public class BottomBarViewModel extends ViewModel {
    private ArrayList<String> filtersProposal;
    private ArrayList<String> filtersPost;

    public BottomBarViewModel() {
        filtersProposal = new ArrayList<>();
        filtersPost = new ArrayList<>();
    }

    public void createProposal(String title, String body, int max, Date expiringDate){
        String userId = UserManager.getUserId();
        UserManager.getUser(userId, user -> {
            if(user != null)
                ProposalRepository.createProposal(title, body, expiringDate, userId, user.getNeighbourhoodID(), max, filtersProposal);
        });
    }

    public static Date toDate(int year, int month, int day, int hour, int minute){
        Calendar expiringDate = Calendar.getInstance();
        expiringDate.set(year, month, day, hour, minute);
        return expiringDate.getTime();
    }

    public void createPost(String title, String body){
        PostRepository.createPost(title, body, filtersPost);
    }

    public void manageFilterProposal(String filter){
        Utilities.manageFilter(filter,filtersProposal);
    }

    public void manageFilterPost(String filter){
        Utilities.manageFilter(filter,filtersPost);
    }

    public void setExpiringHour(TimePicker expiringHour){
        //by default, we set time in timepicker to the hour and minute when the add button is clicked (+1 because of LocalDateTime implementation)
        expiringHour.setIs24HourView(true);  //time picker use 24h format
        expiringHour.setHour(LocalDateTime.now().getHour() + 1);
        expiringHour.setMinute(LocalDateTime.now().getMinute());
    }

    public void setExpiringDate(DatePicker expiringDate){
        //by default, we set date in datepicker to today when the add button is clicked (+1/-1 because of LocalDateTime implementation)
        expiringDate.updateDate(LocalDateTime.now().getYear() - 1, LocalDateTime.now().getMonthValue() + 1, LocalDateTime.now().getDayOfMonth());
        //we cannot create an activity with an expiringDate that comes before the moment we clicked the add activity button (-1000 (one second) cause we cannot use the EXACT moment)
        expiringDate.setMinDate(System.currentTimeMillis() - 1000);
    }

    public boolean checkDateConstraint(Date expiringDate){
        LocalDateTime current = LocalDateTime.now();
        Date currentDate = toDate(current.getYear(), current.getMonthValue() - 1, current.getDayOfMonth(), current.getHour(), current.getMinute());
        return expiringDate.after(currentDate);
    }

    public boolean checkConstraints(TextInputEditText title, String proposalTitle, TextInputEditText body, String proposalBody, Date proposalExpiringDate){
        if(proposalTitle.isEmpty()){ //checks if the title is empty, in that case it blocks the creation until it is filled
            title.setError("Questo campo non può essere vuoto");
            title.requestFocus();
            return false;
        }
        else if(proposalBody.isEmpty()){ //checks if the body is empty, in that case it blocks the creation until it is filled
            body.setError("Questo campo non può essere vuoto");
            body.requestFocus();
            return false;
        }
        else return checkDateConstraint(proposalExpiringDate);
    }

    public boolean checkGroupProposalConstraints(TextInputEditText title, String proposalTitle, TextInputEditText body, String proposalBody, Date proposalExpiringDate, TextInputEditText maxparticipants, String proposalPartcipants) {
        if(proposalPartcipants.isEmpty()){
            maxparticipants.setError("Questo campo non può essere vuoto");
            maxparticipants.requestFocus();
            return false;
        }
        if(Integer.valueOf(proposalPartcipants) <=1){
            maxparticipants.setError("Un'attività di gruppo prevede un minimo di 2 partecipanti");
            maxparticipants.requestFocus();
            return false;
        }
        return checkConstraints(title, proposalTitle, body, proposalBody, proposalExpiringDate);
    }

    public boolean checkPostConstraints(TextInputEditText title, String postTitle, TextInputEditText body, String postBody){
        if(postTitle.isEmpty()){ //checks if the title is empty, in that case it blocks the creation until it is filled
            title.setError("Questo campo non può essere vuoto");
            title.requestFocus();
            return false;
        }
        else if(postBody.isEmpty()){ //checks if the body is empty, in that case it blocks the creation until it is filled
            body.setError("Questo campo non può essere vuoto");
            body.requestFocus();
            return false;
        }
        return true;
    }
    public static void clearProposals(){
        ProposalRepository.clearProposals();
    }

    public static void clearPosts(){
        PostRepository.clearPosts();
    }



}
