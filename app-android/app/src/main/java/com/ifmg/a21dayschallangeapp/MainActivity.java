package com.ifmg.a21dayschallangeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.HorizontalScrollView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChallengeAdapter adapter;
    private List<Challenge> challengeList = new ArrayList<>();
    private final List<Challenge> challengeListFull = new ArrayList<>();

    private TextView emptyStateText;
    private LinearLayout layoutCategorias;
    private HorizontalScrollView categoriasScroll;
    private FloatingActionButton fabAddChallenge;
    private Button btnAddChallenge;

    private Button activeFilterButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerDesafios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyStateText = findViewById(R.id.emptyStateText);
        layoutCategorias = findViewById(R.id.layoutCategorias);
        categoriasScroll = findViewById(R.id.categoriasScroll);

        btnAddChallenge = findViewById(R.id.btnAddChallenge);

        fabAddChallenge = findViewById(R.id.fabAddChallenge);
        fabAddChallenge.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateChallengeActivity.class);
            startActivity(intent);
        });

        adapter = new ChallengeAdapter(challengeList);
        recyclerView.setAdapter(adapter);
        fetchUserChallenges();

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_progress) {
                intent = new Intent(this, ProgressActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_challenge) {
                intent = new Intent(this, CreateChallengeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
            return true;
        });

    }

    private void fetchUserChallenges() {
        new Thread(() -> {

            JSONObject response = ApiClient.get(this, "/challenges/user");

            runOnUiThread(() -> {
                if (response != null && response.optInt("code") == 200) {
                    try {
                        JSONArray challengesArray = response.optJSONArray("userChallenges");

                        challengeListFull.clear();
                        challengeList.clear();

                        if (challengesArray == null || challengesArray.length() == 0) {
                            handleEmptyState(true);
                            return;
                        }

                        for (int i = 0; i < challengesArray.length(); i++) {
                            JSONObject challengeJson = challengesArray.getJSONObject(i);
                            Challenge challenge = new Challenge(challengeJson);

                            challengeListFull.add(challenge);
                            challengeList.add(challenge);
                        }

                        handleEmptyState(false);
                        generateDynamicCategories(challengeListFull);

                        adapter.notifyDataSetChanged();
                        Log.i("MainActivity", "Desafios do usuário carregados com sucesso: " + challengeList.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                        handleEmptyState(true);
                        Toast.makeText(this, "Erro ao processar a lista de desafios.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    String message = response != null ? response.optString("message", "Erro de rede/servidor.") : "Erro de rede.";
                    Toast.makeText(this, "Erro ao carregar desafios: " + message, Toast.LENGTH_LONG).show();
                    handleEmptyState(true);
                }
            });
        }).start();
    }

    private void handleEmptyState(boolean isEmpty) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
            categoriasScroll.setVisibility(View.GONE);

            btnAddChallenge.setVisibility(View.VISIBLE);
            fabAddChallenge.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateText.setVisibility(View.GONE);
            categoriasScroll.setVisibility(View.VISIBLE);

            btnAddChallenge.setVisibility(View.GONE);
            fabAddChallenge.setVisibility(View.VISIBLE);
        }
    }

    private void generateDynamicCategories(List<Challenge> challenges) {
        // 1. Coletar categorias únicas
        Set<String> uniqueCategories = new HashSet<>();
        for (Challenge challenge : challenges) {
            uniqueCategories.add(challenge.getCategory());
        }

        layoutCategorias.removeAllViews();

        addButtonToLayout(layoutCategorias, "Todos", true);

        for (String category : uniqueCategories) {
            if (category != null && !category.isEmpty()) {
                addButtonToLayout(layoutCategorias, category, false);
            }
        }
    }

    private void addButtonToLayout(LinearLayout parentLayout, String text, boolean isSelected) {
        Button btn = new Button(this, null, androidx.appcompat.R.attr.buttonStyleSmall);
        btn.setText(text);

        btn.setTextColor(getResources().getColor(R.color.orange, getTheme()));
        btn.setBackgroundTintList(getResources().getColorStateList(R.color.white, getTheme()));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMarginEnd(8);
        btn.setLayoutParams(params);
        btn.setOnClickListener(v -> {
            filterChallenges(text);
        });

        parentLayout.addView(btn);
        if (text.equals("Todos") && activeFilterButton == null) {
            updateCategoryButtonAppearance(btn);
        }
    }

    public void performCheckin(int challengeId, ImageView checkIcon, boolean newState) {
        checkIcon.setEnabled(false);

        new Thread(() -> {
            try {
                JSONObject payload = new JSONObject();
                payload.put("user_challenge_id", challengeId);
                payload.put("completed", newState);

                JSONObject response = ApiClient.post(this, "/challenges/checkin", payload.toString());

                runOnUiThread(() -> {
                    if (response != null && response.optInt("code") == 200) {
                        Toast.makeText(this, newState ? "Check-in registrado!" : "Check-in desfeito!", Toast.LENGTH_SHORT).show();
                        fetchUserChallenges();
                    } else {
                        Toast.makeText(this, "Erro ao registrar check-in.", Toast.LENGTH_LONG).show();
                        checkIcon.setEnabled(true);

                        if (newState) {
                            checkIcon.setImageResource(R.drawable.ic_unchecked);
                        } else {
                            checkIcon.setImageResource(R.drawable.ic_checked);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateCategoryButtonAppearance(Button clickedButton) {
        if (activeFilterButton != null) {

            activeFilterButton.setBackgroundTintList(getResources().getColorStateList(R.color.white, getTheme()));
            activeFilterButton.setTextColor(getResources().getColor(R.color.orange, getTheme()));
        }

        if (clickedButton != null) {
            clickedButton.setBackgroundTintList(getResources().getColorStateList(R.color.orange, getTheme()));
            clickedButton.setTextColor(getResources().getColor(R.color.white, getTheme()));
        }

        activeFilterButton = clickedButton;
    }

    private void filterChallenges(String category) {
        challengeList.clear();

        if (category.equals("Todos")) {
            challengeList.addAll(challengeListFull);
        } else {
            for (Challenge challenge : challengeListFull) {
                if (challenge.getCategory() != null && challenge.getCategory().equals(category)) {
                    challengeList.add(challenge);
                }
            }
        }

        Button clickedButton = null;
        for (int i = 0; i < layoutCategorias.getChildCount(); i++) {
            View v = layoutCategorias.getChildAt(i);
            if (v instanceof Button) {
                Button btn = (Button) v;
                if (btn.getText().toString().equals(category)) {
                    clickedButton = btn;
                    break;
                }
            }
        }

        updateCategoryButtonAppearance(clickedButton);
        adapter.notifyDataSetChanged();

        if (challengeList.isEmpty()) {
            Toast.makeText(this, "Nenhum desafio encontrado na categoria: " + category, Toast.LENGTH_SHORT).show();
        }
    }

}