package com.example.bekind_v2.UILayer;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.Authentication.LoginActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!SettingsViewModel.isLogged()) startActivity(new Intent(this, LoginActivity.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.settings, new SettingsFragment()).commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_settings);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            Preference logoutBtn = findPreference("logout"), tutorialBtn = findPreference("tutorial");
            ListPreference theme = findPreference("theme"), language = findPreference("language_selection");

            logoutBtn.setOnPreferenceClickListener(preference -> {
                SettingsViewModel.logout();
                startActivity(new Intent(getContext(), LoginActivity.class));
                return true;
            });

            theme.setOnPreferenceChangeListener(((preference, newValue) -> {
                if(newValue.equals("dark_theme")) {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
                    sharedPreferences.edit().putBoolean("dark_theme", true).apply();
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
                    sharedPreferences.edit().putBoolean("dark_theme", false).apply();
                }

                return true;
            }));

            language.setOnPreferenceChangeListener(((preference, newValue) -> {
                LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(newValue.toString());
                AppCompatDelegate.setApplicationLocales(appLocale);
                return true;
            }));

            tutorialBtn.setOnPreferenceClickListener(preference -> {
                Dialog tutorialDialog = new Dialog(getContext());
                View view = View.inflate(getContext(), R.layout.tutorial_popup, null);
                tutorialDialog.setContentView(view);
                tutorialDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Button continueBtn = view.findViewById(R.id.tutorial_continue_btn), backBtn = view.findViewById(R.id.tutorial_back_btn);

                ImageView image = view.findViewById(R.id.tutorial_image);
                TextView description = view.findViewById(R.id.tutorial_text);

                continueBtn.setOnClickListener(v -> {
                    SettingsViewModel.index++;
                    if (SettingsViewModel.index == 12) {
                        SettingsViewModel.index = 1;
                        tutorialDialog.dismiss();
                    }
                    else {
                        if (SettingsViewModel.index > 1) backBtn.setVisibility(View.VISIBLE);
                        if (SettingsViewModel.index == 11) continueBtn.setText(R.string.close);
                        SettingsViewModel.setContent(getActivity(), image, description);
                    }
                });

                backBtn.setOnClickListener(v -> {
                    SettingsViewModel.index--;
                    if (SettingsViewModel.index == 1) backBtn.setVisibility(View.INVISIBLE);
                    SettingsViewModel.setContent(getActivity(), image, description);
                });

                tutorialDialog.show();

                return true;
            });
        }


    }
}