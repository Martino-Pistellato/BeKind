package com.example.bekind_v2.UILayer.ui.dashboard;

import androidx.lifecycle.ViewModel;

import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.Utilities.PostTypes;
import com.example.bekind_v2.Utilities.Utilities;

import java.util.ArrayList;

public class DashboardViewModel extends ViewModel {
    public static ArrayList<String> filters = new ArrayList<>();

    public void manageFilter(String filter){
        Utilities.manageFilter(filter,filters);
        Utilities.getPosts(Utilities.day, UserManager.getUserId(), filters, PostTypes.OTHERSPOSTS);
    }

}