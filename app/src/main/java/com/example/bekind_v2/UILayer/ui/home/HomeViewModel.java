package com.example.bekind_v2.UILayer.ui.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.bekind_v2.Utilities.ProposalsViewModel;
import com.example.bekind_v2.Utilities.Utilities;

public class HomeViewModel extends ViewModel {
    public static class HomeActivityViewPagerAdapter extends FragmentStateAdapter {
        //constructor, necessary
        public HomeActivityViewPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        //return new Fragment based on position
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if(position == 1) {
                return new AcceptedFragment();
            }
            else {
                return new ProposedFragment();
            }
        }

        //return number of Fragments
        @Override
        public int getItemCount() {
            return 2;
        }
    }
}