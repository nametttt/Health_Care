package com.tanya.health_care;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.AdminUsersRecyclerView;
import com.tanya.health_care.code.User;

import java.util.ArrayList;

public class AdminUsersFragment extends Fragment {

    Button addUser;
    RecyclerView recyclerView;
    ArrayList<User> users;
    AdminUsersRecyclerView adapter;
    FirebaseUser user;
    DatabaseReference ref;
    FirebaseDatabase mDb;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_users, container, false);
        init(v);
        return v;
    }

    void init(View v){
        addUser = v.findViewById(R.id.addUser);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDb = FirebaseDatabase.getInstance();

        users = new ArrayList<User>();
        adapter = new AdminUsersRecyclerView(getContext(), users);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        addDataOnRecyclerView();

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();
                AdminAddUserFragment fragment = new AdminAddUserFragment();
                homeActivity.replaceFragment(fragment);
            }
        });

    }
    private void addDataOnRecyclerView() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (users.size() > 0) {
                    users.clear();
                }
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getValue();
                    User ps = ds.getValue(User.class);
                    assert ps != null;
                    users.add(ps);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref = mDb.getReference().child("users");

        ref.addValueEventListener(valueEventListener);
    }

}