package com.tanya.health_care.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.tanya.health_care.R;

public class ProgressBarDialog extends DialogFragment {

    private static final long DEFAULT_TIMEOUT_MS = 3000;
    private long timeout = DEFAULT_TIMEOUT_MS;

    public ProgressBarDialog() {
    }

    public static ProgressBarDialog newInstance(long timeoutMs) {
        ProgressBarDialog fragment = new ProgressBarDialog();
        fragment.setTimeout(timeoutMs);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_loading, null);

        LottieAnimationView animationView = view.findViewById(R.id.animation_view);
        animationView.setAnimation(R.raw.loading);
        animationView.setRepeatCount(LottieDrawable.INFINITE);
        animationView.playAnimation();

        builder.setView(view);

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, timeout);

        return dialog;
    }

    public void setTimeout(long timeoutMs) {
        this.timeout = timeoutMs;
    }
}
