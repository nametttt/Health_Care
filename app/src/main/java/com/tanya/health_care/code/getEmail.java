package com.tanya.health_care.code;

import android.text.TextUtils;
import android.util.Patterns;

public class getEmail {
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
