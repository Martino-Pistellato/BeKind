package com.example.bekind_v2.UILayer;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.example.bekind_v2.DataLayer.UserManager;

public class SettingsViewModel extends ViewModel {
    public static int index = 1;
    public static void logout(){
        UserManager.logout();
    }
    public static boolean isLogged(){ return UserManager.isLogged(); }

    public static void setContent(Activity activity, ImageView image, TextView description){
        Glide.with(activity).load(activity.getResources().getIdentifier("tutorial_pic_"+index, "drawable", activity.getPackageName())).into(image);
        description.setText(activity.getResources().getIdentifier("description_"+index, "string", activity.getPackageName()));
    }



}
