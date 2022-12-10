package com.example.bekind_v2.UILayer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bekind_v2.R;
import com.example.bekind_v2.databinding.ActivityBottomBarBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.bekind_v2.databinding.ActivityBottomBarBinding;

public class BottomBar extends AppCompatActivity {

    private ActivityBottomBarBinding binding;
    private Button addActivityButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBottomBarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_available,  R.id.navigation_dashboard, R.id.navigation_profile)
                    .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_bottom_bar);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        addActivityButton = findViewById(R.id.add_activity_btn);
        addActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}