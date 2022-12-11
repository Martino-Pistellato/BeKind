package com.example.bekind_v2.UILayer.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.NeighbourhoodActivity;
import com.example.bekind_v2.UILayer.ui.home.HomeFragment;
import com.google.android.material.textfield.TextInputEditText;

public class RegistrationFragment2 extends Fragment {

    private AuthenticationViewModel authenticationViewModel;

    public RegistrationFragment2(AuthenticationViewModel authenticationViewModel){
        this.authenticationViewModel = authenticationViewModel;
    }

    public static RegistrationFragment2 newInstance() {
        return new RegistrationFragment2(newInstance().authenticationViewModel);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_registration2, container, false);

        TextView textNeighbourhood = view.findViewById(R.id.text_create_neigh);
        TextInputEditText city = view.findViewById(R.id.user_city), neighbourhood = view.findViewById(R.id.user_neigh),
                          street = view.findViewById(R.id.user_street), streetNumber = view.findViewById(R.id.street_number);
        Button backBtn = view.findViewById(R.id.back_button), continueBtn = view.findViewById(R.id.continue_button);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getUserFields() --> that returns the fields we initialized on the previous page
                authenticationViewModel.changeFragment(getActivity(), R.id.fragment_registration2, authenticationViewModel);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userCity = city.getText().toString().trim(), userNeighbourhood = neighbourhood.getText().toString().trim(),
                        userStreet = street.getText().toString().trim(), userStreetNumber = streetNumber.getText().toString().trim();

                if(!authenticationViewModel.checkLocationFiedls(city, userCity, neighbourhood, userNeighbourhood, street,userStreet, streetNumber, userStreetNumber)){
                    Toast.makeText(getContext(), "Errore: i campi non sono stati riempiti correttamente", Toast.LENGTH_SHORT).show();
                }else{
                    authenticationViewModel.saveLocationData(userCity, userNeighbourhood, userStreet, userStreetNumber);
                    //go to home page fragment
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    HomeFragment homeFragment = new HomeFragment();
                    fragmentTransaction.replace(R.id.fragment_container, homeFragment).commit();
                }
            }
        });

        textNeighbourhood.setOnClickListener((v) -> startActivity(new Intent(RegistrationFragment2.newInstance().getActivity(), NeighbourhoodActivity.class)));

        return view;
    }
}