package com.anhnguyen.homeshopping.util;

import java.text.DecimalFormat;

public class Helper {

    public static boolean isValidEmail(String email){
        if (email == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
