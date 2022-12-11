package com.example.bekind_v2.UILayer.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bekind_v2.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

public class RegistrationFragment1 extends Fragment {

    private AuthenticationViewModel authenticationViewModel;

    public RegistrationFragment1(AuthenticationViewModel authenticationViewModel){
        this.authenticationViewModel = authenticationViewModel;
    }

    public static RegistrationFragment1 newInstance() {
        return new RegistrationFragment1(newInstance().authenticationViewModel);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration1, container, false);

        TextInputEditText name = view.findViewById(R.id.register_name),surname = view.findViewById(R.id.register_surname),
                          email = view.findViewById(R.id.register_email), password = view.findViewById(R.id.register_password);
        DatePicker birth = view.findViewById(R.id.registration_date_picker);
        Button cancelBtn = view.findViewById(R.id.cancel_button), continueBtn = view.findViewById(R.id.continue_button);
        AuthenticationViewModel.setBirthDate(birth);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = name.getText().toString().trim(), userSurname = surname.getText().toString().trim(),
                       userEmail = email.getText().toString().trim(), userPassword = password.getText().toString().trim();

                if(!authenticationViewModel.checkUserFiedls(name, userName, surname, userSurname, email, userEmail, password, userPassword))
                    Toast.makeText(getContext(), "Errore: i campi non sono stati riempiti correttamente", Toast.LENGTH_SHORT).show();
                else{
                    Date birthDate = AuthenticationViewModel.toDate(birth);
                    authenticationViewModel.saveUserData(userName, userSurname, userEmail, userPassword, birthDate);
                    authenticationViewModel.changeFragment(getActivity(), R.id.fragment_registration1, authenticationViewModel);
                }
            }
        });

        return view;
    }
}