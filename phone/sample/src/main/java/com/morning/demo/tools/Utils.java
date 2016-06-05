package com.morning.demo.tools;

import android.net.Uri;


import com.morning.demo.bean.Suggestion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morning on 2016-6-2.
 */
public class Utils {

    private static final String NEED_DECODE_FLAG = "http://127.0.0.1:8300/base/iqiyim3u?url=";

    public static List<Suggestion> getSearchSuggestions(){
        List<Suggestion> suggestions = new ArrayList<>();
//        suggestions.add(new Suggestion("a"));
//        suggestions.add(new Suggestion("b"));
//        suggestions.add(new Suggestion("c"));
        return suggestions;
    }

    public static boolean isNeedDecode(String url){
        return url.contains(NEED_DECODE_FLAG);
    }

    public static String decode(String url){
        url = url.replace(NEED_DECODE_FLAG,"");
        return Uri.decode(url);
    }

    public static String getPhoneId(){
        return "13580332745";
    }

    public static String getRouteId(){
        return "111111";
    }
}
