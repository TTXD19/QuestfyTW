package taiwan.questfy.welsenho.questfy_tw.MainUserActivity;

import android.os.Handler;

public class MainActivityMethods {

    private Boolean doubeTapExit = false;

    public Boolean detectExit(){

        doubeTapExit = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubeTapExit =  false;
            }
        }, 2000);
        return doubeTapExit;
    }

}
