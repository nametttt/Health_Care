package com.tanya.health_care;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Start_Animation extends AppCompatActivity {
    private final int splash_screen_delay = 2000;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_animation);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        LinearLayout ll = findViewById(R.id.health);
        Animation tablego = AnimationUtils.loadAnimation(this, R.anim.exiting);
        ll.startAnimation(tablego);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (firebaseUser != null) {
                    firebaseUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseUser = auth.getCurrentUser();
                                if (firebaseUser != null) {
                                    if (Objects.equals(firebaseUser.getEmail(), "ya@gmail.com")) {
                                        Intent mainIntent = new Intent(Start_Animation.this, AdminHomeActivity.class);
                                        startActivity(mainIntent);
                                        finish();
                                        overridePendingTransition(R.anim.exiting, R.anim.entering);
                                        return;
                                    } else {
                                        Intent mainIntent = new Intent(Start_Animation.this, HomeActivity.class);
                                        startActivity(mainIntent);
                                    }
                                } else {
                                    Intent mainIntent = new Intent(Start_Animation.this, MainActivity.class);
                                    startActivity(mainIntent);
                                }
                                finish();
                                overridePendingTransition(R.anim.exiting, R.anim.entering);
                            } else {
                            }
                        }
                    });
                } else {
                    Intent mainIntent = new Intent(Start_Animation.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                    overridePendingTransition(R.anim.exiting, R.anim.entering);
                }
            }
        }, splash_screen_delay);
    }
}