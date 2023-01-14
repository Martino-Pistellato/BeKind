package com.example.bekind_v2.Utilities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.bekind_v2.DataLayer.NeighbourhoodRepository;
import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.ui.profile.ProfileViewModel;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;


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

        title.setText(R.string.modify_residence_data);//TODO: metti la stringa
        city.setText(profileViewModel.getUser().getCity());
        street.setText(profileViewModel.getUser().getStreet());
        streetNumber.setText(profileViewModel.getUser().getStreet_number());
        profileViewModel.getNeighbourhood(profileViewModel.getUser().getNeighbourhoodID(), neighbourhood::setText);

        city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                neighbourhood.setEnabled(!city.getText().toString().isEmpty());
            }
        });

        neighbourhood.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    NeighbourhoodRepository.getNeighbourhoods(city.getText().toString(), result -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, result);
                        neighbourhood.setAdapter(adapter);
                    });
                }
            }
        });

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        LatLng coord = mapViewModel.getCoordinatesFromAddress(getContext(), city.getText().toString().trim(), street.getText().toString().trim(), streetNumber.getText().toString().trim());

        mapViewModel.initializeMap(getActivity(), getContext(),autocompleteFragment, mapFragment,city,street,streetNumber, coord, null);


        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityText = Utilities.convertToProperForm(city.getText().toString().trim());
                String streetText = Utilities.convertToProperForm(street.getText().toString().trim());
                String streetNumb = Utilities.convertToProperForm(streetNumber.getText().toString().trim());

                profileViewModel.setCity(cityText);
                profileViewModel.setStreet(streetText);
                profileViewModel.setStreet_number(streetNumb);

                profileViewModel.setNeighbourhood(getContext(), neighbourhood, result -> profileViewModel.updateUser());

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
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(false);

                TextInputEditText neighbourhood = dialog.findViewById(R.id.neigh_name);
                Button backBtn = dialog.findViewById(R.id.back_button), confirmBtn = dialog.findViewById(R.id.continue_button);

                backBtn.setText(R.string.close); //TODO: toString
                confirmBtn.setText(R.string.confirm); //TODO: toString

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
                                profileViewModel.setNeighbourhood(getContext(), neighbourhood, (y) -> {
                                    profileViewModel.updateUser();

                                    profileViewModel.getUserName(profileName::setText);
                                    dialog.dismiss();
                                });
                            else {
                                neighbourhood.setError(getString(R.string.already_existing_neighbourhood));
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
