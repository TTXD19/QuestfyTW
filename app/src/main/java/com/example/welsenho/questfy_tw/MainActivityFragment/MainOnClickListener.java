package com.example.welsenho.questfy_tw.MainActivityFragment;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;

import java.util.ArrayList;

public interface MainOnClickListener {
    void onClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList);
}
