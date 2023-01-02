package com.example.bekind_v2.UILayer.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.bekind_v2.DataLayer.NeighbourhoodRepository;
import com.example.bekind_v2.R;
//import com.example.bekind_v2.UILayer.NeighbourhoodActivity;
import com.example.bekind_v2.UILayer.BottomBar;
import com.example.bekind_v2.UILayer.NeighbourhoodFragment;
import com.example.bekind_v2.UILayer.ui.home.HomeFragment;
import com.example.bekind_v2.Utilities.MyCallback;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class RegistrationFragment2 extends Fragment {

    private AuthenticationViewModel authenticationViewModel;

    public RegistrationFragment2(AuthenticationViewModel authenticationViewModel){
        this.authenticationViewModel = authenticationViewModel;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_registration2, container, false);

        TextView textCreateNeighbourhood = view.findViewById(R.id.text_create_neigh);
        TextInputEditText //city = view.findViewById(R.id.user_city), //neighbourhood = view.findViewById(R.id.user_neigh),
                          street = view.findViewById(R.id.user_street), streetNumber = view.findViewById(R.id.street_number);
        Button backBtn = view.findViewById(R.id.back_button), continueBtn = view.findViewById(R.id.continue_button);
        AutoCompleteTextView neighbourhood = view.findViewById(R.id.user_neigh), city = view.findViewById(R.id.user_city);
        //ProgressBar progressBar = view.findViewById(R.id.progressbar);

        authenticationViewModel.getLocationData(city,neighbourhood,street,streetNumber);

        if(city.getText().toString().isEmpty())
            neighbourhood.setEnabled(false);

        city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!city.getText().toString().isEmpty())
                    neighbourhood.setEnabled(true);
                else
                    neighbourhood.setEnabled(false);
            }
        });

        NeighbourhoodRepository.getCities(new MyCallback() {
            @Override
            public void onCallback(Object result) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, (ArrayList<String>) result);
                city.setAdapter(adapter);
            }
        });

        neighbourhood.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    NeighbourhoodRepository.getNeighbourhoods(city.getText().toString().toLowerCase(), new MyCallback() {
                        @Override
                        public void onCallback(Object result) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, (ArrayList<String>) result);
                            neighbourhood.setAdapter(adapter);
                        }
                    });
                }
            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userCity = city.getText().toString().trim(), userNeighbourhood = neighbourhood.getText().toString().trim(),
                        userStreet = street.getText().toString().trim(), userStreetNumber = streetNumber.getText().toString().trim();

                authenticationViewModel.saveLocationData(userCity, userNeighbourhood, userStreet, userStreetNumber);
                authenticationViewModel.changeFragment(getActivity(), R.id.fragment_registration2, authenticationViewModel);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userCity = city.getText().toString().trim(), userNeighbourhood = neighbourhood.getText().toString().trim(),
                       userStreet = street.getText().toString().trim(), userStreetNumber = streetNumber.getText().toString().trim();

                authenticationViewModel.saveLocationData(userCity, userNeighbourhood, userStreet, userStreetNumber);
                authenticationViewModel.checkLocationFields(city, userCity, neighbourhood, userNeighbourhood, street, userStreet, streetNumber, userStreetNumber, new MyCallback() {
                    @Override
                    public void onCallback(Object result) {
                        if(!(boolean) result) {
                            Toast.makeText(getContext(), "Errore: i campi non sono stati riempiti correttamente", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.e("Create USER STEP 1", "call to authentication view model");
                            authenticationViewModel.createUser(getContext(), new MyCallback() {
                                @Override
                                public void onCallback(Object result) {
                                    if (result != null){
                                        Log.e("STEP 7", "here if link is clicked, ready to start app");
                                        startActivity(new Intent(getContext(), BottomBar.class));
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        textCreateNeighbourhood.setOnClickListener((v) -> {
            String userCity = city.getText().toString().trim(), userNeighbourhood = "",
                   userStreet = street.getText().toString().trim(), userStreetNumber = streetNumber.getText().toString().trim();
            authenticationViewModel.saveLocationData(userCity, userNeighbourhood, userStreet, userStreetNumber);

            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            NeighbourhoodFragment neighbourhoodFragment = new NeighbourhoodFragment(authenticationViewModel);
            fragmentTransaction.replace(R.id.fragment_container, neighbourhoodFragment).commit();
        }); //TODO:make a generic replacefragment
        return view;
    }
}