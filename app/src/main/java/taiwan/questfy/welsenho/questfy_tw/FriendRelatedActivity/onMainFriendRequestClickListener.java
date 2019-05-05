package taiwan.questfy.welsenho.questfy_tw.FriendRelatedActivity;


import java.util.ArrayList;

import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;

public interface onMainFriendRequestClickListener {
    void onClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList);
    void onCancelClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList);
}
