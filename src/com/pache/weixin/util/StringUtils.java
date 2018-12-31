package com.pache.weixin.util;

public class StringUtils {

    public static String dealSpecChar(String obj){
        if(obj == null){
            return "";
        }
        return obj.replaceAll("&lt;","<").replaceAll("&gt;",">").replaceAll("&quot;","\"");
    }

}
