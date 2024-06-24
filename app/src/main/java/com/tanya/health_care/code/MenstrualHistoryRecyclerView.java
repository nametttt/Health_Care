package com.tanya.health_care.code;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.HomeActivity;
import com.tanya.health_care.HomeFragment;
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
    private Map<Integer, String> keyMap = new HashMap<>();
    private FirebaseDatabase mDb = FirebaseDatabase.getInstance();
    private GetSplittedPathChild pC = new GetSplittedPathChild();

    public MenstrualHistoryRecyclerView(Context context, ArrayList<MenstrualData> menstrualData) {
        this.context = context;
        this.menstrualData = menstrualData;
        loadKeysFromDatabase();
    }

    private void loadKeysFromDatabase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
                    // Handle database read error
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

        if (position == 0) {
            holder.durationMenstrual.setText("Текущий");
        } else {
            MenstrualData prevData = menstrualData.get(position - 1);
            Calendar prevEnd = prevData.startDate != null ? prevData.startDate : prevData.endDate;
            long duration = currentData.endDate.getTimeInMillis() - prevEnd.getTimeInMillis();
            int days = (int) Math.abs((duration / (1000 * 60 * 60 * 24)));
            holder.durationMenstrual.setText(days + " дней");
        }
        long menstrualDay = currentData.endDate.getTimeInMillis() - currentData.startDate.getTimeInMillis();
        int periodDays = (int) Math.abs((menstrualDay / (1000 * 60 * 60 * 24)));

        String menstrualDays = dateFormat.format(currentData.startDate.getTime()) + " - " +
                (currentData.endDate == null ? "текущий" : dateFormat.format(currentData.endDate.getTime()));
        holder.menstrualDays.setText(menstrualDays);

        CustomStripDrawable customStripDrawable = new CustomStripDrawable(periodDays, position);
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
                                            homeActivity.replaceFragment(new HomeFragment());
                                        }
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
