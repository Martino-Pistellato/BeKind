package com.example.bekind_v2.UILayer.ui.available;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bekind_v2.Utilities.Utilities;

public class AvailableViewModel extends ViewModel {
    public void manageFilter(String filter){
        Utilities.manageFilter(filter, Utilities.SharedViewModel.filters);
    }
}