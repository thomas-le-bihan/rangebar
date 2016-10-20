package com.example.tlebihan.rangebar;

import android.content.Context;

/**
 * Created by t.lebihan on 17/10/2016.
 */

public class Utils {

    public static float pxToDp(Context context, int px){
        float scale = context.getResources().getDisplayMetrics().density;
        return (px - 0.5f) / scale;
    }

    public static float dpToPx(Context context, int dp){
        float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }
}
