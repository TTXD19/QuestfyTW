package com.example.welsenho.questfy_tw.AppIntroRelated;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.example.welsenho.questfy_tw.LoginRelated.LoginActivity;
import com.example.welsenho.questfy_tw.MainUserActivity.MainActivity;
import com.example.welsenho.questfy_tw.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class AppIntroActivity extends AppIntro implements AppIntroFragment1.OnFragmentInteractionListener, AppIntroFragment2.OnFragmentInteractionListener{

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addSlide(new AppIntroFragment1());
        addSlide(new AppIntroFragment2());

        setNextArrowColor(ContextCompat.getColor(getApplicationContext(), R.color.FullBlack));
        setColorSkipButton(ContextCompat.getColor(getApplicationContext(), R.color.FullBlack));
        setIndicatorColor(ContextCompat.getColor(getApplicationContext(), R.color.signInRetangleRed), ContextCompat.getColor(getApplicationContext(), R.color.LightBlackText));
        setColorDoneText(ContextCompat.getColor(getApplicationContext(), R.color.CatergoryDarkGrayText));

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(AppIntroActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent = new Intent(AppIntroActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
