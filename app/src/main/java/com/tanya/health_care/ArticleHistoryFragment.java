package com.tanya.health_care;

import static com.tanya.health_care.DrinkingFragment.isSameDay;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.tanya.health_care.code.ArticleData;
import com.tanya.health_care.code.ArticleHistoryData;
import com.tanya.health_care.code.ArticleHistoryRecyclerView;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.dialog.CustomDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArticleHistoryFragment extends Fragment {

    Button back;
    RecyclerView recyclerView;
    ArrayList<ArticleData> articleDataArrayList;
    ArticleHistoryRecyclerView adapter;
    FirebaseUser user;
    DatabaseReference ref;
    FirebaseDatabase mDb;
    ProgressBar progressBar;
    TextView noneArticle;
    GetSplittedPathChild pC = new GetSplittedPathChild();

    public ArticleHistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_article_history, container, false);
        init(v);
        return v;
    }

    void init(View v) {
        try {
            back = v.findViewById(R.id.back);
            progressBar = v.findViewById(R.id.progressBar);
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDb = FirebaseDatabase.getInstance();
            noneArticle = v.findViewById(R.id.noneArticle);
            articleDataArrayList = new ArrayList<>();
            adapter = new ArticleHistoryRecyclerView(getContext(), articleDataArrayList);
            recyclerView = v.findViewById(R.id.recyclerView);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(adapter);
            addDataOnRecyclerView();

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new ArticleFragment());
                }
            });

        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void addDataOnRecyclerView() {
        progressBar.setVisibility(View.VISIBLE);

        String userPath = pC.getSplittedPathChild(user.getEmail());

        ref = mDb.getReference().child("users").child(userPath).child("myArticles");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                articleDataArrayList.clear();

                if (snapshot.exists()) {
                    List<Task<DataSnapshot>> tasks = new ArrayList<>();

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ArticleHistoryData articleHistoryData = ds.getValue(ArticleHistoryData.class);
                        if (articleHistoryData != null && isSameDay(articleHistoryData.lastAdded, new Date())) {
                            DatabaseReference articleRef = mDb.getReference().child("articles").child(articleHistoryData.articleUid);
                            tasks.add(articleRef.get());
                        } else if (articleHistoryData != null) {
                            ds.getRef().removeValue();
                        }
                    }

                    Tasks.whenAllComplete(tasks).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<Task<?>>> task) {
                            for (Task<?> individualTask : tasks) {
                                if (individualTask.isSuccessful()) {
                                    DataSnapshot articleSnapshot = (DataSnapshot) individualTask.getResult();
                                    ArticleData articleData = articleSnapshot.getValue(ArticleData.class);
                                    if (articleData != null) {
                                        articleDataArrayList.add(articleData);
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                            updateUI();
                        }
                    });
                } else {
                    updateUI();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void updateUI() {
        progressBar.setVisibility(View.GONE);
        if (articleDataArrayList.isEmpty()) {
            noneArticle.setVisibility(View.VISIBLE);
        } else {
            noneArticle.setVisibility(View.GONE);
        }
    }
}
