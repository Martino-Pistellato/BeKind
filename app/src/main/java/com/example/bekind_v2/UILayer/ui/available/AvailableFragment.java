package com.example.bekind_v2.UILayer.ui.available;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.BottomBar;
import com.example.bekind_v2.Utilities.ProposalRecyclerViewAdapter;
import com.example.bekind_v2.Utilities.ProposalsViewModel;
import com.example.bekind_v2.Utilities.ScheduleBar;
import com.example.bekind_v2.Utilities.Utilities;
import com.example.bekind_v2.databinding.FragmentAvailableBinding;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Calendar;

public class AvailableFragment extends Fragment {

    private FragmentAvailableBinding binding;
    private final ProposalsViewModel proposalsViewModel;

    public AvailableFragment(){
        this.proposalsViewModel = Utilities.SharedViewModel.proposalsViewModel;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AvailableViewModel availableViewModel = new ViewModelProvider(this).get(AvailableViewModel.class);
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

        final Observer<ArrayList<ProposalRepository.Proposal>> availableObserver = new Observer<ArrayList<ProposalRepository.Proposal>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<ProposalRepository.Proposal> available) {
                ProposalRecyclerViewAdapter adapter = new ProposalRecyclerViewAdapter(available, getContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };

        proposalsViewModel.getAvailable().observe(getViewLifecycleOwner(),availableObserver);


        TextView scheduleDate = root.findViewById(R.id.scheduledate_text);
        ScheduleBar.ScheduleDate.setTextDate(scheduleDate);

        SwitchCompat simpleSwitch = root.findViewById(R.id.simpleSwitch);
        Context context = this.getContext();

        scheduleDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = ScheduleBar.ScheduleDate.showDatePickerDialog(context);

                datePickerDialog.getDatePicker().setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);

                        ScheduleBar.ScheduleDate.setScheduleDate(calendar.getTime());
                    }

                });

                Button buttonOk = (Button) datePickerDialog.getButton(datePickerDialog.BUTTON_POSITIVE);
                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ScheduleBar.ScheduleDate.setTextDate(scheduleDate);
                        datePickerDialog.dismiss();
                    }
                });
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}