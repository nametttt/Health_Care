package com.tanya.health_care;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.viewpager.widget.ViewPager;
import android.widget.ImageView;

import com.tanya.health_care.code.MyPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private Button btn;
    private TextView txt;
    private ViewPager viewPager;
    private ImageView indicator1, indicator2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        indicator1 = findViewById(R.id.indicator1);
        indicator2 = findViewById(R.id.indicator2);
        btn = findViewById(R.id.continu);
        txt = findViewById(R.id.auth);

        // Создаем адаптер для ViewPager
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        // Добавляем слушателя изменения страницы ViewPager
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // Обновляем состояние индикаторов при изменении страницы
                if (position == 0) {
                    indicator1.setImageResource(R.drawable.selected_circle_asset);
                    indicator2.setImageResource(R.drawable.circle_asset);
                } else {
                    indicator1.setImageResource(R.drawable.circle_asset);
                    indicator2.setImageResource(R.drawable.selected_circle_asset);
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegActivityEmail.class);
                startActivity(intent);
            }
        });

        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}