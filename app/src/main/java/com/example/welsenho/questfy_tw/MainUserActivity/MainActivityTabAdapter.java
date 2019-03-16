package com.example.welsenho.questfy_tw.MainUserActivity;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.welsenho.questfy_tw.MainActivityFragment.MainActivityLatestArticleFragment;
import com.example.welsenho.questfy_tw.MainActivityFragment.MainSubjectChooseFragment;
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
            case 0: return new MainSubjectChooseFragment();
            case 1: return new MainActivityLatestArticleFragment();
            case 2: return new MostPopularFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (language.equals("中文")) {
            switch (position) {
                case 0:
                    return "相關科系";
                case 1:
                    return "最新文章";
                case 2:
                    return "熱門文章";
            }
        }else {
            switch (position) {
                case 0:
                    return "Program Select";
                case 1:
                    return "Latest Article";
                case 2:
                    return "Most Popular";
            }
        }
        return null;
    }
}
