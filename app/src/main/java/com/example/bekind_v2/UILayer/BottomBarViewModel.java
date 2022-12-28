package com.example.bekind_v2.UILayer;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.example.bekind_v2.DataLayer.PostRepository;
import com.example.bekind_v2.DataLayer.UserDatabaseRepository;
import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.ui.home.HomeViewModel;
import com.example.bekind_v2.Utilities.RepublishTypes;
import com.example.bekind_v2.Utilities.Types;
import com.example.bekind_v2.Utilities.Utilities;
import com.google.android.material.chip.Chip;
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

    private String proposalTitle, proposalBody;
    private Date proposalExpd;

    public BottomBarViewModel() {
        filtersProposal = new ArrayList<>();
        filtersPost = new ArrayList<>();
        proposalTitle = "";
        proposalBody = "";
    }

    public void createProposal(String title, String body, int max, Date expiringDate, RepublishTypes choice){
        String userId = UserManager.getUserId();
        UserManager.getUser(userId, user -> {
            if(user != null)
                ProposalRepository.createProposal(title, body, expiringDate, userId, user.getNeighbourhoodID(), max, choice, filtersProposal);
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

    public boolean checkGroupProposalConstraints(TextInputEditText maxparticipants, String proposalPartcipants) {
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
        return true;
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


    public void showFirstPopupProposal(Context applicationContext, Dialog dialog, Dialog choose_dialog) {
        TextInputEditText title, body;
        DatePicker expiringDate;
        TimePicker expiringHour;
        Chip shoppingChip, houseworksChip, cleaningChip, transportChip, randomChip;
        Button closeBtn, continueBtn;

        dialog.setContentView(R.layout.add_proposal_popup); //set content of dialog (look in layout folder for new_activity_dialog file)
        dialog.setCanceledOnTouchOutside(false); //prevents dialog to close when clicking outside of it

        shoppingChip = dialog.findViewById(R.id.shopping_chip_popup);
        houseworksChip = dialog.findViewById(R.id.houseworks_chip_popup);
        cleaningChip = dialog.findViewById(R.id.cleaning_chip_popup);
        transportChip = dialog.findViewById(R.id.transport_chip_popup);
        randomChip = dialog.findViewById(R.id.random_chip_popup);

        shoppingChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageFilterProposal(shoppingChip.getText().toString());
            }
        });
        houseworksChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageFilterProposal(houseworksChip.getText().toString());
            }
        });
        cleaningChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageFilterProposal(cleaningChip.getText().toString());
            }
        });
        transportChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageFilterProposal(transportChip.getText().toString());
            }
        });
        randomChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageFilterProposal(randomChip.getText().toString());
            }
        });

        closeBtn = dialog.findViewById(R.id.close_btn); //button for closing dialog
        continueBtn = dialog.findViewById(R.id.continue_btn);

        title = dialog.findViewById(R.id.activity_title);
        body = dialog.findViewById(R.id.activity_body);
        expiringDate = dialog.findViewById(R.id.date_picker);
        expiringHour = dialog.findViewById(R.id.time_picker);

        title.setText(proposalTitle);
        body.setText(proposalBody);
        setSelectedChips(shoppingChip, houseworksChip, cleaningChip, transportChip, randomChip);

        setExpiringHour(expiringHour);
        setExpiringDate(expiringDate);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss(); //close dialog
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date proposalExpiringDate = BottomBarViewModel.toDate(expiringDate.getYear(), expiringDate.getMonth(), expiringDate.getDayOfMonth(), expiringHour.getHour(), expiringHour.getMinute());
                String proposalTitle = title.getText().toString().trim(); //gets the content of the title
                String proposalBody = body.getText().toString().trim(); //gets the content of the body

                if(!checkConstraints(title, proposalTitle, body, proposalBody, proposalExpiringDate))
                    Toast.makeText(applicationContext, "Errore: i campi non sono stati riempiti correttamente", Toast.LENGTH_SHORT).show();
                else{
                    saveProposalData(proposalTitle, proposalBody, proposalExpiringDate);
                    showSecondPopupProposal(applicationContext,dialog,choose_dialog);
                }
            }
        });
        dialog.show();
    }

    private void setSelectedChips(Chip shoppingChip, Chip houseworksChip, Chip cleaningChip, Chip transportChip, Chip randomChip) {
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

    private void showSecondPopupProposal(Context applicationContext, Dialog dialog, Dialog choose_dialog) {
        CheckBox groupProposal, periodicProposal;
        TextInputEditText maxParticipants;
        ListView listView;
        Button backBtn, publishBtn;

        dialog.setContentView(R.layout.add_proposal_popup2);

        groupProposal = dialog.findViewById(R.id.group_checkbox);
        maxParticipants = dialog.findViewById(R.id.activity_maxparticipants);

        groupProposal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    maxParticipants.setVisibility(View.VISIBLE);
                else {
                    maxParticipants.setText("");
                    maxParticipants.setVisibility(View.GONE);
                }
            }
        });

        periodicProposal = dialog.findViewById(R.id.periodic_checkbox);
        listView = dialog.findViewById(R.id.periodic_choices);

        RepublishTypes[] types = {RepublishTypes.GIORNALIERA, RepublishTypes.SETTIMANALE, RepublishTypes.MENSILE, RepublishTypes.ANNUALE};
        ArrayAdapter<RepublishTypes> adapter = new ArrayAdapter<>(applicationContext, android.R.layout.simple_list_item_single_choice, types);
        listView.setAdapter(adapter);
        RepublishTypes[] choice = new RepublishTypes[1];
        choice[0] = RepublishTypes.MAI;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                choice[0] = (RepublishTypes) listView.getItemAtPosition(i);
            }
        });

        periodicProposal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    listView.setVisibility(View.VISIBLE);
                else
                    listView.setVisibility(View.GONE);
            }
        });

        backBtn = dialog.findViewById(R.id.back_btn);
        publishBtn = dialog.findViewById(R.id.publish_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFirstPopupProposal(applicationContext, dialog, choose_dialog);
            }
        });

        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int proposalMaxParticipants= 1;
                boolean publish = true;

                if(groupProposal.isChecked()) {
                    if (!checkGroupProposalConstraints(maxParticipants, maxParticipants.getText().toString().trim())) {
                        Toast.makeText(applicationContext, "Errore: i campi non sono stati riempiti correttamente", Toast.LENGTH_SHORT).show();
                        publish = false;
                    }
                    else{
                        proposalMaxParticipants = Integer.valueOf(maxParticipants.getText().toString().trim());
                    }
                }

                if(periodicProposal.isChecked()){
                    if(choice[0] == RepublishTypes.MAI){
                        Toast.makeText(applicationContext, "Errore: i campi non sono stati riempiti correttamente", Toast.LENGTH_SHORT).show();
                        publish = false;
                    }
                }

                if(publish){
                    createProposal(getProposalTitle(), getProposalBody(), proposalMaxParticipants, getProposalExpd(), choice[0]);
                    Utilities.getProposals(Utilities.day, UserManager.getUserId(), HomeViewModel.filters, Types.PROPOSED);
                    setProposalBody("");
                    setProposalTitle("");
                    dialog.dismiss();
                    choose_dialog.dismiss();
                }

            }
        });
        dialog.show();
    }

    private void saveProposalData(String proposalTitle, String proposalBody, Date proposalExpd) {
        this.proposalTitle=proposalTitle;
        this.proposalBody=proposalBody;
        this.proposalExpd = proposalExpd;
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

    public void setProposalTitle(String proposalTitle) {
        this.proposalTitle = proposalTitle;
    }

    public void setProposalBody(String proposalBody) {
        this.proposalBody = proposalBody;
    }

}
