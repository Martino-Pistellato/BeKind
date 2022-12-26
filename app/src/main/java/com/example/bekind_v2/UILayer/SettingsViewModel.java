package com.example.bekind_v2.UILayer;

import androidx.lifecycle.ViewModel;
import com.example.bekind_v2.DataLayer.UserManager;

public class SettingsViewModel extends ViewModel {
    public static void logout(){
        UserManager.logout();
    }
}
