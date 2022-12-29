package com.example.bekind_v2.UILayer.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bekind_v2.DataLayer.PostRepository;
import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.R;
import com.example.bekind_v2.Utilities.PostRecyclerViewAdapter;
import com.example.bekind_v2.Utilities.PostTypes;
import com.example.bekind_v2.Utilities.PostsViewModel;
import com.example.bekind_v2.Utilities.ProposalRecyclerViewAdapter;
import com.example.bekind_v2.Utilities.ProposalsViewModel;
import com.example.bekind_v2.Utilities.Types;
import com.example.bekind_v2.Utilities.Utilities;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

public class MyPostsFragment extends Fragment {
    private final PostsViewModel postsViewModel;

    public MyPostsFragment() {
        this.postsViewModel = Utilities.SharedViewModel.postsViewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myposts, container, false);

        Chip eventChip, utilitiesChip, criminalChip, transportChip, animalChip, randomChip;
        eventChip = view.findViewById(R.id.event_chip);
        utilitiesChip = view.findViewById(R.id.utilities_chip);
        criminalChip = view.findViewById(R.id.criminal_chip);
        transportChip = view.findViewById(R.id.transport_chip);
        animalChip = view.findViewById(R.id.animal_chip);
        randomChip = view.findViewById(R.id.random_chip);

        eventChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileViewModel.managePostsFilter(eventChip.getText().toString());
            }
        });

        utilitiesChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ProfileViewModel.managePostsFilter(utilitiesChip.getText().toString()); }
        });

        criminalChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ProfileViewModel.managePostsFilter(criminalChip.getText().toString()); }
        });

        transportChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ProfileViewModel.managePostsFilter(transportChip.getText().toString()); }
        });

        animalChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ProfileViewModel.managePostsFilter(animalChip.getText().toString()); }
        });

        randomChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ProfileViewModel.managePostsFilter(randomChip.getText().toString()); }
        });



        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_post);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final Observer<ArrayList<PostRepository.Post>> postObserver = new Observer<ArrayList<PostRepository.Post>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<PostRepository.Post> posts) {
                PostRecyclerViewAdapter adapter = new PostRecyclerViewAdapter(posts, getContext(), PostTypes.MYPOSTS);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };

        postsViewModel.getMyPosts().observe(getViewLifecycleOwner(),postObserver);
        
        Utilities.getPosts(Utilities.day, UserManager.getUserId(), ProfileViewModel.postsFilters, PostTypes.MYPOSTS);
        
        return view;
    }
}
