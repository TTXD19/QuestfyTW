package com.example.welsenho.questfy_tw.MainUserActivity;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainActivityTabAdapter extends FragmentPagerAdapter {

    MainActivityTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0: return new MainActivityLatestArticleFragment();
            case 1: return new MostPopularFragment();
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
            case 0: return "Latest Article";
            case 1: return "Most Popular";
        }
        return null;
    }
}
