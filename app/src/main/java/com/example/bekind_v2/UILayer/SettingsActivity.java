package com.example.bekind_v2.UILayer;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

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
            Preference logoutBtn = findPreference("logout");
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
        }
    }
}