package taiwan.questfy.welsenho.questfy_tw.MainActivityFragment;


import java.util.ArrayList;

import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;

public interface MainOnClickListener {
    void onClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList);
}
