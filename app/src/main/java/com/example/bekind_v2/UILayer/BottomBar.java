package com.example.bekind_v2.UILayer;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.R;
import com.example.bekind_v2.Utilities.CreateActivityDialog;
import com.example.bekind_v2.Utilities.MapViewModel;
import com.example.bekind_v2.Utilities.PostTypes;
import com.example.bekind_v2.Utilities.PostsViewModel;
import com.example.bekind_v2.Utilities.ProposalsViewModel;
import com.example.bekind_v2.Utilities.Utilities;
import com.example.bekind_v2.databinding.ActivityBottomBarBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.util.ArrayList;

public class BottomBar extends AppCompatActivity {

    private ActivityBottomBarBinding binding;
    private BottomBarViewModel bottomBarViewModel;
    private FloatingActionButton addProposalButton;
    private MapViewModel mapViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BottomBarViewModel.clearProposals();
        BottomBarViewModel.clearPosts();

        Utilities.SharedViewModel.postsViewModel = new ViewModelProvider(this).get(PostsViewModel.class);
        Utilities.SharedViewModel.proposalsViewModel = new ViewModelProvider(this).get(ProposalsViewModel.class);
        Utilities.day = LocalDate.now();

        binding = ActivityBottomBarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        /* Passing each menu ID as a set of Ids because each
        * menu should be considered as top level destinations.*/
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_available, R.id.navigation_dashboard, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        bottomBarViewModel = new ViewModelProvider(this).get(BottomBarViewModel.class);
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        addProposalButton = findViewById(R.id.add_proposal_btn);

        if(sharedPreferences.getBoolean("first_time", true)){
            sharedPreferences.edit().putBoolean("first_time", false).apply();

            Dialog tutorialDialog = new Dialog(this);
            View view = View.inflate(this, R.layout.tutorial_popup, null);
            tutorialDialog.setContentView(view);
            tutorialDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Button continueBtn = view.findViewById(R.id.tutorial_continue_btn), backBtn = view.findViewById(R.id.tutorial_back_btn);

            ImageView image = view.findViewById(R.id.tutorial_image);
            TextView description = view.findViewById(R.id.tutorial_text);

            continueBtn.setOnClickListener(v -> {
                SettingsViewModel.index++;
                if (SettingsViewModel.index == 12) {
                    SettingsViewModel.index = 1;
                    tutorialDialog.dismiss();
                }
                else {
                    if (SettingsViewModel.index > 1) backBtn.setVisibility(View.VISIBLE);
                    if (SettingsViewModel.index == 11) continueBtn.setText(R.string.close);
                    SettingsViewModel.setContent(this, image, description);
                }
            });

            backBtn.setOnClickListener(v -> {
                SettingsViewModel.index--;
                if (SettingsViewModel.index == 1) backBtn.setVisibility(View.INVISIBLE);
                SettingsViewModel.setContent(this, image, description);
            });

            tutorialDialog.show();
        }



        addProposalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog choose_dialog = new Dialog(BottomBar.this);
                Button activityBtn, postBtn;

                choose_dialog.setContentView(R.layout.choose_popup);
                choose_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                activityBtn = choose_dialog.findViewById(R.id.activity_btn);
                postBtn = choose_dialog.findViewById(R.id.post_btn);

                activityBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogFragment dialogFragment = new CreateActivityDialog(bottomBarViewModel, choose_dialog, mapViewModel);

                        dialogFragment.show(getSupportFragmentManager(), null);
                    }
                });

                postBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog = new Dialog(BottomBar.this);
                        TextInputEditText title, body;
                        Button closeBtn, publishBtn;

                        dialog.setContentView(R.layout.add_post_popup); //set content of dialog (look in layout folder for new_activity_dialog file)
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCanceledOnTouchOutside(false); //prevents dialog to close when clicking outside of it

                        Chip eventChip, animalChip, utilitiesChip, transportChip, randomChip, criminalChip;

                        eventChip = dialog.findViewById(R.id.event_chip_popup);
                        animalChip = dialog.findViewById(R.id.animal_chip_popup);
                        utilitiesChip = dialog.findViewById(R.id.utilities_chip_popup);
                        transportChip = dialog.findViewById(R.id.transport_chip_popup);
                        randomChip = dialog.findViewById(R.id.random_chip_popup);
                        criminalChip = dialog.findViewById(R.id.criminal_chip_popup);

                        eventChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) { bottomBarViewModel.manageFilterPost(eventChip.getText().toString()); }
                        });
                        animalChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) { bottomBarViewModel.manageFilterPost(animalChip.getText().toString()); }
                        });
                        utilitiesChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) { bottomBarViewModel.manageFilterPost(utilitiesChip.getText().toString()); }
                        });
                        transportChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) { bottomBarViewModel.manageFilterPost(transportChip.getText().toString()); }
                        });
                        randomChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) { bottomBarViewModel.manageFilterPost(randomChip.getText().toString()); }
                        });
                        criminalChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) { bottomBarViewModel.manageFilterPost(criminalChip.getText().toString()); }
                        });

                        closeBtn = dialog.findViewById(R.id.close_btn); //button for closing dialog
                        publishBtn = dialog.findViewById(R.id.publish_btn); //button to confirm and publish activity
                        title = dialog.findViewById(R.id.post_title);
                        body = dialog.findViewById(R.id.post_body);

                        closeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss(); //close dialog
                            }
                        });

                        publishBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String postTitle = title.getText().toString().trim();
                                String postBody = body.getText().toString().trim();

                                if(!bottomBarViewModel.checkPostConstraints(title, postTitle, body, postBody, getApplicationContext()))
                                    Toast.makeText(getApplicationContext(), R.string.fields_error, Toast.LENGTH_SHORT).show();
                                else
                                    bottomBarViewModel.createPost(postTitle, postBody, result -> { Utilities.getPosts(Utilities.day, UserManager.getUserId(), new ArrayList<>(), PostTypes.MYPOSTS); });
                                
                                dialog.dismiss();
                                choose_dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
            choose_dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, SettingsActivity.class));
        return super.onOptionsItemSelected(item);
    }
}

