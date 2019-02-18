package com.example.welsenho.questfy_tw.FriendRelatedActivity;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;

import java.util.ArrayList;

public interface onMainFriendRequestClickListener {
    void onClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList);
    void onCancelClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList);
}
