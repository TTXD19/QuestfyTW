package taiwan.questfy.welsenho.questfy_tw.OtherUserProfileRelatedMethod;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OtherUserProfileRelatedMethods {

    public String getDate(){
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
        Date myDate = new Date();
        String filename = timeStampFormat.format(myDate);
        return filename;
    }

    public String getRequestDate(){
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date myDate = new Date();
        return timeStampFormat.format(myDate);
    }


}
