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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingPagePopulateFragment extends PreferenceFragmentCompat {

    private Preference preAppVersion;
    private Preference preEmailRegister;
    private Preference preResetPassword;
    private Preference preLogOut;
    private Preference preWhatisQuestfy;
    private Preference preFacebookPage;
    private Preference preInviteFriend;
    private Preference prePrivacyPolicy;
    private Preference preTermOfAgreements;
    private PreferenceCategory preferenceGeneral;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.user_setting_preferences, s);

        preAppVersion = findPreference("appVersion");
        preEmailRegister = findPreference("registerEmail");
        preResetPassword = findPreference("resetPassword");
        preLogOut = findPreference("logout");
        preWhatisQuestfy = findPreference("whatIsQuestfy");
        preFacebookPage = findPreference("facebookFanPage");
        preInviteFriend = findPreference("inviteFriend");
        prePrivacyPolicy = findPreference("privacyPolicy");
        preTermOfAgreements = findPreference("termOfUses");


        preAppVersion.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getContext(), preference.getKey(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        InitFirebase();
        setKey();
    }

    private void InitFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void setKey(){
        preEmailRegister.setSummary(firebaseUser.getEmail());
    }



}
