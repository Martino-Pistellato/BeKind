package com.example.bekind_v2.UILayer;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.Authentication.LoginActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        Log.e("SCREEN", "sono qui");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean is_dark = sharedPreferences.getBoolean("dark_theme", false);
        if(is_dark)
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);

        if(!BottomBarViewModel.isLogged())
            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        else
            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        finish();

    }
}