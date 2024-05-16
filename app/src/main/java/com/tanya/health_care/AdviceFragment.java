package com.tanya.health_care;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tanya.health_care.code.YaGPTAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                adviceLayout.setVisibility(View.GONE);
                String searchText = searchEditText.getText().toString().trim() + " Если это не связано со здоровьем, напиши, что отвечать не будешь. Отвечай только по здоровью! Никогда не пиши, кто ты и отвечай покроче и попроще!";
                try {
                    sendRequest(searchText, searchEditText.getText().toString().trim());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Button nutritionButton = view.findViewById(R.id.nutrition);
        nutritionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adviceLayout.setVisibility(View.GONE);

                String text = "Как мое питание" + "Сформулируй короче";
                try {
                    sendRequest(text, nutritionButton.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Button waterButton = view.findViewById(R.id.water);
        waterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adviceLayout.setVisibility(View.GONE);

                String text = "Напиши про питьевой режим " + "Сформулируй короче";
                try {
                    sendRequest(text, waterButton.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Button gigienaButton = view.findViewById(R.id.gigiena);
        gigienaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adviceLayout.setVisibility(View.GONE);

                String text = "Расскажи правила личной гигиены" + " Сформулируй короче";
                try {
                    sendRequest(text, gigienaButton.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Button sleepButton = view.findViewById(R.id.sleep);
        sleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adviceLayout.setVisibility(View.GONE);
                String text = "Советы по сну" + "Сформулируй короче";
                try {
                    sendRequest(text, sleepButton.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void sendRequest(String searchText, String bodys) throws IOException {
        progressBar.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        YaGPTAPI yaGPTAPI = new YaGPTAPI();
        yaGPTAPI.send(searchText, getContext(), new YaGPTAPI.ResponseCallback() {
            @Override
            public void onResponseReceived(final String response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String lastResponse = null;

                            for (String resp : response.split("\n")) {
                                lastResponse = resp;
                            }

                            if (lastResponse != null) {
                                JSONObject jsonObject = new JSONObject(lastResponse);
                                JSONArray alternatives = jsonObject.getJSONObject("result").getJSONArray("alternatives");
                                if (alternatives.length() > 0) {
                                    JSONObject lastAlternative = alternatives.getJSONObject(alternatives.length() - 1); // Получаем последний фрагмент
                                    final String text = lastAlternative.getJSONObject("message").getString("text");
                                    header.setText(bodys);
                                    body.setText(text);
                                    progressBar.setVisibility(View.GONE);
                                    adviceLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            header.setText(searchText);
                            body.setText("Что-то не так");
                            progressBar.setVisibility(View.GONE);
                            adviceLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }
}
