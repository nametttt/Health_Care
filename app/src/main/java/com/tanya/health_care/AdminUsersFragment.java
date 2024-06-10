package com.tanya.health_care;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.AdminUsersRecyclerView;
import com.tanya.health_care.code.UserData;
import com.tanya.health_care.dialog.CustomDialog;
import java.util.ArrayList;

public class AdminUsersFragment extends Fragment {

    Button addUser;
    RecyclerView recyclerView;
    ArrayList<UserData> users;
    AdminUsersRecyclerView adapter;
    FirebaseUser user;
    DatabaseReference ref;
    FirebaseDatabase mDb;
    ImageButton searchButton;
    EditText searchEditText;
    ProgressBar progressBar;
    Spinner userTypeSpinner;
    ArrayAdapter<CharSequence> spinnerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_users, container, false);
        init(v);
        return v;
    }

    void init(View v){
        try {
            addUser = v.findViewById(R.id.addUser);

            user = FirebaseAuth.getInstance().getCurrentUser();
            mDb = FirebaseDatabase.getInstance();
            progressBar = v.findViewById(R.id.progressBar);
            users = new ArrayList<UserData>();
            adapter = new AdminUsersRecyclerView(getContext(), users);
            recyclerView = v.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            addDataOnRecyclerView();

            searchButton = v.findViewById(R.id.search);
            searchEditText = v.findViewById(R.id.searchEditText);

            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String searchText = searchEditText.getText().toString().trim();
                    if (searchText.isEmpty()) {
                        searchButton.setClickable(false);
                        searchButton.setImageResource(R.drawable.search);
                        addDataOnRecyclerView();
                    } else {
                        searchButton.setClickable(true);
                        searchButton.setImageResource(R.drawable.close);
                        filterUsers(searchText);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            addUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();
                    AdminAddUserFragment fragment = new AdminAddUserFragment();
                    homeActivity.replaceFragment(fragment);
                }
            });

            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchEditText.setText(null);
                    searchButton.setClickable(false);
                    searchButton.setImageResource(R.drawable.search);
                }
            });

            userTypeSpinner = v.findViewById(R.id.userTypeSpinner);
            spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.user_types1, android.R.layout.simple_spinner_item);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            userTypeSpinner.setAdapter(spinnerAdapter);
            userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedRole = parent.getItemAtPosition(position).toString();
                    if (selectedRole.equals("Все")) {
                        filterUsersByRole("All");
                    } else if (selectedRole.equals("Пользователи")) {
                        filterUsersByRole("Пользователь");
                    } else if (selectedRole.equals("Администраторы")) {
                        filterUsersByRole("Администратор");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    addDataOnRecyclerView();
                }
            });

        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void addDataOnRecyclerView() {
        try {
            progressBar.setVisibility(View.VISIBLE);

            ref = mDb.getReference().child("users");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    users.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        UserData userData = ds.getValue(UserData.class);
                        if (userData != null && userData.getEmail() != null && !userData.getEmail().equals(user.getEmail())) {
                            users.add(userData);
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void filterUsers(String searchText) {
        try {
            ref = mDb.getReference().child("users");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    users.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        UserData userData = ds.getValue(UserData.class);
                        if (userData != null && userData.getEmail() != null && !userData.getEmail().equals(user.getEmail())) {
                            if (userData.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                                    userData.getEmail().toLowerCase().contains(searchText.toLowerCase())) {
                                users.add(userData);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void filterUsersByRole(String role) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            ref = mDb.getReference().child("users");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    users.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        UserData userData = ds.getValue(UserData.class);
                        if (userData != null && userData.getEmail() != null && !userData.getEmail().equals(user.getEmail())) {
                            if (role.equals("All") || role.isEmpty() || userData.getRole().equalsIgnoreCase(role)) {
                                users.add(userData);
                            }
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
}
