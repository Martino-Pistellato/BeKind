package com.example.bekind_v2.UILayer.Authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.example.bekind_v2.DataLayer.NeighbourhoodRepository;
import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.BottomBar;
import com.example.bekind_v2.UILayer.NeighbourhoodFragment;
import com.example.bekind_v2.Utilities.MapViewModel;
import com.example.bekind_v2.Utilities.MyCallback;
import com.example.bekind_v2.Utilities.Utilities;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.textfield.TextInputEditText;


import java.util.ArrayList;

public class RegistrationFragment2 extends Fragment {

    private AuthenticationViewModel authenticationViewModel;
    private MapViewModel mapViewModel;

    public RegistrationFragment2(){}

    public RegistrationFragment2(AuthenticationViewModel authenticationViewModel, MapViewModel mapViewModel) {
        this.authenticationViewModel = authenticationViewModel;
        this.mapViewModel = mapViewModel;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Context context = this.getContext();
        View view = inflater.inflate(R.layout.fragment_registration2, container, false);

        TextView textCreateNeighbourhood = view.findViewById(R.id.text_create_neigh);
        TextInputEditText city = view.findViewById(R.id.user_city), //neighbourhood = view.findViewById(R.id.user_neigh),
                street = view.findViewById(R.id.user_street), streetNumber = view.findViewById(R.id.street_number);
        Button backBtn = view.findViewById(R.id.back_button), continueBtn = view.findViewById(R.id.continue_button);
        AutoCompleteTextView neighbourhood = view.findViewById(R.id.user_neigh);
        //ProgressBar progressBar = view.findViewById(R.id.progressbar);
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        //if we have previously inserted something in the form fields, we get those data and set the fields correctly
        authenticationViewModel.getLocationData(city, neighbourhood, street, streetNumber);

        /*get the coordinates from the address (city+street+streetnumber)
        * if nothing is returned (form fields are empty or address is invalid), coord will be null*/
        LatLng coord = mapViewModel.getCoordinatesFromAddress(requireContext(), city.getText().toString().trim(), street.getText().toString().trim(), streetNumber.getText().toString().trim());

        /*initialize the map, setting a marker on the given coordinates (if coord != null) and
        * set the search bar for addresses and the map
        * if necessary, it will ask for geolocalization permission*/
        mapViewModel.initializeMap(getActivity(), getContext(), autocompleteFragment, mapFragment, city, street, streetNumber, coord, null);

        //if the city form field is empty, the neighbourhood form field is not editable (we cannot write in it)
        if (city.getText().toString().isEmpty())
            neighbourhood.setEnabled(false);

        /*the city form field has a listener for changes in text
        * if the city form field is empty, neighbourhood form field is not editable
        * else, it is*/
        city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                neighbourhood.setEnabled(!city.getText().toString().isEmpty());
            }
        });

        /*neighbourhood form field has a focus changed listener
        * whenever we select this form field (N.B. this is possible only if city form filed is NOT empty)
        * a method to get all the neighbourhoods associated with the city specified in the city form field is triggered
        * while writing on this field, if any result of the query matches the written text, it will be shown in a drop down list */
        neighbourhood.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    NeighbourhoodRepository.getNeighbourhoods(city.getText().toString(), new MyCallback<ArrayList<String>>() {
                        @Override
                        public void onCallback(ArrayList<String> result) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, result);
                            neighbourhood.setAdapter(adapter);
                        }
                    });
                }
            }
        });


        //if we press the back button, we save the location data inserted in the form fields and we are redirected to the first page of the registration
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userCity = city.getText().toString().trim(), userNeighbourhood = neighbourhood.getText().toString().trim(),
                        userStreet = street.getText().toString().trim(), userStreetNumber = streetNumber.getText().toString().trim();

                authenticationViewModel.saveLocationData(userCity, userNeighbourhood, userStreet, userStreetNumber);
                authenticationViewModel.changeFragment(getActivity(), R.id.fragment_registration2, authenticationViewModel, mapViewModel);
            }
        });

        //if we press the continue button
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*we extract the texts from the fields and we convert them to a proper form
                * (Ex. venezia, VENEZIA, vENezIa, ecc.., will all be converted to Venezia)*/
                String userCity = city.getText().toString().trim(), userNeighbourhood = neighbourhood.getText().toString().trim(),
                        userStreet = street.getText().toString().trim(), userStreetNumber = streetNumber.getText().toString().trim();
                userCity = Utilities.convertToProperForm(userCity);
                userStreet = Utilities.convertToProperForm(userStreet);
                userStreetNumber = Utilities.convertToProperForm(userStreetNumber);
                userNeighbourhood = Utilities.convertToProperForm(userNeighbourhood);

                /* we save the data and we check if everything is correct (texts are not empty, the specified neighbourhood exists)
                * if something is wrong, messages depending on the situation will be shown
                * else, we attempt to create the user, and if everything goes fine we are redirected to the home page */
                authenticationViewModel.saveLocationData(userCity, userNeighbourhood, userStreet, userStreetNumber);
                authenticationViewModel.checkLocationFields(city, userCity, neighbourhood, userNeighbourhood, street, userStreet, streetNumber, userStreetNumber, context, new MyCallback() {
                    @Override
                    public void onCallback(Object result) {
                        if (!(boolean) result) {
                            Toast.makeText(getContext(), R.string.fields_error, Toast.LENGTH_SHORT).show();
                        } else {
                            authenticationViewModel.createUser(getContext(), new MyCallback() {
                                @Override
                                public void onCallback(Object result) {
                                    if (result != null) {
                                        sharedPreferences.edit().putBoolean("first_time", true).apply();
                                        startActivity(new Intent(getContext(), BottomBar.class));
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        /*if the neighbourhood inserted does not exists, a message is shown, suggesting its creation
        * we click on this text to do so */
        textCreateNeighbourhood.setOnClickListener((v) -> {
            /*we extract the text from the fields and we convert it to a proper form
             * (Ex. venezia, VENEZIA, vENezIa, ecc.., will all be converted to Venezia)*/
            String userCity = city.getText().toString().trim(), userNeighbourhood = "",
                   userStreet = street.getText().toString().trim(), userStreetNumber = streetNumber.getText().toString().trim();
            userCity = Utilities.convertToProperForm(userCity);
            userStreet = Utilities.convertToProperForm(userStreet);
            userStreetNumber = Utilities.convertToProperForm(userStreetNumber);

            if(authenticationViewModel.checkLocationFields(city, userCity, street, userStreet, streetNumber, userStreetNumber, this.getContext())){
                authenticationViewModel.saveLocationData(userCity, userNeighbourhood, userStreet, userStreetNumber);
                //we save the data inserted in the form fields and we are redirected to the neighbourhood creation page

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                NeighbourhoodFragment neighbourhoodFragment = new NeighbourhoodFragment(authenticationViewModel, mapViewModel);
                fragmentTransaction.replace(R.id.fragment_container, neighbourhoodFragment).commit();
            }
        });

        return view;
    }
}