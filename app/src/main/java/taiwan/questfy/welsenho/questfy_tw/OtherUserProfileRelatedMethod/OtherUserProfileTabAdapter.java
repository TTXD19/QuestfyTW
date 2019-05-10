package taiwan.questfy.welsenho.questfy_tw.OtherUserProfileRelatedMethod;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class OtherUserProfileTabAdapter extends FragmentPagerAdapter {


    public OtherUserProfileTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:return new UserAskedQuestionsFragment();
            case 1:return new AnsweredHitstoryFragment();
            case 2:return new CheckFollowersFragment();
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
        switch (position){
            case 0:
                return "發問問題";
            case 1:
                return "回答問題";
            case 2:
                return "追蹤者";
        }
        return super.getPageTitle(position);
    }
}
