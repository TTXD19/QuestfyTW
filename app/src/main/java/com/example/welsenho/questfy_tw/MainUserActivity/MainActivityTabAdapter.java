package com.example.welsenho.questfy_tw.MainUserActivity;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.welsenho.questfy_tw.MainActivityFragment.KeepArticlesFragment;
import com.example.welsenho.questfy_tw.MainActivityFragment.MainActivityLatestArticleFragment;
import com.example.welsenho.questfy_tw.MainActivityFragment.MainSubjectChooseFragment;
import com.example.welsenho.questfy_tw.MainActivityFragment.MostPopularFragment;
import com.example.welsenho.questfy_tw.MainActivityFragment.MyOwnPostArticles;
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
            case 3: return new KeepArticlesFragment();
            case 4: return new MyOwnPostArticles();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 5;
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
                case 3:
                    return "收藏文章";
                case 4:
                    return "我發表的問題";
            }
        }else {
            switch (position) {
                case 0:
                    return "Program Select";
                case 1:
                    return "Latest Article";
                case 2:
                    return "Most Popular";
                case 3:
                    return "Keep Articles";
                case 4:
                    return "My Questions";
            }
        }
        return null;
    }
}
