package com.tanya.health_care.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.tanya.health_care.code.getSplittedPathChild;


import com.tanya.health_care.HomeActivity;
import com.tanya.health_care.LoginActivity;
import com.tanya.health_care.MainActivity;
import com.tanya.health_care.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class deleteDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.MyAlertDialogTheme);
        builder.setTitle("Удаление профиля")
                .setMessage("Профиль нельзя восстановить");

// Создаем отдельный layout для кнопок
        LinearLayout layout = new LinearLayout(requireActivity());
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setPadding(40, 40, 40, 40);
        layout.setBackgroundColor(Color.WHITE);

// Создаем кнопку "Удалить"
        Button deleteButton = new Button(requireActivity());
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f));
        deleteButton.setText("Удалить");
        deleteButton.setTextColor(Color.WHITE);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSplittedPathChild g = new getSplittedPathChild();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(g.getSplittedPathChild(user.getEmail()));
                ref.removeValue();
                user.delete();
            }
        });

// Создаем кнопку "Отмена"
        Button cancelButton = new Button(requireActivity());
        cancelButton.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f));
        cancelButton.setText("Отмена");
        cancelButton.setTextColor(Color.BLACK);
        cancelButton.setBackgroundColor(getResources().getColor(R.color.lightgray));


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        View divider = new View(requireActivity());
        divider.setLayoutParams(new LinearLayout.LayoutParams(20, LinearLayout.LayoutParams.MATCH_PARENT));


        GradientDrawable canc = new GradientDrawable();
        canc.setColor(getResources().getColor(R.color.lightgray));
        canc.setCornerRadius(20);
        cancelButton.setBackground(canc);


// Устанавливаем цвет рамки для кнопки "Удалить"
        GradientDrawable deleteButtonBackground = new GradientDrawable();
        deleteButtonBackground.setColor(getResources().getColor(R.color.green));
        deleteButtonBackground.setCornerRadius(20);
        deleteButton.setBackground(deleteButtonBackground);

// Выравниваем кнопки по высоте и добавляем отступ между ними
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.addView(cancelButton, new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        ));
        layout.addView(divider);
        layout.addView(deleteButton);

        layout.setDividerPadding(20);

// Устанавливаем радиус закругления углов для всего окна
        GradientDrawable dialogBackground = new GradientDrawable();
        dialogBackground.setCornerRadius(40);
        dialogBackground.setColor(Color.WHITE);
        layout.setBackground(dialogBackground);

        builder.setView(layout);
        builder.setCancelable(true);
        return builder.create();
    }
}
