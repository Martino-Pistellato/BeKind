package com.example.bekind_v2.UILayer.Authentication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bekind_v2.R;

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



        return view;
    }
}