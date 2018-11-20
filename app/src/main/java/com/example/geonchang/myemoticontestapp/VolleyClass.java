package com.example.geonchang.myemoticontestapp;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by klp6363 on 2017. 3. 14..
 */

public class VolleyClass {
    private static VolleyClass volley;
    private RequestQueue requestQueue;

    private VolleyClass(Context context){
        requestQueue = Volley.newRequestQueue(context);
    }

    public static VolleyClass getInstance(Context context){
        if(volley == null){
            volley = new VolleyClass(context);
        }
        return volley;
    }

    public RequestQueue getRequestQueue(){
        return requestQueue;
    }
}
