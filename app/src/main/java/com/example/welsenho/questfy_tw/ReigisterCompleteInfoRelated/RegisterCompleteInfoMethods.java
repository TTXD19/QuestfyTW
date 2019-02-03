package com.example.welsenho.questfy_tw.ReigisterCompleteInfoRelated;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterCompleteInfoMethods {

    public boolean checkTypeInWord(String typeIn){
        Pattern pattern = Pattern.compile("[~#@*+%{}<>\\[\\]|\"\\_^]");
        Matcher matcher = pattern.matcher(typeIn);
        return matcher.find();
    }

}
