package com.tanya.health_care.code;

import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageButton;

import com.tanya.health_care.R;

public class EyeVisibility {
    private static boolean isVisible;

    public static void toggleVisibility(EditText editText, ImageButton imageButton) {
        boolean isVisible = editText.getInputType() != (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        if (isVisible) {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imageButton.setImageResource(R.drawable.eye);
        } else {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            imageButton.setImageResource(R.drawable.eye_off);
        }
        editText.setSelection(editText.getText().length());
    }
}
