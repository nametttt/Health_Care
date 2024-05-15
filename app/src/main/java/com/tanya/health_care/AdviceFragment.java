package com.tanya.health_care;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tanya.health_care.code.YaGPTAPI;

import java.io.IOException;
import java.util.ArrayList;

public class AdviceFragment extends Fragment {

    Button back;
    ImageButton search;
    EditText searchEditText;
    LinearLayout adviceLayout;
    TextView header, body;
    ProgressBar progressBar;

    public AdviceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advice, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        back = view.findViewById(R.id.back);
        search = view.findViewById(R.id.search);
        searchEditText = view.findViewById(R.id.searchEditText);
        adviceLayout = view.findViewById(R.id.advice);
        header = view.findViewById(R.id.header);
        body = view.findViewById(R.id.body);
        progressBar = view.findViewById(R.id.progressBar);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new MyCommonHealthFragment());
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchEditText.getText().toString().trim();
                if (!searchText.isEmpty()) {
                    try {
                        sendRequest(searchText);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        // Обработчик нажатия на кнопку "Как мое питание"
        Button nutritionButton = view.findViewById(R.id.nutrition);
        nutritionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "Как мое питание";
                try {
                    sendRequest(text);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Обработчик нажатия на кнопку "Советы по сну"
        Button sleepButton = view.findViewById(R.id.sleep);
        sleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "Советы по сну";
                try {
                    sendRequest(text);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void sendRequest(String searchText) throws IOException {
        progressBar.setVisibility(View.VISIBLE);
        YaGPTAPI yaGPTAPI = new YaGPTAPI();
        ArrayList<String> list = new ArrayList<>();
        list.add("a");

        yaGPTAPI.send(list, searchText, getContext(), new YaGPTAPI.CompletionCallback() {
            @Override
            public void onComplete(String result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        header.setText(searchText);
                        body.setText(result);
                        adviceLayout.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        // Обработка ошибки
                    }
                });
            }
        });
    }
}
