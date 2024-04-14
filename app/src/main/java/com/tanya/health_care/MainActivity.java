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
    private ImageView indicator1, indicator2, indicator3, indicator4, indicator5, indicator6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            viewPager = findViewById(R.id.viewPager);
            indicator1 = findViewById(R.id.indicator1);
            indicator2 = findViewById(R.id.indicator2);
            indicator3 = findViewById(R.id.indicator3);
            indicator4 = findViewById(R.id.indicator4);
            indicator5 = findViewById(R.id.indicator5);
            indicator6 = findViewById(R.id.indicator6);
            btn = findViewById(R.id.continu);
            txt = findViewById(R.id.auth);

            MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(adapter);

            viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    switch (position) {
                        case 0:
                            indicator1.setImageResource(R.drawable.selected_circle_asset);
                            indicator2.setImageResource(R.drawable.circle_asset);
                            indicator3.setImageResource(R.drawable.circle_asset);
                            indicator4.setImageResource(R.drawable.circle_asset);
                            indicator5.setImageResource(R.drawable.circle_asset);
                            indicator6.setImageResource(R.drawable.circle_asset);
                            break;
                        case 1:
                            indicator1.setImageResource(R.drawable.circle_asset);
                            indicator2.setImageResource(R.drawable.selected_circle_asset);
                            indicator3.setImageResource(R.drawable.circle_asset);
                            indicator4.setImageResource(R.drawable.circle_asset);
                            indicator5.setImageResource(R.drawable.circle_asset);
                            indicator6.setImageResource(R.drawable.circle_asset);
                            break;
                        case 2:
                            indicator1.setImageResource(R.drawable.circle_asset);
                            indicator2.setImageResource(R.drawable.circle_asset);
                            indicator3.setImageResource(R.drawable.selected_circle_asset);
                            indicator4.setImageResource(R.drawable.circle_asset);
                            indicator5.setImageResource(R.drawable.circle_asset);
                            indicator6.setImageResource(R.drawable.circle_asset);
                            break;
                        case 3:
                            indicator1.setImageResource(R.drawable.circle_asset);
                            indicator2.setImageResource(R.drawable.circle_asset);
                            indicator3.setImageResource(R.drawable.circle_asset);
                            indicator4.setImageResource(R.drawable.selected_circle_asset);
                            indicator5.setImageResource(R.drawable.circle_asset);
                            indicator6.setImageResource(R.drawable.circle_asset);
                            break;
                        case 4:
                            indicator1.setImageResource(R.drawable.circle_asset);
                            indicator2.setImageResource(R.drawable.circle_asset);
                            indicator3.setImageResource(R.drawable.circle_asset);
                            indicator4.setImageResource(R.drawable.circle_asset);
                            indicator5.setImageResource(R.drawable.selected_circle_asset);
                            indicator6.setImageResource(R.drawable.circle_asset);
                            break;
                        case 5:
                            indicator1.setImageResource(R.drawable.circle_asset);
                            indicator2.setImageResource(R.drawable.circle_asset);
                            indicator3.setImageResource(R.drawable.circle_asset);
                            indicator4.setImageResource(R.drawable.circle_asset);
                            indicator5.setImageResource(R.drawable.circle_asset);
                            indicator6.setImageResource(R.drawable.selected_circle_asset);
                            break;
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}