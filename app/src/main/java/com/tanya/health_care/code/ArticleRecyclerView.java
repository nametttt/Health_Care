package com.tanya.health_care.code;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tanya.health_care.AboutArticleFragment;
import com.tanya.health_care.ChangeCommonHealthFragment;
import com.tanya.health_care.ChangeDrinkingFragment;
import com.tanya.health_care.HomeActivity;
import com.tanya.health_care.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ArticleRecyclerView extends RecyclerView.Adapter<ArticleRecyclerView.ViewHolder> {

    private ArrayList<ArticleData> commonHealthData;
    private Context context;

    public ArticleRecyclerView(Context context, ArrayList<ArticleData> commonHealthData) {
        this.context = context;
        this.commonHealthData = commonHealthData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_article_asset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArticleData currentCommon = commonHealthData.get(position);
        holder.title.setText(currentCommon.title);

        int resId = 0;
        switch (currentCommon.image){
            case("1.png"):
                resId = R.drawable.p1;
                break;
            case("2.png"):
                resId = R.drawable.p2;
                break;
            case("3.png"):
                resId = R.drawable.p3;
                break;
            case("4.png"):
                resId = R.drawable.p4;
                break;
            case("5.png"):
                resId = R.drawable.p5;
                break;
            case("6.png"):
                resId = R.drawable.p6;
                break;
            case("7.png"):
                resId = R.drawable.p7;
                break;
            default:
                resId = R.drawable.p1;
        }

        holder.image.setImageResource(resId);

        int finalResId = resId;
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) v.getContext();
                homeActivity.replaceFragment(new AboutArticleFragment(currentCommon.title, currentCommon.description, finalResId));
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

        }
    }
}
