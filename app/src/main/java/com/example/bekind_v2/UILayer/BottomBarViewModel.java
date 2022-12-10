package com.example.bekind_v2.UILayer;

import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.lifecycle.ViewModel;

import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;

import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.DataLayer.UserManager;

public class BottomBarViewModel extends ViewModel {
    private ArrayList<String> filters;

    public BottomBarViewModel() {
        filters = new ArrayList<>();
    }

    public void createProposal(String title, String body, Date expiringDate){
        String userId = UserManager.getUserId();
        ProposalRepository.createProposal(title, body, expiringDate, userId, null, filters);
    }

    public static Date toDate(int year, int month, int day, int hour, int minute){
        Calendar expiringDate = Calendar.getInstance();
        expiringDate.set(year, month, day, hour, minute);
        return expiringDate.getTime();
    }

    public void manageFilter(String filter){
        if(filters.contains(filter))
            filters.remove(filter);
        else
            filters.add(filter);
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
        Date currentDate = toDate(current.getYear(), current.getMonthValue(), current.getDayOfMonth(), current.getHour(), current.getMinute());
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
}
