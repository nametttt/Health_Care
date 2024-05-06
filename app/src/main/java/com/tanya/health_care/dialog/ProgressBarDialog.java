package com.tanya.health_care.dialog;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.wait_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, timeout);
    }

    public void setTimeout(long timeoutMs) {
        this.timeout = timeoutMs;
    }

}
