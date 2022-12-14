package com.example.bekind_v2.UILayer.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.bekind_v2.Utilities.ProposalsViewModel;

public class HomeViewModel extends ViewModel {
    public static class HomeActivityViewPagerAdapter extends FragmentStateAdapter {
        private ProposalsViewModel proposalsViewModel;
        //constructor, necessary
        public HomeActivityViewPagerAdapter(@NonNull Fragment fragment, ProposalsViewModel proposalsViewModel) {
            super(fragment);
            this.proposalsViewModel = proposalsViewModel;
        }

        //return new Fragment based on position
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if(position == 1) {
                return new AcceptedFragment(proposalsViewModel);
            }
            else {
                return new ProposalFragment(proposalsViewModel);
            }
        }

        //return number of Fragments
        @Override
        public int getItemCount() {
            return 2;
        }
    }
}