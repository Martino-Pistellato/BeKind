package com.example.bekind_v2.Utilities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.BottomBarViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class ModifyActivityFirstPage extends Fragment {
    private View view;
    private ModifyActivityDialog modifyActivityDialog;
    private ProposalRepository.Proposal proposal;

    public ModifyActivityFirstPage(ModifyActivityDialog modifyActivityDialog, ProposalRepository.Proposal proposal){
        this.modifyActivityDialog = modifyActivityDialog;
        this.proposal = proposal;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.add_proposal_popup,container,false);

        Date date = proposal.getExpiringDate();
        LocalDateTime expiringDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        ArrayList<String> filters = proposal.getFilters(), newFilters = new ArrayList<>();

        TextView text = view.findViewById(R.id.new_activity_header);
        TextInputEditText activityTitle = view.findViewById(R.id.activity_title), activityBody = view.findViewById(R.id.activity_body);

        DatePicker expiringDate = view.findViewById(R.id.date_picker);
        TimePicker expiringHour = view.findViewById(R.id.time_picker);
        Chip shoppingChip = view.findViewById(R.id.shopping_chip_popup), houseworkChip = view.findViewById(R.id.houseworks_chip_popup),
                cleaningChip = view.findViewById(R.id.cleaning_chip_popup), transportChip = view.findViewById(R.id.transport_chip_popup),
                randomChip = view.findViewById(R.id.random_chip_popup);
        Button closeButton = view.findViewById(R.id.close_btn) , continueButton = view.findViewById(R.id.continue_btn);

        text.setText(R.string.update_activity);
        activityTitle.setText(proposal.getTitle());
        activityBody.setText(proposal.getBody());
        expiringDate.updateDate(expiringDateTime.getYear(), expiringDateTime.getMonthValue() - 1, expiringDateTime.getDayOfMonth());
        expiringHour.setIs24HourView(true);
        expiringHour.setHour(expiringDateTime.getHour());
        expiringHour.setMinute(expiringDateTime.getMinute());

        if(filters.contains(shoppingChip.getText().toString()))
            shoppingChip.setChecked(true);
        if(filters.contains(houseworkChip.getText().toString()))
            houseworkChip.setChecked(true);
        if(filters.contains(cleaningChip.getText().toString()))
            cleaningChip.setChecked(true);
        if(filters.contains(transportChip.getText().toString()))
            transportChip.setChecked(true);
        if(filters.contains(randomChip.getText().toString()))
            randomChip.setChecked(true);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyActivityDialog.dismiss();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = activityTitle.getText().toString().trim(), body = activityBody.getText().toString().trim();
                Date proposalExpiringDate = BottomBarViewModel.toDate(expiringDate.getYear(), expiringDate.getMonth(), expiringDate.getDayOfMonth(), expiringHour.getHour(), expiringHour.getMinute());
                if(shoppingChip.isChecked())
                    newFilters.add(shoppingChip.getText().toString());
                if(houseworkChip.isChecked())
                    newFilters.add(houseworkChip.getText().toString());
                if(cleaningChip.isChecked())
                    newFilters.add(cleaningChip.getText().toString());
                if(transportChip.isChecked())
                    newFilters.add(transportChip.getText().toString());
                if(randomChip.isChecked())
                    newFilters.add(randomChip.getText().toString());
                if(! modifyActivityDialog.checkConstraints(activityTitle, title, activityBody, body, proposalExpiringDate, getContext())){
                    Toast.makeText(getActivity(), R.string.fields_error, Toast.LENGTH_SHORT).show();
                }
                else{
                    modifyActivityDialog.saveProposalData(title, body, proposalExpiringDate, newFilters);
                    modifyActivityDialog.changeFragment(R.layout.add_proposal_popup);
                }
            }
        });

        return view;
    }

}
