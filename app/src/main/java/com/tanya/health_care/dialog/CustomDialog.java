package com.tanya.health_care.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.airbnb.lottie.LottieAnimationView;
import com.tanya.health_care.R;

public class CustomDialog extends DialogFragment {

    private String message;
    private boolean isSuccess;

    public CustomDialog(String message, boolean isSuccess) {
        this.message = message;
        this.isSuccess = isSuccess;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_custom, null);

        LottieAnimationView animationView = view.findViewById(R.id.animation_view);
        if (isSuccess) {
            animationView.setAnimation(R.raw.success);
        } else {
            animationView.setAnimation(R.raw.error);
        }
        animationView.playAnimation();

        TextView messageTextView = view.findViewById(R.id.message);
        messageTextView.setText(message);

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
