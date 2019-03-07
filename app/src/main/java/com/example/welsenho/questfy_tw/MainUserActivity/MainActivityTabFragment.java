package com.example.welsenho.questfy_tw.MainUserActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.MainActivityFragment.MainActivityLatestArticleFragment;
import com.example.welsenho.questfy_tw.R;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivityTabFragment extends Fragment{

    public TabLayout tabLayout;
    public ViewPager viewPager;

    public OnFragmentInteractionListener mListener;

    public MainActivityTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_activity_tab, container, false);
        tabLayout = view.findViewById(R.id.main_activity_tabLayout);
        viewPager = view.findViewById(R.id.main_activity_viewpager);
        viewPager.setAdapter(new MainActivityTabAdapter(getChildFragmentManager(), Locale.getDefault().getDisplayLanguage()));
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                mListener.getTabCurrentPage(i);
            }

            @Override
            public void onPageSelected(int i) {
                mListener.getTabCurrentPage(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener){
            mListener = (OnFragmentInteractionListener) context;
        }else {
            throw new RuntimeException(context.toString() +
            " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    interface OnFragmentInteractionListener{
        // TODO: Update argument type and name
        void getTabCurrentPage(int page);
    }
}
