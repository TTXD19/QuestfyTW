package taiwan.questfy.welsenho.questfy_tw.SettingPageRelated;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import taiwan.questfy.welsenho.questfy_tw.LoginRelated.LoginActivity;
import taiwan.questfy.welsenho.questfy_tw.R;

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
    private Preference preRegisterAccount;

    private PreferenceCategory preferenceGeneral;
    private Preference preferenceaccountInfo;

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
        preRegisterAccount = findPreference("registerAccount");

        preferenceaccountInfo = findPreference("accountInfo");


        preAppVersion.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getContext(), preference.getSummary(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        preResetPassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(firebaseUser.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getContext(), "已寄出一封重設郵件至您的Email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return true;
            }
        });

        preLogOut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                firebaseAuth.signOut();
                return false;
            }
        });

        prePrivacyPolicy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String url = "https://welsen9595.wixsite.com/questfyprivacypolicy";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            }
        });

        preTermOfAgreements.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String url = "https://welsen9595.wixsite.com/questfyprivacypolicy";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            }
        });

        preFacebookPage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getContext(), "臉書專頁製作中，🈲️請期待", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        preInviteFriend.setVisible(false);




        InitFirebase();
        setKey();
    }

    private void InitFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void setKey(){
        if (firebaseUser != null) {
            preEmailRegister.setSummary(firebaseUser.getEmail());
            preRegisterAccount.setVisible(false);
            preEmailRegister.setVisible(true);
            preResetPassword.setVisible(true);
            preLogOut.setVisible(true);
        }else {
            preRegisterAccount.setVisible(true);
            preEmailRegister.setVisible(false);
            preResetPassword.setVisible(false);
            preLogOut.setVisible(false);
        }
    }



}
