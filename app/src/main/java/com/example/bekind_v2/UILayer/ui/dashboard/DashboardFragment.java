package com.example.bekind_v2.UILayer.ui.dashboard;

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
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bekind_v2.R;
import com.example.bekind_v2.Utilities.ScheduleBar;
import com.example.bekind_v2.databinding.FragmentDashboardBinding;

import java.util.Calendar;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

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