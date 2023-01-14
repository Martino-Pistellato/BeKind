package com.example.bekind_v2.UILayer;

import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.lifecycle.ViewModel;

import com.example.bekind_v2.DataLayer.PostRepository;
import com.example.bekind_v2.R;
import com.example.bekind_v2.Utilities.MyCallback;
import com.example.bekind_v2.Utilities.RepublishTypes;
import com.example.bekind_v2.Utilities.Utilities;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;

import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.DataLayer.UserManager;

public class BottomBarViewModel extends ViewModel {
    private ArrayList<String> filtersProposal;
    private ArrayList<String> filtersPost;

    private String proposalTitle, proposalBody, proposalCity, proposalStreet, proposalStreetNumber;
    private Date proposalExpd;

    public BottomBarViewModel() {
        filtersProposal = new ArrayList<>();
        filtersPost = new ArrayList<>();
        proposalTitle = "";
        proposalBody = "";
        proposalExpd = null;
    }

    public static boolean isLogged() {
        return UserManager.isLogged();
    }

    public void createProposal(String title, String body, int max, Date expiringDate, double lat, double longitude, RepublishTypes choice, MyCallback<Boolean> myCallback){
        String userId = UserManager.getUserId();
        UserManager.getUser(userId, user -> {
            if(user != null)
                ProposalRepository.createProposal(title, body, expiringDate, userId, user.getNeighbourhoodID(), max, lat, longitude, choice, filtersProposal, myCallback);
        });
    }

    public static Date toDate(int year, int month, int day, int hour, int minute){
        Calendar expiringDate = Calendar.getInstance();
        expiringDate.set(year, month, day, hour, minute);
        return expiringDate.getTime();
    }

    public void createPost(String title, String body, MyCallback<Boolean> myCallback){
        PostRepository.createPost(title, body, filtersPost, result -> {
            filtersPost.clear();
            myCallback.onCallback(result);
        });
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
        if(this.proposalExpd==null){
            expiringHour.setHour(LocalDateTime.now(ZoneId.of("ECT")).getHour());
            expiringHour.setMinute(LocalDateTime.now().getMinute());
        }else{
            expiringHour.setHour(LocalDateTime.ofInstant(this.proposalExpd.toInstant(), ZoneId.systemDefault()).getHour());
            expiringHour.setMinute(LocalDateTime.ofInstant(this.proposalExpd.toInstant(), ZoneId.systemDefault()).getMinute());
        }
    }

    public void setExpiringDate(DatePicker expiringDate){
        //by default, we set date in datepicker to today when the add button is clicked (+1/-1 because of LocalDateTime implementation)
        if(this.proposalExpd == null){
            expiringDate.updateDate(LocalDateTime.now().getYear() - 1, LocalDateTime.now().getMonthValue() + 1, LocalDateTime.now().getDayOfMonth());
        }else{
            //for some reason, if we are using the previously saved date when getting back from second page, there is no need for -1 in year, and month must be -1 and not +1
            LocalDateTime toSet = LocalDateTime.ofInstant(this.proposalExpd.toInstant(), ZoneId.systemDefault());
            expiringDate.updateDate(toSet.getYear(), toSet.getMonthValue() - 1, toSet.getDayOfMonth());
        }
        //we cannot create an activity with an expiringDate that comes before the moment we clicked the add activity button (-1000 (one second) cause we cannot use the EXACT moment)
        expiringDate.setMinDate(System.currentTimeMillis() - 1000);
    }

    public boolean checkDateConstraint(Date expiringDate){
        LocalDateTime current = LocalDateTime.now(ZoneId.of("ECT"));
        Date currentDate = toDate(current.getYear(), current.getMonthValue() - 1, current.getDayOfMonth(), current.getHour(), current.getMinute());

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

    public boolean checkPostConstraints(TextInputEditText title, String postTitle, TextInputEditText body, String postBody, Context context){
        if(postTitle.isEmpty()){ //checks if the title is empty, in that case it blocks the creation until it is filled
            title.setError(context.getString(R.string.empty_field));
            title.requestFocus();
            return false;
        }
        else if(postBody.isEmpty()){ //checks if the body is empty, in that case it blocks the creation until it is filled
            body.setError(context.getString(R.string.empty_field));
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

    public void setSelectedChips(Chip shoppingChip, Chip houseworksChip, Chip cleaningChip, Chip transportChip, Chip randomChip) {
        for(String s : filtersProposal){
            switch(s){
                case "Spesa": shoppingChip.setChecked(true);break;
                case "Lavori Domestici":houseworksChip.setChecked(true);break;
                case "Pulizie": cleaningChip.setChecked(true);break;
                case "Trasporto": transportChip.setChecked(true); break;
                case "Varie": randomChip.setChecked(true);break;
            }
        }
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

    public void saveProposalData(String proposalTitle, String proposalBody, Date proposalExpd) {
        this.proposalTitle=proposalTitle;
        this.proposalBody=proposalBody;
        this.proposalExpd = proposalExpd;
    }

    public void saveProposalLocationData(String city, String street, String streetNumber){
        this.proposalCity = city;
        this.proposalStreet = street;
        this.proposalStreetNumber = streetNumber;
    }

    public String getProposalTitle() {
        return proposalTitle;
    }

    public String getProposalBody() {
        return proposalBody;
    }

    public Date getProposalExpd() {
        return proposalExpd;
    }

    public String getProposalCity() {
        return proposalCity;
    }

    public String getProposalStreet() {
        return proposalStreet;
    }

    public String getProposalStreetNumber() {
        return proposalStreetNumber;
    }

    public void setProposalTitle(String proposalTitle) {
        this.proposalTitle = proposalTitle;
    }

    public void setProposalBody(String proposalBody) {
        this.proposalBody = proposalBody;
    }

    public void setProposalExpd(Date proposalExpd) {
        this.proposalExpd = proposalExpd;
    }

    public void setProposalCity(String proposalCity) {
        this.proposalCity = proposalCity;
    }

    public void setProposalStreet(String proposalStreet) {
        this.proposalStreet = proposalStreet;
    }

    public void setProposalStreetNumber(String proposalStreetNumber) {
        this.proposalStreetNumber = proposalStreetNumber;
    }
}
