package com.example.bekind_v2.UILayer.Authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.example.bekind_v2.R;
import com.example.bekind_v2.Utilities.MapViewModel;

public class RegistrationActivity extends AppCompatActivity {

    AuthenticationViewModel authenticationViewModel;
    MapViewModel mapViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        authenticationViewModel = new ViewModelProvider(this).get(AuthenticationViewModel.class);
        this.mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        setContentView(R.layout.activity_registration);

        RegistrationFragment1 newFragment = new RegistrationFragment1(authenticationViewModel, mapViewModel);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .commit();
    }
}