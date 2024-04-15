package com.tanya.health_care.code;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class WaterConsumptionChartView extends View {

    private static final int NUM_DAYS = 7; // Количество дней для отображения
    private static final int TARGET_CONSUMPTION = 1200; // Цель потребления воды в мл

    private int[] waterConsumption; // Массив значений потребления воды
    private Paint paint;
    private Paint targetPaint;

    public WaterConsumptionChartView(Context context) {
        super(context);
        init();
    }

    public WaterConsumptionChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(4);
        paint.setAntiAlias(true);

        targetPaint = new Paint();
        targetPaint.setColor(Color.RED);
        targetPaint.setStrokeWidth(2);
        targetPaint.setStyle(Paint.Style.STROKE);

        waterConsumption = new int[NUM_DAYS];
    }

    // Метод для установки значений потребления воды
    public void setWaterConsumption(int[] consumption) {
        if (consumption.length != NUM_DAYS) {
            throw new IllegalArgumentException("Invalid array size");
        }
        waterConsumption = consumption;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Определяем размеры области отрисовки
        int width = getWidth();
        int height = getHeight();

        // Определяем ширину и высоту каждой колонки
        float columnWidth = (float) width / NUM_DAYS;
        float maxConsumption = getMaxConsumption();
        float columnHeightRatio = maxConsumption > 0 ? (float) height / maxConsumption : 0;

        // Отрисовываем столбцы графика
        for (int i = 0; i < NUM_DAYS; i++) {
            float left = i * columnWidth;
            float right = left + columnWidth;
            float top = height - waterConsumption[i] * columnHeightRatio;
            float bottom = height;
            canvas.drawRect(left, top, right, bottom, paint);
        }

        // Рисуем линию цели потребления воды
        float targetHeight = height - TARGET_CONSUMPTION * columnHeightRatio;
        canvas.drawLine(0, targetHeight, width, targetHeight, targetPaint);
    }

    // Метод для нахождения максимального значения потребления воды
    private int getMaxConsumption() {
        int max = 0;
        for (int consumption : waterConsumption) {
            if (consumption > max) {
                max = consumption;
            }
        }
        return Math.max(max, TARGET_CONSUMPTION);
    }
}

