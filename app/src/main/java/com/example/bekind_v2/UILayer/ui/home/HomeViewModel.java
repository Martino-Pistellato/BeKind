package com.example.bekind_v2.UILayer.ui.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
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
                return new ProposedFragment(proposalsViewModel);
            }
        }

        //return number of Fragments
        @Override
        public int getItemCount() {
            return 2;
        }
    }
}