package com.example.welsenho.questfy_tw.PersonAskQuestionRelated;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

public class MainPersonalAskTabAdapter extends FragmentPagerAdapter {

    public MainPersonalAskTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:return new PersonAskByFragment();
            case 1:return new PersonalAskToFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:return "對我的提問";
            case 1:return "我的提問";
        }
        return null;
    }
}
