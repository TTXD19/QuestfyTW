package com.example.welsenho.questfy_tw.SettingPageRelated;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.R;

public class SettingPagePopulateFragment extends PreferenceFragmentCompat {

    private Preference preAppVersion;
    private PreferenceCategory preferenceGeneral;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.user_setting_preferences, s);

        preAppVersion = findPreference("appVersion");
        preAppVersion.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getContext(), preference.getKey(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });


    }



}
