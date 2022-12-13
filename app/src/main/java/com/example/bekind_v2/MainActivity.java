package com.example.bekind_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.bekind_v2.UILayer.Authentication.AuthenticationViewModel;
import com.example.bekind_v2.UILayer.Authentication.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
}