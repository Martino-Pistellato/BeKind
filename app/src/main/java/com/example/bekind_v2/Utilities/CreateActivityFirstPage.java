package com.example.bekind_v2.Utilities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.BottomBarViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

public class CreateActivityFirstPage extends Fragment {
    private View view;
    private BottomBarViewModel bottomBarViewModel;
    private CreateActivityDialog createActivityDialog;

    public CreateActivityFirstPage(BottomBarViewModel bottomBarViewModel, CreateActivityDialog createActivityDialog){
        this.bottomBarViewModel = bottomBarViewModel;
        this.createActivityDialog = createActivityDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.add_proposal_popup,container,false);

        TextInputEditText title, body;
        DatePicker expiringDate;
        TimePicker expiringHour;
        Chip shoppingChip, houseworksChip, cleaningChip, transportChip, randomChip;
        Button closeBtn, continueBtn;
        shoppingChip = view.findViewById(R.id.shopping_chip_popup);
        houseworksChip = view.findViewById(R.id.houseworks_chip_popup);
        cleaningChip = view.findViewById(R.id.cleaning_chip_popup);
        transportChip = view.findViewById(R.id.transport_chip_popup);
        randomChip = view.findViewById(R.id.random_chip_popup);

        shoppingChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomBarViewModel.manageFilterProposal(shoppingChip.getText().toString());
            }
        });
        houseworksChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomBarViewModel.manageFilterProposal(houseworksChip.getText().toString());
            }
        });
        cleaningChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomBarViewModel.manageFilterProposal(cleaningChip.getText().toString());
            }
        });
        transportChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomBarViewModel.manageFilterProposal(transportChip.getText().toString());
            }
        });
        randomChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomBarViewModel.manageFilterProposal(randomChip.getText().toString());
            }
        });

        closeBtn = view.findViewById(R.id.close_btn); //button for closing dialog
        continueBtn = view.findViewById(R.id.continue_btn);

        title = view.findViewById(R.id.activity_title);
        body = view.findViewById(R.id.activity_body);
        expiringDate = view.findViewById(R.id.date_picker);
        expiringHour = view.findViewById(R.id.time_picker);

        title.setText(bottomBarViewModel.getProposalTitle());
        body.setText(bottomBarViewModel.getProposalBody());
        bottomBarViewModel.setSelectedChips(shoppingChip, houseworksChip, cleaningChip, transportChip, randomChip);

        bottomBarViewModel.setExpiringHour(expiringHour);
        bottomBarViewModel.setExpiringDate(expiringDate);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createActivityDialog.dismiss(); //close dialog
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date proposalExpiringDate = BottomBarViewModel.toDate(expiringDate.getYear(), expiringDate.getMonth(), expiringDate.getDayOfMonth(), expiringHour.getHour()-1, expiringHour.getMinute());
                String proposalTitle = title.getText().toString().trim(); //gets the content of the title
                String proposalBody = body.getText().toString().trim(); //gets the content of the body

                if(!bottomBarViewModel.checkConstraints(title, proposalTitle, body, proposalBody, proposalExpiringDate))
                    Toast.makeText(getContext(), "Errore: i campi non sono stati riempiti correttamente", Toast.LENGTH_SHORT).show();
                else{
                    bottomBarViewModel.saveProposalData(proposalTitle, proposalBody, proposalExpiringDate);
                    createActivityDialog.changeFragment(getActivity(), R.layout.add_proposal_popup);
                    /*
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    //Out of simplicity, i am creating ChildFragment2 every time user presses the button.
                    //However, you should keep the instance somewhere to avoid creation.
                    transaction.replace(R.id.proposal_popup1, createActivity2);
                    //You can add here as well your fragment in and out animation how you like.
                    transaction.addToBackStack("childFragment2");
                    transaction.commit();
                    //showSecondPopupProposal(applicationContext,dialog,choose_dialog, map);*/
                }
            }
        });

        return view;
    }
}
