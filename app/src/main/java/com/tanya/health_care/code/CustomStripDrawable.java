package com.tanya.health_care.code;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

import androidx.annotation.NonNull;

public class CustomStripDrawable extends Drawable {

    private ShapeDrawable bigStripDrawable;
    private ShapeDrawable smallStrip1Drawable;
    private ShapeDrawable smallStrip2Drawable;

    private int bigStripColor = Color.parseColor("#E0E0E0");  // Светло серая для большой полосы
    private int smallStrip1Color = Color.parseColor("#FF4081"); // Розовая для первой маленькой полосы
    private int smallStrip2Color = Color.parseColor("#3F51B5"); // Голубая для второй маленькой полосы

    private int bigStripHeight = 20;  // Высота большой полосы
    private int smallStrip1Height = 15; // Высота первой маленькой полосы
    private int smallStrip2Height = 10; // Высота второй маленькой полосы

    private int cornerRadius = 10; // Радиус закругления углов

    public CustomStripDrawable() {
        Paint bigPaint = new Paint();
        bigPaint.setColor(bigStripColor);
        bigPaint.setAntiAlias(true);

        Paint smallPaint1 = new Paint();
        smallPaint1.setColor(smallStrip1Color);
        smallPaint1.setAntiAlias(true);

        Paint smallPaint2 = new Paint();
        smallPaint2.setColor(smallStrip2Color);
        smallPaint2.setAntiAlias(true);

        float[] outerRadii = new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius,
                cornerRadius, cornerRadius, cornerRadius, cornerRadius};

        bigStripDrawable = new ShapeDrawable(new RoundRectShape(outerRadii, null, null));
        bigStripDrawable.getPaint().set(bigPaint);

        smallStrip1Drawable = new ShapeDrawable(new RoundRectShape(outerRadii, null, null));
        smallStrip1Drawable.getPaint().set(smallPaint1);

        smallStrip2Drawable = new ShapeDrawable(new RoundRectShape(outerRadii, null, null));
        smallStrip2Drawable.getPaint().set(smallPaint2);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();

        // Рисуем большую округлую полосу
        RectF bigRect = new RectF(bounds.left, bounds.top, bounds.right, bounds.top + bigStripHeight);
        bigStripDrawable.setBounds((int) bigRect.left, (int) bigRect.top, (int) bigRect.right, (int) bigRect.bottom);
        bigStripDrawable.draw(canvas);

        // Рисуем первую маленькую полосу (розовая)
        RectF smallRect1 = new RectF(bounds.left + 20, bounds.top + bigStripHeight / 2 - smallStrip1Height / 2,
                bounds.left + 120, bounds.top + bigStripHeight / 2 + smallStrip1Height / 2);
        smallStrip1Drawable.setBounds((int) smallRect1.left, (int) smallRect1.top, (int) smallRect1.right, (int) smallRect1.bottom);
        smallStrip1Drawable.draw(canvas);

        // Рисуем вторую маленькую полосу (голубая)
        RectF smallRect2 = new RectF(bounds.left + 130, bounds.top + bigStripHeight / 2 - smallStrip2Height / 2,
                bounds.left + 230, bounds.top + bigStripHeight / 2 + smallStrip2Height / 2);
        smallStrip2Drawable.setBounds((int) smallRect2.left, (int) smallRect2.top, (int) smallRect2.right, (int) smallRect2.bottom);
        smallStrip2Drawable.draw(canvas);
    }

    @Override
    public void setAlpha(int alpha) {
        bigStripDrawable.setAlpha(alpha);
        smallStrip1Drawable.setAlpha(alpha);
        smallStrip2Drawable.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(android.graphics.ColorFilter colorFilter) {
        bigStripDrawable.setColorFilter(colorFilter);
        smallStrip1Drawable.setColorFilter(colorFilter);
        smallStrip2Drawable.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return bigStripDrawable.getOpacity();
    }
}
