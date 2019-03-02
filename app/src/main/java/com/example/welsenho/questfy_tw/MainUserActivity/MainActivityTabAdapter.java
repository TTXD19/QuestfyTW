package com.example.welsenho.questfy_tw.MainUserActivity;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.welsenho.questfy_tw.MainActivityFragment.MainActivityLatestArticleFragment;
import com.example.welsenho.questfy_tw.MainActivityFragment.MostPopularFragment;
import com.example.welsenho.questfy_tw.R;

public class MainActivityTabAdapter extends FragmentPagerAdapter{

    String language;

    public MainActivityTabAdapter(FragmentManager fm, String language) {
        super(fm);
        this.language = language;
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
        if (language.equals("中文")) {
            switch (position) {
                case 0:
                    return "最新文章";
                case 1:
                    return "熱門文章";
            }
        }else {
            switch (position) {
                case 0:
                    return "Latest Article";
                case 1:
                    return "Most Popular";
            }
        }
        return null;
    }
}
