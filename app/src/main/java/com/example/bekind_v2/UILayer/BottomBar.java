package com.example.bekind_v2.UILayer;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.bekind_v2.DataLayer.PostRepository;
import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.ui.home.HomeViewModel;
import com.example.bekind_v2.Utilities.MyCallback;
import com.example.bekind_v2.Utilities.PostTypes;
import com.example.bekind_v2.Utilities.PostsViewModel;
import com.example.bekind_v2.Utilities.ProposalsViewModel;
import com.example.bekind_v2.Utilities.Types;
import com.example.bekind_v2.Utilities.Utilities;
import com.example.bekind_v2.databinding.ActivityBottomBarBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.graphics.GraphicsLayerScope;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class BottomBar extends AppCompatActivity {

    private ActivityBottomBarBinding binding;
    private BottomBarViewModel bottomBarViewModel;
    private PostsViewModel postsViewModel;
    private FloatingActionButton addProposalButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BottomBarViewModel.clearProposals();
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
        postsViewModel = new ViewModelProvider(this).get(PostsViewModel.class);
        Utilities.SharedViewModel.proposalsViewModel = new ViewModelProvider(this).get(ProposalsViewModel.class);
        Utilities.SharedViewModel.day = LocalDate.now();
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
                        TextInputEditText title, body;
                        DatePicker expiringDate;
                        TimePicker expiringHour;
                        Chip shoppingChip, houseworksChip, cleaningChip, transportChip, randomChip;
                        Button closeBtn, publishBtn;

                        dialog.setContentView(R.layout.add_proposal_popup); //set content of dialog (look in layout folder for new_activity_dialog file)
                        dialog.setCanceledOnTouchOutside(false); //prevents dialog to close when clicking outside of it

                        shoppingChip = dialog.findViewById(R.id.shopping_chip_popup);
                        houseworksChip = dialog.findViewById(R.id.houseworks_chip_popup);
                        cleaningChip = dialog.findViewById(R.id.cleaning_chip_popup);
                        transportChip = dialog.findViewById(R.id.transport_chip_popup);
                        randomChip = dialog.findViewById(R.id.random_chip_popup);

                        shoppingChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomBarViewModel.manageFilter(shoppingChip.getText().toString());
                            }
                        });
                        houseworksChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomBarViewModel.manageFilter(houseworksChip.getText().toString());
                            }
                        });
                        cleaningChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomBarViewModel.manageFilter(cleaningChip.getText().toString());
                            }
                        });
                        transportChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomBarViewModel.manageFilter(transportChip.getText().toString());
                            }
                        });
                        randomChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomBarViewModel.manageFilter(randomChip.getText().toString());
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

                                if (!bottomBarViewModel.checkConstraints(title, proposalTitle, body, proposalBody, proposalExpiringDate))
                                    Toast.makeText(getApplicationContext(), "Errore: i campi non sono stati riempiti correttamente", Toast.LENGTH_SHORT).show();
                                else {   //all the parameters in input are correct
                                    bottomBarViewModel.createProposal(proposalTitle, proposalBody, proposalExpiringDate);

                                    ProposalRepository.getProposals(Utilities.SharedViewModel.day, UserManager.getUserId(), HomeViewModel.filters, Types.PROPOSED, new MyCallback<ArrayList<ProposalRepository.Proposal>>() {
                                                @Override
                                                public void onCallback(ArrayList<ProposalRepository.Proposal> result) {
                                                    Utilities.SharedViewModel.proposalsViewModel.getProposed().setValue(result);
                                                }
                                            }
                                    );
                                    dialog.dismiss();
                                    choose_dialog.dismiss();
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
                                    PostRepository.getPosts(PostTypes.MYPOSTS, UserManager.getUserId(), new MyCallback<ArrayList<PostRepository.Post>>() {
                                                @Override
                                                public void onCallback(ArrayList<PostRepository.Post> result) {
                                                    postsViewModel.getPosts().setValue(result);
                                                }
                                            }
                                    );
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
}

