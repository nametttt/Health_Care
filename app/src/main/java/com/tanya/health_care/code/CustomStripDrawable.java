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

    private int bigStripColor = Color.parseColor("#E0E0E0");  // Light grey for big strip
    private int smallStrip1Color = Color.parseColor("#FF4081"); // Pink for first small strip
    private int smallStrip2Color = Color.parseColor("#3F51B5"); // Blue for second small strip

    private int bigStripHeight = 20;  // Height of big strip
    private int smallStrip1Height = 15; // Height of first small strip
    private int smallStrip2Height = 15; // Height of second small strip

    private int cornerRadius = 10; // Corner radius
    private int duration; // Duration in days
    private int position; // Position of the item

    public CustomStripDrawable(int duration, int position) {
        this.duration = duration;
        this.position = position;

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
        RectF bigRect = new RectF(bounds.left, bounds.top, bounds.right, bounds.top + bigStripHeight);
        bigStripDrawable.setBounds((int) bigRect.left, (int) bigRect.top, (int) bigRect.right, (int) bigRect.bottom);
        bigStripDrawable.draw(canvas);

        // Calculate the length of the small pink strip based on the duration
        int smallStrip1Length = 20 + duration * 30; // Adjust this value as needed
        int gapBetweenStrips = 10; // Define the gap between the pink and blue strips

        // Calculate the left margin for the pink strip based on the position
        int leftMargin = position * 20; // Adjust the multiplier as needed

        RectF smallRect1 = new RectF(bounds.left + leftMargin, bounds.top + bigStripHeight / 2 - smallStrip1Height / 2,
                bounds.left + leftMargin + smallStrip1Length, bounds.top + bigStripHeight / 2 + smallStrip1Height / 2);
        smallStrip1Drawable.setBounds((int) smallRect1.left, (int) smallRect1.top, (int) smallRect1.right, (int) smallRect1.bottom);
        smallStrip1Drawable.draw(canvas);

        // Fixed length for the blue strip
        int smallStrip2Length = 200; // Fixed length, adjust as needed
        RectF smallRect2 = new RectF(smallRect1.right + gapBetweenStrips, bounds.top + bigStripHeight / 2 - smallStrip2Height / 2,
                smallRect1.right + gapBetweenStrips + smallStrip2Length, bounds.top + bigStripHeight / 2 + smallStrip2Height / 2);
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
