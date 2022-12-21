package com.example.bekind_v2.UILayer.ui.profile;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bekind_v2.MainActivity;
import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.Authentication.LoginActivity;
import com.example.bekind_v2.UILayer.BottomBar;
import com.example.bekind_v2.Utilities.MyCallback;
import com.example.bekind_v2.databinding.FragmentProfileBinding;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button logoutBtn = binding.logout, updateUserData = binding.modifyProfileBtn, updateUserLocation = binding.modifyNeighBtn;
        TextView profileName = binding.profileName;

        profileViewModel.getUserName(profileName::setText);

        updateUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.popup_update1);
                dialog.setCanceledOnTouchOutside(false);

                TextInputEditText userName = dialog.findViewById(R.id.modify_profile_name), userSurname = dialog.findViewById(R.id.modify_profile_surname);
                Button confirmBtn =  dialog.findViewById(R.id.confirm_button), cancelBtn =  dialog.findViewById(R.id.cancel_button);
                TextView textCredentials = dialog.findViewById(R.id.text_change_credentials);

                userName.setText(profileViewModel.getUser().getName());
                userSurname.setText(profileViewModel.getUser().getSurname());

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss(); //close dialog
                    }
                });

                confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        profileViewModel.setName(userName.getText().toString().trim());
                        profileViewModel.setSurname(userSurname.getText().toString().trim());
                        profileViewModel.getUserName(profileName::setText);
                        profileViewModel.updateUser();
                        dialog.dismiss();
                    }
                });

                textCredentials.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //if you're inside a dialog, first you close the outer one and then you open a new one
                        //TODO:ask: should we implement a way to update user name/surname without closing the dialog and opening the credential one?
                        dialog.dismiss();
                        Dialog dialog = new Dialog(getContext());
                        dialog.setContentView(R.layout.popup_update2); //set content of dialog (look in layout folder for modify_profile_dialog file)
                        dialog.setCanceledOnTouchOutside(false); //prevents dialog to close when clicking outside of it

                        Button cancelBtn = dialog.findViewById(R.id.cancel_button); //button for closing dialog
                        Button confirmBtn = dialog.findViewById(R.id.confirm_button); //button to confirm profile modification

                        TextInputEditText email = dialog.findViewById(R.id.user_email);
                        TextInputEditText  password = dialog.findViewById(R.id.user_password);

                        email.setText(profileViewModel.getUser().getEmail());

                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss(); //close dialog
                            }
                        });

                        confirmBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                profileViewModel.setEmail(email.getText().toString().trim());
                                profileViewModel.setPassword(password.getText().toString().trim());

                                profileViewModel.updateUser();
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                });

                dialog.show();
            }
        });

        updateUserLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.fragment_registration2);
                dialog.setCanceledOnTouchOutside(false);

                TextView textCreateNeighbourhood = dialog.findViewById(R.id.text_create_neigh), title = dialog.findViewById(R.id.neighbourhood_text);
                TextInputEditText city = dialog.findViewById(R.id.user_city), neighbourhood = dialog.findViewById(R.id.user_neigh),
                                  street = dialog.findViewById(R.id.user_street), streetNumber = dialog.findViewById(R.id.street_number);
                Button cancelBtn = dialog.findViewById(R.id.back_button), continueBtn = dialog.findViewById(R.id.continue_button);

                title.setText("Modifica dati residenza");
                city.setText(profileViewModel.getUser().getCity());
                street.setText(profileViewModel.getUser().getStreet());
                streetNumber.setText(profileViewModel.getUser().getStreet_number());
                profileViewModel.getNeighbourhood(profileViewModel.getUser().getNeighbourhoodID(), neighbourhood::setText);

                continueBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileViewModel.setCity(city.getText().toString().trim());
                        profileViewModel.setStreet(street.getText().toString().trim());
                        profileViewModel.setStreet_number(streetNumber.getText().toString().trim());
                        profileViewModel.setNeighbourhood(neighbourhood, new MyCallback<Boolean>() {
                            @Override
                            public void onCallback(Boolean result) {
                                profileViewModel.updateUser();
                            }
                        });

                        profileViewModel.getUserName(profileName::setText);
                        dialog.dismiss();
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                textCreateNeighbourhood.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileViewModel.setCity(city.getText().toString().trim());
                        profileViewModel.setStreet(street.getText().toString().trim());
                        profileViewModel.setStreet_number(streetNumber.getText().toString().trim());

                        dialog.dismiss();
                        Dialog dialog = new Dialog(getContext());
                        dialog.setContentView(R.layout.fragment_neighbourhood);
                        dialog.setCanceledOnTouchOutside(false);

                        TextInputEditText neighbourhood = dialog.findViewById(R.id.neigh_name);
                        Button backBtn = dialog.findViewById(R.id.back_button), confirmBtn = dialog.findViewById(R.id.continue_button);

                        backBtn.setText("CHIUDI");
                        confirmBtn.setText("CONFERMA");

                        backBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        confirmBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                profileViewModel.createNeighbourhood(neighbourhood.getText().toString().trim(), (x) -> {
                                   if (x)
                                       profileViewModel.setNeighbourhood(neighbourhood, (y) -> {
                                           profileViewModel.updateUser();

                                           profileViewModel.getUserName(profileName::setText);
                                           dialog.dismiss();
                                       });
                                   else {
                                       neighbourhood.setError("Il quartiere esiste già");
                                   }
                                });
                            }
                        });

                        dialog.show();
                    }
                });

                dialog.show();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileViewModel.logout();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}