package com.example.bekind_v2.UILayer.Authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.BottomBar;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText email, password;
    private Button btnLogin;
    private TextView textRegister;
    private AuthenticationViewModel authenticationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authenticationViewModel = new ViewModelProvider(this).get(AuthenticationViewModel.class);
        setContentView(R.layout.activity_login);

        if (authenticationViewModel.isLogged()) {
            startActivity(new Intent(LoginActivity.this, BottomBar.class));
        }//bring me to the main page

        email = findViewById(R.id.login_email); //text field for email
        password = findViewById(R.id.login_password); //text field for password
        btnLogin = findViewById(R.id.login_button); //button for login
        textRegister = findViewById(R.id.text_register); //link to registration

        btnLogin.setOnClickListener( (view) -> { //login button
            String userEmail = email.getText().toString().trim(); //to get the text written by the user in the specified EditText view
            String userPassword = password.getText().toString().trim(); //trim is used to delete spaces from the start and the end of the input, if present

            if(!authenticationViewModel.checkCredentials(email, userEmail, password, userPassword))
                Toast.makeText(getApplicationContext(), "Errore: le credenziali di accesso non sono corrette", Toast.LENGTH_SHORT).show();
            else{
                authenticationViewModel.login(getApplicationContext(), userEmail, userPassword, (x) -> {
                    if (x)
                        startActivity(new Intent(LoginActivity.this, BottomBar.class)); //bring me to the main page, passing .this could be a problem but who knows
                    else
                        Toast.makeText(getApplicationContext(), "Errore: le credenziali di accesso non sono corrette", Toast.LENGTH_SHORT).show();
                });
            }
        });

        textRegister.setOnClickListener( (view) -> {startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));} ); //registration link
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}