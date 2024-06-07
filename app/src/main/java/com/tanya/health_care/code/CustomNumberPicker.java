package com.tanya.health_care.code;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.tanya.health_care.R;

public class CustomNumberPicker extends LinearLayout {

    private TextView textView;
    private NumberPicker numberPicker;
    private int currentValue;

    public CustomNumberPicker(Context context) {
        super(context);
        init(context);
    }

    public CustomNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.number_picker_asset, this);

        textView = findViewById(R.id.number_picker_text_view);
        numberPicker = findViewById(R.id.number_picker);

        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setVisibility(View.GONE);
                numberPicker.setVisibility(View.VISIBLE);
            }
        });

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                currentValue = newVal;
                textView.setText(String.valueOf(newVal));
            }
        });

        numberPicker.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                numberPicker.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                return true;
            }
        });
    }

    public int getValue() {
        return currentValue;
    }

    public void setValue(int value) {
        currentValue = value;
        numberPicker.setValue(value);
        textView.setText(String.valueOf(value));
    }

    public void setMinValue(int minValue) {
        numberPicker.setMinValue(minValue);
    }

    public void setMaxValue(int maxValue) {
        numberPicker.setMaxValue(maxValue);
    }
}
