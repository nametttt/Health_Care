
package com.tanya.health_care.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.airbnb.lottie.LottieAnimationView;
import com.tanya.health_care.R;

public class AdviceDialog extends DialogFragment {

    public AdviceDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_custom, null);

        LottieAnimationView animationView = view.findViewById(R.id.animation_view);
        animationView.setAnimation(R.raw.questions);
        animationView.setProgress(0);
        animationView.playAnimation();
        TextView messageTextView = view.findViewById(R.id.message);
        messageTextView.setText("Добро пожаловать в раздел 'Советы по здоровью'! Получайте ответы на вопросы о здоровье и персональные рекомендации на основе анализа ваших данных.");
        Button okButton = view.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(view);

        return builder.create();
    }
}

