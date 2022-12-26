package com.example.bekind_v2.UILayer.ui.home;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.R;
import com.example.bekind_v2.Utilities.MyCallback;
import com.example.bekind_v2.Utilities.ProposalRecyclerViewAdapter;
import com.example.bekind_v2.Utilities.ScheduleBar;
import com.example.bekind_v2.Utilities.ScheduleBar.ScheduleDate;
import com.example.bekind_v2.Utilities.Types;
import com.example.bekind_v2.Utilities.Utilities;
import com.example.bekind_v2.databinding.FragmentHomeBinding;
import com.google.android.material.chip.Chip;

import java.util.Calendar;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SwitchCompat simpleSwitch;
    private TextView scheduledateText, totalActivities;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Chip shoppingChip, houseworksChip, cleaningChip, transportChip, randomChip;

        simpleSwitch = root.findViewById(R.id.simpleSwitch);

        shoppingChip = root.findViewById(R.id.shopping_chip);
        houseworksChip = root.findViewById(R.id.houseworks_chip);
        cleaningChip = root.findViewById(R.id.cleaning_chip);
        transportChip = root.findViewById(R.id.transport_chip);
        randomChip = root.findViewById(R.id.random_chip);
        
        shoppingChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { homeViewModel.manageFilter(shoppingChip.getText().toString()); }
        });
        houseworksChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { homeViewModel.manageFilter(houseworksChip.getText().toString()); }
        });
        cleaningChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { homeViewModel.manageFilter(cleaningChip.getText().toString()); }
        });
        transportChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { homeViewModel.manageFilter(transportChip.getText().toString()); }
        });
        randomChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { homeViewModel.manageFilter(randomChip.getText().toString()); }
        });

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view_proposal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Context context = this.getContext();

        final Observer<ArrayList<ProposalRepository.Proposal>> acceptedObserver = new Observer<ArrayList<ProposalRepository.Proposal>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<ProposalRepository.Proposal> accepted) {
                ProposalRecyclerViewAdapter adapter = new ProposalRecyclerViewAdapter(accepted, getContext(), Types.ACCEPTED);

                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };

        Log.e("BACK IN HOME", "in home where "+(Utilities.SharedViewModel.proposalsViewModel==null)+" is null");
        //TODO for some reason line 57 of bottom bar makes the app crash at this line, used this check to avoid crashing, check if better solutions are possible
       // if(Utilities.SharedViewModel.proposalsViewModel != null)
            Utilities.SharedViewModel.proposalsViewModel.getAccepted().observe(getViewLifecycleOwner(),acceptedObserver);

        Utilities.getProposals(Utilities.day, UserManager.getUserId(), HomeViewModel.filters, Types.ACCEPTED);

        TextView scheduleDate = root.findViewById(R.id.scheduledate_text);
        ScheduleDate.setTextDate(scheduleDate);

        scheduleDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = ScheduleDate.showDatePickerDialog(context);

                datePickerDialog.getDatePicker().setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);

                        ScheduleDate.setScheduleDate(calendar.getTime());
                    }
                });

                Button buttonOk = (Button) datePickerDialog.getButton(datePickerDialog.BUTTON_POSITIVE);
                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ScheduleDate.setTextDate(scheduleDate);
                        datePickerDialog.dismiss();

                        Utilities.getProposals(ScheduleDate.getScheduleLocalDate(), UserManager.getUserId(), HomeViewModel.filters, Types.ACCEPTED);
                    }
                });
            }
        });

        scheduledateText = root.findViewById(R.id.scheduledate_text);
        totalActivities = root.findViewById(R.id.total_activities);

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isPressed() && !isChecked){
                    scheduledateText.setVisibility(View.VISIBLE);
                    totalActivities.setVisibility(View.INVISIBLE);
                    Utilities.day = ScheduleDate.getScheduleLocalDate();                    
                }
                else if (buttonView.isPressed() && isChecked){
                    scheduledateText.setVisibility(View.INVISIBLE);
                    totalActivities.setVisibility(View.VISIBLE);
                    Utilities.day = null;
                }

                Utilities.getProposals(Utilities.day, UserManager.getUserId(), HomeViewModel.filters, Types.ACCEPTED);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (!simpleSwitch.isChecked()){
            GetProposals.getProposalsDate(ScheduleBar.ScheduleDate.getScheduleLocalDate(), UserManager.getUserId(), HomeViewModel.filters, Types.AVAILABLE);
            scheduledateText.setVisibility(View.VISIBLE);
            totalActivities.setVisibility(View.INVISIBLE);
        }else{
            GetProposals.getProposalsDate(null, UserManager.getUserId(), HomeViewModel.filters, Types.AVAILABLE);
            scheduledateText.setVisibility(View.INVISIBLE);
            totalActivities.setVisibility(View.VISIBLE);
        }*/

        if(Utilities.day == null){
            if(!simpleSwitch.isChecked()){
                simpleSwitch.setChecked(true);
                scheduledateText.setVisibility(View.INVISIBLE);
                totalActivities.setVisibility(View.VISIBLE);
            }
        }
        else{
            if(simpleSwitch.isChecked()){
                simpleSwitch.setChecked(false);
                scheduledateText.setVisibility(View.VISIBLE);
                totalActivities.setVisibility(View.INVISIBLE);
            }
        }

        Utilities.getProposals(Utilities.day, UserManager.getUserId(), HomeViewModel.filters, Types.ACCEPTED);
    }
}