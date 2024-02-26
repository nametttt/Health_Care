package com.tanya.health_care.code;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tanya.health_care.AboutArticleFragment;
import com.tanya.health_care.AdminChangeUserFragment;
import com.tanya.health_care.AdminHomeActivity;
import com.tanya.health_care.ChangeCommonHealthFragment;
import com.tanya.health_care.ChangeDrinkingFragment;
import com.tanya.health_care.HomeActivity;
import com.tanya.health_care.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdminUsersRecyclerView extends RecyclerView.Adapter<AdminUsersRecyclerView.ViewHolder> {

    private ArrayList<User> users;
    private Context context;

    public AdminUsersRecyclerView(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_admin_users_asset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User currentCommon = users.get(position);
        holder.names.setText(currentCommon.email);

        holder.roles.setText(currentCommon.role);

        holder.change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminHomeActivity homeActivity = (AdminHomeActivity) v.getContext();
                homeActivity.replaceFragment(new AdminChangeUserFragment(currentCommon.name, currentCommon.email, currentCommon.role, currentCommon.gender, currentCommon.birthday));
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView roles, names;
        RelativeLayout relativeLayout;
        Button change;

        public ViewHolder(View itemView) {
            super(itemView);
            roles = itemView.findViewById(R.id.userRole);
            names = itemView.findViewById(R.id.userEmail);
            relativeLayout = itemView.findViewById(R.id.rl);
            change = itemView.findViewById(R.id.changeUser);

        }
    }
}
