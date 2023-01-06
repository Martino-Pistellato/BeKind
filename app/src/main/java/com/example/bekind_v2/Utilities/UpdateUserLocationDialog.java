package com.example.bekind_v2.Utilities;

import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.ui.profile.ProfileViewModel;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.List;

public class UpdateUserLocationDialog extends DialogFragment {

    private View view;
    private ProfileViewModel profileViewModel;
    private MapViewModel mapViewModel;
    private TextView profileName;

    public UpdateUserLocationDialog(ProfileViewModel profileViewModel, MapViewModel mapViewModel, TextView profileName){
        this.profileViewModel = profileViewModel;
        this.mapViewModel = mapViewModel;
        this.profileName = profileName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_registration2,container,false);

        TextView textCreateNeighbourhood = view.findViewById(R.id.text_create_neigh), title = view.findViewById(R.id.neighbourhood_text);
        TextInputEditText city = view.findViewById(R.id.user_city), street = view.findViewById(R.id.user_street), streetNumber = view.findViewById(R.id.street_number);
        Button cancelBtn = view.findViewById(R.id.back_button), continueBtn = view.findViewById(R.id.continue_button);
        AutoCompleteTextView neighbourhood = view.findViewById(R.id.user_neigh);

        title.setText("Modifica dati residenza");//TODO: metti la stringa
        city.setText(profileViewModel.getUser().getCity());
        street.setText(profileViewModel.getUser().getStreet());
        streetNumber.setText(profileViewModel.getUser().getStreet_number());
        profileViewModel.getNeighbourhood(profileViewModel.getUser().getNeighbourhoodID(), neighbourhood::setText);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        LatLng coord = mapViewModel.getCoordinatesFromAddress(getContext(), city, street, streetNumber);

        mapViewModel.initializeMap(getActivity(), getContext(),autocompleteFragment, mapFragment,city,street,streetNumber, coord);


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
                dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        textCreateNeighbourhood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileViewModel.setCity(city.getText().toString().trim());
                profileViewModel.setStreet(street.getText().toString().trim());
                profileViewModel.setStreet_number(streetNumber.getText().toString().trim());

                dismiss();
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.fragment_neighbourhood);
                dialog.setCanceledOnTouchOutside(false);

                TextInputEditText neighbourhood = dialog.findViewById(R.id.neigh_name);
                Button backBtn = dialog.findViewById(R.id.back_button), confirmBtn = dialog.findViewById(R.id.continue_button);

                backBtn.setText("CHIUDI"); //TODO: toString
                confirmBtn.setText("CONFERMA"); //TODO: toString

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
        return  view;
    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }
}
