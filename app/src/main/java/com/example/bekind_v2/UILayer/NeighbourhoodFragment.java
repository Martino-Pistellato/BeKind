package com.example.bekind_v2.UILayer;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.Authentication.AuthenticationViewModel;
import com.example.bekind_v2.UILayer.Authentication.RegistrationFragment2;
import com.example.bekind_v2.Utilities.MapViewModel;
import com.example.bekind_v2.Utilities.Utilities;
import com.google.android.material.textfield.TextInputEditText;

public class NeighbourhoodFragment extends Fragment {

    private NeighbourhoodViewModel neighbourhoodViewModel;
    private final AuthenticationViewModel authenticationViewModel;
    private MapViewModel mapViewModel;

    public NeighbourhoodFragment(AuthenticationViewModel authenticationViewModel, MapViewModel mapViewModel){
        this.authenticationViewModel = authenticationViewModel;
        this.mapViewModel = mapViewModel;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_neighbourhood, container, false);

        TextInputEditText name = view.findViewById(R.id.neigh_name);
        Button backBtn = view.findViewById(R.id.back_button), continueBtn = view.findViewById(R.id.continue_button);

        //if we press the back button, we are redirected to second page of the registration
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                RegistrationFragment2 registrationFragment2 = new RegistrationFragment2(authenticationViewModel, mapViewModel);
                fragmentTransaction.replace(R.id.fragment_container, registrationFragment2).commit();
            }
        });

        //if we press the continue button
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*we extract the text from the neighbourhood form field and we get the city inserted in the second page of the registration
                * both are converted to a proper form
                * (Ex. venezia, VENEZIA, vENezIa, ecc.., will all be converted to Venezia)*/
                String neighbourhoodName = Utilities.convertToProperForm(name.getText().toString().trim());
                String city = authenticationViewModel.getCity();
                /* we check if the neighbourhood form field is correct
                 * if it is empty, a message will be shown */
                if(!neighbourhoodViewModel.checkNeighbourhoodName(getContext(), name, neighbourhoodName)){
                    Toast.makeText(getContext(), getString(R.string.fields_error), Toast.LENGTH_SHORT).show();
                }
                else{
                    /*if everything is fine, we attempt to create the new neighbourhood. Once its done, we attempt to create the new user
                    * if everything is fine, we are redirected to the home page*/
                    city = Utilities.convertToProperForm(city);
                    NeighbourhoodViewModel.createNeighbourhood(neighbourhoodName, city, (x)->{
                        if(x) {
                            authenticationViewModel.setNeighbourhood(neighbourhoodName);
                            authenticationViewModel.createUser(getContext(), (y) -> {
                                if(y != null)
                                    startActivity(new Intent(getContext(), BottomBar.class));
                            });
                        }
                        else
                            Toast.makeText(getContext(), getString(R.string.existing_neighbourhood), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        neighbourhoodViewModel = new ViewModelProvider(this).get(NeighbourhoodViewModel.class);
    }
}