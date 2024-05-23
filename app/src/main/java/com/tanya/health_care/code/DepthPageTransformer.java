package com.tanya.health_care.code;
import android.view.View;
import androidx.viewpager.widget.ViewPager;

public class DepthPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.75f;

    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();

        if (position < -1) { // [-Infinity,-1)
            // Эта страница находится вне экрана слева.
            view.setAlpha(0f);
        } else if (position <= 0) { // [-1,0]
            // Используйте стандартный переход при пролистывании влево.
            view.setAlpha(1f);
            view.setTranslationX(0);
            view.setScaleX(1f);
            view.setScaleY(1f);
        } else if (position <= 1) { // (0,1]
            // Затухание страницы.
            view.setAlpha(1 - position);

            // Противодействуйте стандартному переходу.
            view.setTranslationX(pageWidth * -position);

            // Масштабирование страницы вниз (между MIN_SCALE и 1).
            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        } else { // (1,+Infinity]
            // Эта страница находится вне экрана справа.
            view.setAlpha(0f);
        }
    }
}
