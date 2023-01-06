package com.example.bekind_v2.UILayer.ui.available;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.example.bekind_v2.Utilities.MapViewModel;
import com.example.bekind_v2.Utilities.ProposalRecyclerViewAdapter;
import com.example.bekind_v2.Utilities.ProposalsViewModel;
import com.example.bekind_v2.Utilities.ScheduleBar;
import com.example.bekind_v2.Utilities.Types;
import com.example.bekind_v2.Utilities.Utilities;
import com.example.bekind_v2.databinding.FragmentAvailableBinding;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.chip.Chip;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;

public class AvailableFragment extends Fragment {

    private FragmentAvailableBinding binding;
    private final ProposalsViewModel proposalsViewModel;
    private SwitchCompat simpleSwitch;
    private TextView totalActivities, scheduledateText;

    public AvailableFragment(){
        this.proposalsViewModel = Utilities.SharedViewModel.proposalsViewModel;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AvailableViewModel availableViewModel = new ViewModelProvider(this).get(AvailableViewModel.class);
        MapViewModel mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        binding = FragmentAvailableBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Chip shoppingChip, houseworksChip, cleaningChip, transportChip, randomChip;

        shoppingChip = root.findViewById(R.id.shopping_chip);
        houseworksChip = root.findViewById(R.id.houseworks_chip);
        cleaningChip = root.findViewById(R.id.cleaning_chip);
        transportChip = root.findViewById(R.id.transport_chip);
        randomChip = root.findViewById(R.id.random_chip);

        shoppingChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { availableViewModel.manageFilter(shoppingChip.getText().toString()); }
        });
        houseworksChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { availableViewModel.manageFilter(houseworksChip.getText().toString()); }
        });
        cleaningChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { availableViewModel.manageFilter(cleaningChip.getText().toString()); }
        });
        transportChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { availableViewModel.manageFilter(transportChip.getText().toString()); }
        });
        randomChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { availableViewModel.manageFilter(randomChip.getText().toString()); }
        });

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view_proposal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Context context = this.getContext();

        LinearLayout mapContainer = root.findViewById(R.id.map_container);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map_prop);

        Dialog mapdialog = new Dialog(context);
        mapdialog.setCanceledOnTouchOutside(true);
        ((ViewGroup)mapContainer.getParent()).removeView(mapContainer);
        mapdialog.setContentView(mapContainer);

        final Observer<ArrayList<ProposalRepository.Proposal>> availableObserver = new Observer<ArrayList<ProposalRepository.Proposal>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<ProposalRepository.Proposal> available) {
                ProposalRecyclerViewAdapter adapter = new ProposalRecyclerViewAdapter(mapViewModel, mapdialog, mapFragment, available, getContext(),getActivity(), Types.AVAILABLE);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };

        proposalsViewModel.getAvailable().observe(getViewLifecycleOwner(),availableObserver);

        //Utilities.getProposals(Utilities.day, UserManager.getUserId(), AvailableViewModel.filters, Types.AVAILABLE);

        scheduledateText = root.findViewById(R.id.scheduledate_text);
        totalActivities = root.findViewById(R.id.total_activities);
        ScheduleBar.ScheduleDate.setTextDate(scheduledateText);

        simpleSwitch = root.findViewById(R.id.simpleSwitch);

        scheduledateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = ScheduleBar.ScheduleDate.showDatePickerDialog(context);

                datePickerDialog.getDatePicker().setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);

                        ScheduleBar.ScheduleDate.setScheduleDate(calendar.getTime());
                        Utilities.day = calendar.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    }
                });

                Button buttonOk = (Button) datePickerDialog.getButton(datePickerDialog.BUTTON_POSITIVE);
                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ScheduleBar.ScheduleDate.setTextDate(scheduledateText);
                        datePickerDialog.dismiss();

                        Utilities.getProposals(Utilities.day, UserManager.getUserId(), AvailableViewModel.filters, Types.AVAILABLE);
                    }
                });
            }
        });

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isPressed() && !isChecked){
                    scheduledateText.setVisibility(View.VISIBLE);
                    totalActivities.setVisibility(View.INVISIBLE);
                    Utilities.day = ScheduleBar.ScheduleDate.getScheduleLocalDate();

                }
                else if (buttonView.isPressed() && isChecked){
                    scheduledateText.setVisibility(View.INVISIBLE);
                    totalActivities.setVisibility(View.VISIBLE);
                    Utilities.day = null;
                }
                Utilities.getProposals(Utilities.day, UserManager.getUserId(), AvailableViewModel.filters, Types.AVAILABLE);
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

        if(Utilities.day == null){
            if(!simpleSwitch.isChecked()){
                simpleSwitch.setChecked(true);
            }
            scheduledateText.setVisibility(View.INVISIBLE);
            totalActivities.setVisibility(View.VISIBLE);
        }
        else{
            if(simpleSwitch.isChecked()){
                simpleSwitch.setChecked(false);
            }
            scheduledateText.setVisibility(View.VISIBLE);
            totalActivities.setVisibility(View.INVISIBLE);
        }

        Utilities.getProposals(Utilities.day, UserManager.getUserId(), AvailableViewModel.filters, Types.AVAILABLE);
    }
}