package com.example.bekind_v2.UILayer;

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
import com.google.android.material.textfield.TextInputEditText;

public class NeighbourhoodFragment extends Fragment {

    private NeighbourhoodViewModel neighbourhoodViewModel;
    private AuthenticationViewModel authenticationViewModel;

    public NeighbourhoodFragment(AuthenticationViewModel authenticationViewModel){
        this.authenticationViewModel = authenticationViewModel;
        this.neighbourhoodViewModel = new ViewModelProvider(this).get(NeighbourhoodViewModel.class);
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

            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            String neighbourhoodName = name.getText().toString().trim();
            @Override
            public void onClick(View v) {
                if(!neighbourhoodViewModel.checkNeighbourhoodName(name, neighbourhoodName)){
                    Toast.makeText(getContext(), "Errore: i campi non sono stati riempiti correttamente", Toast.LENGTH_SHORT).show();
                }
                else{
                    neighbourhoodViewModel.createNeighbourhood(neighbourhoodName, authenticationViewModel.getCity(), (x)->{
                        if((boolean)x)
                            authenticationViewModel.createUser((y)->{startActivity(new Intent(getContext(), BottomBar.class));});
                        else
                            Toast.makeText(getContext(), "Errore: questo quartiere esiste già", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });

        return view;
    }

}