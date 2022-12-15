package com.example.bekind_v2.UILayer.ui.available;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bekind_v2.UILayer.BottomBar;
import com.example.bekind_v2.Utilities.ProposalsViewModel;
import com.example.bekind_v2.databinding.FragmentAvailableBinding;

public class AvailableFragment extends Fragment {

    private FragmentAvailableBinding binding;
    private ProposalsViewModel proposalsViewModel;

    public AvailableFragment(){
        this.proposalsViewModel = BottomBar.SharedViewModel.proposalsViewModel;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AvailableViewModel availableViewModel =
                new ViewModelProvider(this).get(AvailableViewModel.class);

        binding = FragmentAvailableBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAvailable;
        availableViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}