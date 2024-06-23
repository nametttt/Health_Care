package com.tanya.health_care.code;

import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tanya.health_care.AboutArticleFragment;
import com.tanya.health_care.HomeActivity;
import com.tanya.health_care.R;

import java.util.ArrayList;

public class ArticleHistoryRecyclerView extends RecyclerView.Adapter<ArticleHistoryRecyclerView.ViewHolder> {

    private ArrayList<ArticleData> commonHealthData;
    private Context context;

    public ArticleHistoryRecyclerView(Context context, ArrayList<ArticleData> commonHealthData) {
        this.context = context;
        this.commonHealthData = commonHealthData;
    }

    public void updateSource(ArrayList<ArticleData> commonHealthData) {
        this.commonHealthData = commonHealthData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_history_asset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArticleData currentCommon = commonHealthData.get(position);
        holder.title.setText(currentCommon.title);

        String imageUrl = currentCommon.image;
        if (imageUrl == null || imageUrl.isEmpty()) {
            holder.image.setImageResource(R.drawable.notphoto);
        } else {
            Picasso.get().load(imageUrl).placeholder(R.drawable.notphoto).into(holder.image);
        }

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) v.getContext();
                homeActivity.replaceFragment(new AboutArticleFragment(currentCommon.uid,currentCommon.title, currentCommon.description, imageUrl, "history"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return commonHealthData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            relativeLayout = itemView.findViewById(R.id.rl);

            title.setEllipsize(TextUtils.TruncateAt.END);
            title.setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_FULL);
        }
    }
}
