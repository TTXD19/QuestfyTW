package com.example.welsenho.questfy_tw.MainUserActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

public class MainActivityMethods {

    private Boolean doubeTapExit = false;

    public Boolean detectExit(){

        doubeTapExit = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubeTapExit =  false;
            }
        }, 2000);
        return doubeTapExit;
    }

}
