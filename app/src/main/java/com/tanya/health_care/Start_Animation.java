package com.tanya.health_care;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Start_Animation extends AppCompatActivity {
    private  final int splash_screen_delay = 2000;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_animation);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        LinearLayout ll = findViewById(R.id.health);
        Animation tablego = AnimationUtils.loadAnimation(this,R.anim.exiting);
        ll.startAnimation(tablego);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (firebaseUser != null) {
                    firebaseUser.getEmail();
                    assert firebaseUser!=null;
                    if (Objects.equals(firebaseUser.getEmail(), "ya@gmail.com")){
                        Intent mainIntent = new Intent(Start_Animation.this, AdminHomeActivity.class);
                        Start_Animation.this.startActivity(mainIntent);

                        Start_Animation.this.finish();

                        overridePendingTransition(R.anim.exiting, R.anim.entering);

                        return;
                    }
                    Intent mainIntent = new Intent(Start_Animation.this, HomeActivity.class);
                    Start_Animation.this.startActivity(mainIntent);

                }

                else {
                    Intent mainIntent = new Intent(Start_Animation.this, MainActivity.class);
                    Start_Animation.this.startActivity(mainIntent);

                }
                Start_Animation.this.finish();
                overridePendingTransition(R.anim.exiting, R.anim.entering);
            }
        }, splash_screen_delay);
    }
}