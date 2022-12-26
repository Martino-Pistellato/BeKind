package com.example.bekind_v2.UILayer;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.ui.dashboard.DashboardViewModel;
import com.example.bekind_v2.UILayer.ui.home.HomeViewModel;
import com.example.bekind_v2.Utilities.PostTypes;
import com.example.bekind_v2.Utilities.PostsViewModel;
import com.example.bekind_v2.Utilities.ProposalsViewModel;
import com.example.bekind_v2.Utilities.Types;
import com.example.bekind_v2.Utilities.Utilities;
import com.example.bekind_v2.databinding.ActivityBottomBarBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.util.Date;

public class BottomBar extends AppCompatActivity {

    private ActivityBottomBarBinding binding;
    private BottomBarViewModel bottomBarViewModel;
    private FloatingActionButton addProposalButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BottomBarViewModel.clearProposals();
        BottomBarViewModel.clearPosts();
        Utilities.SharedViewModel.postsViewModel = new ViewModelProvider(this).get(PostsViewModel.class);
        Utilities.SharedViewModel.proposalsViewModel = new ViewModelProvider(this).get(ProposalsViewModel.class);
        Utilities.day = LocalDate.now();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean is_dark = sharedPreferences.getBoolean("dark_theme", false);
        if(is_dark)
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);


        binding = ActivityBottomBarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_available, R.id.navigation_dashboard, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        bottomBarViewModel = new ViewModelProvider(this).get(BottomBarViewModel.class);

        addProposalButton = findViewById(R.id.add_proposal_btn);

        addProposalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog choose_dialog = new Dialog(BottomBar.this);
                Button activityBtn, postBtn;

                choose_dialog.setContentView(R.layout.choose_popup);
               //choose_dialog.setCanceledOnTouchOutside(false);

                activityBtn = choose_dialog.findViewById(R.id.activity_btn);
                postBtn = choose_dialog.findViewById(R.id.post_btn);

                activityBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog = new Dialog(BottomBar.this);
                        TextInputEditText title, body, maxparticipants;
                        DatePicker expiringDate;
                        TimePicker expiringHour;
                        CheckBox groupProposal;
                        Chip shoppingChip, houseworksChip, cleaningChip, transportChip, randomChip;
                        Button closeBtn, publishBtn;

                        dialog.setContentView(R.layout.add_proposal_popup); //set content of dialog (look in layout folder for new_activity_dialog file)
                        dialog.setCanceledOnTouchOutside(false); //prevents dialog to close when clicking outside of it

                        groupProposal = dialog.findViewById(R.id.group_checkbox);
                        maxparticipants = dialog.findViewById(R.id.activity_maxparticipants);

                        groupProposal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                if(b)
                                    maxparticipants.setVisibility(View.VISIBLE);
                                else {
                                    maxparticipants.setText("");
                                    maxparticipants.setVisibility(View.GONE);
                                }
                            }
                        });

                        shoppingChip = dialog.findViewById(R.id.shopping_chip_popup);
                        houseworksChip = dialog.findViewById(R.id.houseworks_chip_popup);
                        cleaningChip = dialog.findViewById(R.id.cleaning_chip_popup);
                        transportChip = dialog.findViewById(R.id.transport_chip_popup);
                        randomChip = dialog.findViewById(R.id.random_chip_popup);

                        shoppingChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomBarViewModel.manageFilterProposal(shoppingChip.getText().toString());
                            }
                        });
                        houseworksChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomBarViewModel.manageFilterProposal(houseworksChip.getText().toString());
                            }
                        });
                        cleaningChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomBarViewModel.manageFilterProposal(cleaningChip.getText().toString());
                            }
                        });
                        transportChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomBarViewModel.manageFilterProposal(transportChip.getText().toString());
                            }
                        });
                        randomChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomBarViewModel.manageFilterProposal(randomChip.getText().toString());
                            }
                        });

                        closeBtn = dialog.findViewById(R.id.close_btn); //button for closing dialog
                        publishBtn = dialog.findViewById(R.id.publish_btn); //button to confirm and publish activity

                        title = dialog.findViewById(R.id.activity_title);
                        body = dialog.findViewById(R.id.activity_body);
                        expiringDate = dialog.findViewById(R.id.date_picker);
                        expiringHour = dialog.findViewById(R.id.time_picker);

                        bottomBarViewModel.setExpiringHour(expiringHour);
                        bottomBarViewModel.setExpiringDate(expiringDate);

                        closeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss(); //close dialog
                            }
                        });

                        publishBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Date proposalExpiringDate = BottomBarViewModel.toDate(expiringDate.getYear(), expiringDate.getMonth(), expiringDate.getDayOfMonth(), expiringHour.getHour(), expiringHour.getMinute());
                                String proposalTitle = title.getText().toString().trim(); //gets the content of the title
                                String proposalBody = body.getText().toString().trim(); //gets the content of the body


                                if(!groupProposal.isChecked()) {
                                    if (!bottomBarViewModel.checkConstraints(title, proposalTitle, body, proposalBody, proposalExpiringDate))
                                        Toast.makeText(getApplicationContext(), "Errore: i campi non sono stati riempiti correttamente", Toast.LENGTH_SHORT).show();
                                    else {   //all the parameters in input are correct
                                        bottomBarViewModel.createProposal(proposalTitle, proposalBody,1, proposalExpiringDate);

                                        Utilities.getProposals(Utilities.day, UserManager.getUserId(), HomeViewModel.filters, Types.PROPOSED);
                                        dialog.dismiss();
                                        choose_dialog.dismiss();
                                    }
                                }else{
                                    String proposalPartcipants = maxparticipants.getText().toString().trim();
                                    if(!bottomBarViewModel.checkGroupProposalConstraints(title, proposalTitle, body, proposalBody, proposalExpiringDate, maxparticipants, proposalPartcipants))
                                        Toast.makeText(getApplicationContext(), "Errore: i campi non sono stati riempiti correttamente", Toast.LENGTH_SHORT).show();
                                    else{
                                        bottomBarViewModel.createProposal(proposalTitle, proposalBody, Integer.valueOf(proposalPartcipants), proposalExpiringDate);
                                        Utilities.getProposals(Utilities.day, UserManager.getUserId(), HomeViewModel.filters, Types.PROPOSED);
                                        dialog.dismiss();
                                        choose_dialog.dismiss();
                                    }
                                }

                            }
                        });

                        dialog.show();
                    }
                });

                postBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog = new Dialog(BottomBar.this);
                        TextInputEditText title, body;
                        Button closeBtn, publishBtn;

                        dialog.setContentView(R.layout.add_post_popup); //set content of dialog (look in layout folder for new_activity_dialog file)
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


                                if(!bottomBarViewModel.checkPostConstraints(title, postTitle, body, postBody))
                                    Toast.makeText(getApplicationContext(), "Errore: i campi non sono stati riempiti correttamente", Toast.LENGTH_SHORT).show();
                                else{
                                    bottomBarViewModel.createPost(postTitle, postBody);

                                    //TODO i think that this getposts is no longer necessary ---> cancel this, or have two arrays in postsViewModel for my posts and other posts?
                                    Utilities.getPosts(Utilities.day, UserManager.getUserId(), DashboardViewModel.filters, PostTypes.MYPOSTS);
                                }
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

