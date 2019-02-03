package com.example.welsenho.questfy_tw.AnswerReplyActivityRelated;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AnserReplyRelatedMethods {

    public String getUploadDate(){
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date myDate = new Date();
        return timeStampFormat.format(myDate);
    }


}
