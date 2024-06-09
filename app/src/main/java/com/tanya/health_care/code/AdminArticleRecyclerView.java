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

import com.squareup.picasso.Picasso;
import com.tanya.health_care.AboutArticleFragment;
import com.tanya.health_care.AdminChangeArticleFragment;
import com.tanya.health_care.AdminHomeActivity;
import com.tanya.health_care.ChangeCommonHealthFragment;
import com.tanya.health_care.ChangeDrinkingFragment;
import com.tanya.health_care.HomeActivity;
import com.tanya.health_care.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdminArticleRecyclerView extends RecyclerView.Adapter<AdminArticleRecyclerView.ViewHolder> {

    private ArrayList<ArticleData> commonHealthData;
    private Context context;

    public AdminArticleRecyclerView(Context context, ArrayList<ArticleData> commonHealthData) {
        this.context = context;
        this.commonHealthData = commonHealthData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_admin_articles_asset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArticleData currentCommon = commonHealthData.get(position);
        holder.title.setText(currentCommon.title);
        holder.category.setText(currentCommon.category);
        String imageUrl = currentCommon.image;
        if (imageUrl == null || imageUrl.isEmpty()) {
            holder.image.setImageResource(R.drawable.notphoto);
        } else {
            Picasso.get().load(imageUrl).placeholder(R.drawable.notphoto).into(holder.image);
        }
        holder.change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminHomeActivity homeActivity = (AdminHomeActivity) v.getContext();
                AdminChangeArticleFragment fragment = new AdminChangeArticleFragment(currentCommon.uid, currentCommon.title, currentCommon.description, currentCommon.category, imageUrl, currentCommon.access);
                Bundle args = new Bundle();
                args.putString("Add", null);
                fragment.setArguments(args);
                homeActivity.replaceFragment(fragment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commonHealthData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, category;
        RelativeLayout relativeLayout;
        Button change;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            category = itemView.findViewById(R.id.category);
            relativeLayout = itemView.findViewById(R.id.rl);
            change = itemView.findViewById(R.id.change);

        }
    }
}
