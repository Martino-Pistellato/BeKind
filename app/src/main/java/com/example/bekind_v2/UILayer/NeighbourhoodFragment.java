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
import com.google.android.material.textfield.TextInputEditText;

public class NeighbourhoodFragment extends Fragment {

    private NeighbourhoodViewModel neighbourhoodViewModel;
    private final AuthenticationViewModel authenticationViewModel;

    public NeighbourhoodFragment(AuthenticationViewModel authenticationViewModel){
        this.authenticationViewModel = authenticationViewModel;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_neighbourhood, container, false);

        TextInputEditText name = view.findViewById(R.id.neigh_name);
        Button backBtn = view.findViewById(R.id.back_button), continueBtn = view.findViewById(R.id.continue_button);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                RegistrationFragment2 registrationFragment2 = new RegistrationFragment2(authenticationViewModel);
                fragmentTransaction.replace(R.id.fragment_container, registrationFragment2).commit();
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String neighbourhoodName = name.getText().toString().trim();
                if(!neighbourhoodViewModel.checkNeighbourhoodName(name, neighbourhoodName)){
                    Toast.makeText(getContext(), "Errore: i campi non sono stati riempiti correttamente", Toast.LENGTH_SHORT).show();
                }
                else{
                    NeighbourhoodViewModel.createNeighbourhood(neighbourhoodName, authenticationViewModel.getCity(), (x)->{
                        if(x) {
                            authenticationViewModel.setNeighbourhood(neighbourhoodName);
                            authenticationViewModel.createUser(getContext(), (y) -> {
                                startActivity(new Intent(getContext(), BottomBar.class));
                            });
                        }
                        else
                            Toast.makeText(getContext(), "Errore: questo quartiere esiste già", Toast.LENGTH_SHORT).show();
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