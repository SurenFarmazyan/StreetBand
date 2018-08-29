package com.streetband.utils;

import android.content.Context;

public class Density {
    private static float mDensity;
    public static float getDensity(Context context){
        if(mDensity == 0){
            mDensity = context.getResources().getDisplayMetrics().density;
        }
        return mDensity;
    }
}
