package com.example.bekind_v2.UILayer.ui.dashboard;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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

import com.example.bekind_v2.DataLayer.PostRepository;
import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.ui.home.HomeViewModel;
import com.example.bekind_v2.Utilities.MyCallback;
import com.example.bekind_v2.Utilities.PostRecyclerViewAdapter;
import com.example.bekind_v2.Utilities.PostTypes;
import com.example.bekind_v2.Utilities.PostsViewModel;
import com.example.bekind_v2.Utilities.ProposalRecyclerViewAdapter;
import com.example.bekind_v2.Utilities.ScheduleBar;
import com.example.bekind_v2.Utilities.Types;
import com.example.bekind_v2.Utilities.Utilities;
import com.example.bekind_v2.databinding.FragmentDashboardBinding;

import java.util.ArrayList;
import java.util.Calendar;

//TODO add filters and datepicker

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        PostsViewModel postsViewModel = new ViewModelProvider(this).get(PostsViewModel.class);
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view_post);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final Observer<ArrayList<PostRepository.Post>> postObserver = new Observer<ArrayList<PostRepository.Post>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<PostRepository.Post> posts) {
                PostRecyclerViewAdapter adapter = new PostRecyclerViewAdapter(posts, getContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };

        postsViewModel.getPosts().observe(getViewLifecycleOwner(), postObserver);

        PostRepository.getPosts(PostTypes.OTHERSPOSTS, UserManager.getUserId(), new MyCallback<ArrayList<PostRepository.Post>>() {
                                    @Override
                                    public void onCallback(ArrayList<PostRepository.Post> result) {
                                        postsViewModel.getPosts().setValue(result);
                                    }
                                }
        );


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