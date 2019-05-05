package taiwan.questfy.welsenho.questfy_tw.FriendRelatedActivity;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainFriendTabAdapter extends FragmentPagerAdapter {

    public MainFriendTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0: return new FriendMessageFragment();
            case 1: return new FriendRequestFragment();
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
            case 0: return "好友訊息";
            case 1: return "好友請求";
        }
        return null;
    }
}
