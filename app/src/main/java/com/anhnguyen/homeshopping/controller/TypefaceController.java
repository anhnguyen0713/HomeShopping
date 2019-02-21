package com.anhnguyen.homeshopping.controller;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;


public class TypefaceController {
    public static Typeface createTypeface(Context context, String font){
        return Typeface.createFromAsset(context.getAssets(), font);
    }

    public static void setTypeface(TextView t, Typeface font){
        t.setTypeface(font);
    }
}
