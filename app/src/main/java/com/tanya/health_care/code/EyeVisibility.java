package com.tanya.health_care.code;

import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageButton;

import com.tanya.health_care.R;

public class EyeVisibility {
    public static boolean isVisible;
    public static void toggleVisibility(EditText editText, ImageButton imageButton) {
        isVisible = false;
        if (isVisible) {
            String text = editText.getText().toString();
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setText(text);
            imageButton.setImageResource(R.drawable.eye);
            editText.setSelection(text.length());
        } else {
            String text = editText.getText().toString();
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setText(text);
            imageButton.setImageResource(R.drawable.eye_off);
            editText.setSelection(text.length());
        }
        isVisible = !isVisible;
    }
}
