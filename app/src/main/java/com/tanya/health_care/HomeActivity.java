package com.tanya.health_care;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tanya.health_care.code.GetSplittedPathChild;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HomeActivity extends AppCompatActivity {

    int waterValue, sleepValue, nutritionValue;
    float weightValue;
    String pressureValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        replaceFragment(new HomeFragment());
        askNotificationPermission();
        Locale locale = new Locale("ru");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }
                String token = task.getResult();
                String msg = getString(R.string.msg_token_fmt, token);
                saveTokenToDatabase(token);
            }
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnItemSelectedListener(item -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                GetSplittedPathChild pC = new GetSplittedPathChild();
                String userPath = pC.getSplittedPathChild(user.getEmail());

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        replaceFragment(new HomeFragment());
                        break;
                    case R.id.navigation_search:
                        replaceFragment(new ArticleFragment());
                        break;
                    case R.id.navigation_chat:
                        fetchDataAsync(userPath).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                replaceFragment(new MyCommonHealthFragment(waterValue, pressureValue, weightValue, sleepValue, nutritionValue));
                            }
                        });
                        break;
                    case R.id.navigation_profile:
                        ProfileFragment fragment = new ProfileFragment();
                        replaceFragment(fragment);
                        break;
                }
            }
            return true;
        });
    }

    private Task<Void> fetchDataAsync(String userPath) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users").child(userPath).child("values");
        Task<DataSnapshot> waterValueTask = database.child("WaterValue").get();
        Task<DataSnapshot> pressureValueTask = database.child("PressureValue").get();
        Task<DataSnapshot> weightValueTask = database.child("WeightValue").get();
        Task<DataSnapshot> sleepValueTask = database.child("SleepValue").get();
        Task<DataSnapshot> nutritionValueTask = database.child("NutritionValue").get();

        return Tasks.whenAll(waterValueTask, pressureValueTask).continueWith(task -> {
            if (task.isSuccessful()) {
                waterValue = waterValueTask.getResult().getValue(int.class);
                pressureValue = pressureValueTask.getResult().getValue(String.class);
                weightValue = weightValueTask.getResult().getValue(float.class);
                sleepValue = sleepValueTask.getResult().getValue(int.class);
                nutritionValue = nutritionValueTask.getResult().getValue(int.class);

            } else {
                Log.e(TAG, "Failed to fetch data", task.getException());
            }
            return null;
        });
    }

    private void saveTokenToDatabase(String token) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference ref = db.getReference("users");
            GetSplittedPathChild pC = new GetSplittedPathChild();
            DatabaseReference userRef = ref.child(pC.getSplittedPathChild(user.getEmail()));
            userRef.child("deviceToken").setValue(token)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Token saved successfully");
                            } else {
                                Log.e(TAG, "Failed to save token to database", task.getException());
                            }
                        }
                    });
        } else {
            Log.e(TAG, "Current user is null");
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_home, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            });

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}
