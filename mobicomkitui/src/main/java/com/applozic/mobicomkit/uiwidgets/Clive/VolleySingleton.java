package com.applozic.mobicomkit.uiwidgets.Clive;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.applozic.mobicomkit.uiwidgets.ApplozicApplication;

/**
 * Created by apple on 4/28/17.
 */

public class VolleySingleton {
    private static VolleySingleton sInstance=null;
    private static RequestQueue mRequestQueue;
    private static  Context context;

    private VolleySingleton(Context context){
        mRequestQueue= Volley.newRequestQueue(context);
        this.context =context;
    }


    public static VolleySingleton getsInstance(Context context){

        if(sInstance==null){
            sInstance=new VolleySingleton(context);
        }
        return  sInstance;
    }

    public static RequestQueue getRequestQueue(Context context){

        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(context.getCacheDir(), 10 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            // Don't forget to start the volley request queue
            mRequestQueue.start();
        }
        return mRequestQueue;

    }
}
