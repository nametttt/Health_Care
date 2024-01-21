package com.tanya.health_care;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import java.util.Objects;

public class Start_Animation extends AppCompatActivity {
    private  final int splash_screen_delay = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_animation);

        LinearLayout health = findViewById(R.id.health);
        Animation tablego = AnimationUtils.loadAnimation(this,R.anim.exiting);
        health.startAnimation(tablego);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent mainIntent = new Intent(Start_Animation.this, LoginActivity.class);
                Start_Animation.this.startActivity(mainIntent);

                Start_Animation.this.finish();

                    overridePendingTransition(R.anim.exiting, R.anim.entering);



            }
        }, splash_screen_delay);

    }
}