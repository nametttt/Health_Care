package com.tanya.health_care.code;

import static com.google.android.material.internal.ContextUtils.getActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.AddMenstrulDateFragment;
import com.tanya.health_care.ChangeFoodWeightFragment;
import com.tanya.health_care.DrinkingStatisticFragment;
import com.tanya.health_care.HomeActivity;
import com.tanya.health_care.HomeFragment;
import com.tanya.health_care.MenstrualFragment;
import com.tanya.health_care.MenstrualStatisticFragment;
import com.tanya.health_care.R;
import com.tanya.health_care.dialog.CustomDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MenstrualHistoryRecyclerView extends RecyclerView.Adapter<MenstrualHistoryRecyclerView.ViewHolder> {

    private ArrayList<MenstrualData> menstrualData;
    private Context context;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM", Locale.getDefault());
    private DatabaseReference ref;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    FirebaseDatabase mDb;
    private Map<Integer, String> keyMap = new HashMap<>();

    public MenstrualHistoryRecyclerView(Context context, ArrayList<MenstrualData> menstrualData) {
        this.context = context;
        this.menstrualData = menstrualData;
        loadKeysFromDatabase();
    }

    private void loadKeysFromDatabase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
        if (user != null) {
            DatabaseReference databaseRef = mDb.getReference("users")
                    .child(pC.getSplittedPathChild(user.getEmail()))
                    .child("characteristic")
                    .child("menstrual")
                    .child("dates");

            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int index = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        keyMap.put(index, dataSnapshot.getKey());
                        index++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Обработка ошибки чтения из базы данных
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_menstrual_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenstrualData currentData = menstrualData.get(position);

        if (position < menstrualData.size() - 1) {
            MenstrualData nextData = menstrualData.get(position + 1);

            Calendar currentStart = currentData.startDate;
            Calendar nextStart = nextData.startDate;
            Calendar currentEnd = currentData.endDate != null ? currentData.endDate : currentStart;
            Calendar startDate, endDate;
            if (currentEnd.after(nextStart)) {
                startDate = nextStart;
                endDate = currentEnd;
            } else {
                startDate = currentStart;
                endDate = nextStart;
            }

            long duration = endDate.getTimeInMillis() - startDate.getTimeInMillis();
            long days = duration / (1000 * 60 * 60 * 24);

            holder.durationMenstrual.setText(days + " дней");
        } else {
            holder.durationMenstrual.setText("Текущий");
        }

        String menstrualDays = dateFormat.format(currentData.startDate.getTime()) + " - " +
                (currentData.endDate == null ? "текущий" : dateFormat.format(currentData.endDate.getTime()));
        holder.menstrualDays.setText(menstrualDays);

        CustomStripDrawable customStripDrawable = new CustomStripDrawable();
        holder.imageView.setImageDrawable(customStripDrawable);

        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) return;

                new AlertDialog.Builder(context)
                        .setTitle("Подтверждение удаления")
                        .setMessage("Вы уверены, что хотите удалить эту запись?")
                        .setPositiveButton("Да", (dialog, which) -> {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null && keyMap.containsKey(currentPosition)) {
                                DatabaseReference ref = mDb.getReference("users")
                                        .child(pC.getSplittedPathChild(user.getEmail()))
                                        .child("characteristic")
                                        .child("menstrual")
                                        .child("dates")
                                        .child(keyMap.get(currentPosition));
                                ref.removeValue();
                                CustomDialog dialogFragment = new CustomDialog("Удаление прошло успешно!", true);
                                if (context instanceof FragmentActivity) {
                                    dialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "custom_dialog");
                                }

                                DatabaseReference newref = mDb.getReference("users")
                                        .child(pC.getSplittedPathChild(user.getEmail()))
                                        .child("characteristic")
                                        .child("menstrual")
                                        .child("dates");
                                newref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists()) {
                                            HomeActivity homeActivity = (HomeActivity) v.getContext();
                                            homeActivity.replaceFragment(new HomeFragment());                            }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + databaseError.getMessage(), false);
                                        dialogFragment.show(dialogFragment.getChildFragmentManager(), "custom_dialog");
                                    }
                                });


                                menstrualData.remove(currentPosition);
                                notifyItemRemoved(currentPosition);
                                notifyItemRangeChanged(currentPosition, menstrualData.size());


                            }
                        })
                        .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return menstrualData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView durationMenstrual, menstrualDays;
        ImageView imageView, deleteImage;
        RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            durationMenstrual = itemView.findViewById(R.id.durationMenstrual);
            menstrualDays = itemView.findViewById(R.id.menstrualDays);
            imageView = itemView.findViewById(R.id.imageView);
            deleteImage = itemView.findViewById(R.id.deleteImage);
            relativeLayout = itemView.findViewById(R.id.relative);
        }
    }
}
