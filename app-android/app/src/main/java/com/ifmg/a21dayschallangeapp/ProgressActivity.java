package com.ifmg.a21dayschallangeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.ifmg.a21dayschallangeapp.StatsModel;

import org.json.JSONObject;
import org.json.JSONArray;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.components.XAxis;

public class ProgressActivity extends AppCompatActivity {

    private LinearLayout layoutChallengeChips;
    private ImageView btnVoltar;
    private List<Challenge> challengeList = new ArrayList<>();

    private TextView tvCurrentStreak, tvCompletionRate, tvSkippedDays, tvDaysRemaining;

    private LineChart lineChartProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        layoutChallengeChips = findViewById(R.id.layoutChallengeChips);
        btnVoltar = findViewById(R.id.btnVoltar);

        tvCurrentStreak = findViewById(R.id.tvCurrentStreak);
        tvCompletionRate = findViewById(R.id.tvCompletionRate);
        tvSkippedDays = findViewById(R.id.tvSkippedDays);
        tvDaysRemaining = findViewById(R.id.tvDaysRemaining);
        lineChartProgress = findViewById(R.id.lineChartProgress);

        btnVoltar.setOnClickListener(v -> finish());

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_progress);

        bottomNavigation.setOnItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                intent = new Intent(this, MainActivity.class);
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
            return false;
        });

        fetchAndDisplayOverallProgress();

        fetchAndDisplayProgress();
    }


    private void fetchAndDisplayProgress() {
        new Thread(() -> {
            JSONObject response = ApiClient.get(this, "/challenges/user");

            runOnUiThread(() -> {
                if (response != null && response.optInt("code") == 200) {

                    org.json.JSONArray challengesArray = response.optJSONArray("userChallenges");

                    if (challengesArray != null) {
                        List<Challenge> activeChallenges = new ArrayList<>();
                        for (int i = 0; i < challengesArray.length(); i++) {
                            activeChallenges.add(new Challenge(challengesArray.optJSONObject(i)));
                        }

                        if (!activeChallenges.isEmpty()) {
                            this.challengeList = activeChallenges;
                            generateChallengeChips(this.challengeList);

                            if (layoutChallengeChips.getChildCount() > 0) {
                                layoutChallengeChips.getChildAt(0).performClick();
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Erro ao carregar desafios.", Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }


    private void generateChallengeChips(List<Challenge> challenges) {
        layoutChallengeChips.removeAllViews();

        for (Challenge challenge : challenges) {
            TextView chip = new TextView(this);
            chip.setText(challenge.getTitle());

            chip.setPadding(30, 15, 30, 15);
            chip.setTextSize(14);
            chip.setSingleLine(true);
            chip.setBackgroundResource(R.drawable.chip_background_default);
            chip.setTextColor(getResources().getColor(android.R.color.black));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 16, 0);
            chip.setLayoutParams(params);

            chip.setOnClickListener(v -> handleChipSelection((TextView) v, String.valueOf(challenge.getId())));

            layoutChallengeChips.addView(chip);
        }
    }


    private void handleChipSelection(TextView selectedChip, String challengeId) {
        for (int i = 0; i < layoutChallengeChips.getChildCount(); i++) {
            View child = layoutChallengeChips.getChildAt(i);
            if (child instanceof TextView) {
                child.setBackgroundResource(R.drawable.chip_background_default);
                ((TextView) child).setTextColor(getResources().getColor(android.R.color.black));
            }
        }

        selectedChip.setBackgroundResource(R.drawable.chip_background_selected);
        selectedChip.setTextColor(getResources().getColor(android.R.color.white));

        fetchAndDisplayStats(challengeId);
    }

    private void fetchAndDisplayStats(String challengeId) {
        new Thread(() -> {
            JSONObject response = ApiClient.get(this, "/challenges/stats/" + challengeId);

            runOnUiThread(() -> {
                if (response != null && response.optInt("code") == 200) {

                    JSONObject statsJson = response.optJSONObject("stats");

                    if (statsJson != null) {
                        try {
                            Gson gson = new Gson();
                            StatsModel stats = gson.fromJson(statsJson.toString(), StatsModel.class);

                            updateStatsUI(stats);


                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Erro ao processar estatísticas.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Estatísticas não encontradas para o desafio.", Toast.LENGTH_LONG).show();
                        updateStatsUI(null);
                    }
                } else {
                    Toast.makeText(this, "Falha ao buscar estatísticas da API.", Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }

    private void updateStatsUI(StatsModel stats) {
        if (stats == null) {
            tvCurrentStreak.setText("🔥 0");
            tvCompletionRate.setText("✓ 0 %");
            tvSkippedDays.setText("✕ 0");
            tvDaysRemaining.setText("◷ 0");
            return;
        }

        tvCurrentStreak.setText("🔥 " + stats.getCurrentStreak());
        tvCompletionRate.setText("✓ " + stats.getHabitCompletionRate() + " %");
        tvSkippedDays.setText("✕ " + stats.getSkippedDays());
        tvDaysRemaining.setText("◷ " + stats.getDaysRemaining());
    }

    private void fetchAndDisplayOverallProgress() {
        new Thread(() -> {
            JSONObject response = ApiClient.get(this, "/challenges/overall-progress");

            runOnUiThread(() -> {
                if (response != null && response.optInt("code") == 200) {
                    try {
                        Gson gson = new Gson();
                        OverallProgressModel model = gson.fromJson(response.toString(), OverallProgressModel.class);

                        if (model.getProgressData() != null && !model.getProgressData().isEmpty()) {
                            setupChart(model.getProgressData());
                        } else {
                            lineChartProgress.clear();
                            lineChartProgress.setNoDataText("Nenhum desafio concluído nos últimos 30 dias.");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erro ao processar dados do gráfico.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Falha ao buscar dados do gráfico.", Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }


    private void setupChart(List<OverallProgressModel.ProgressDay> data) {

        List<Entry> entries = new ArrayList<>();
        List<String> dateLabels = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            entries.add(new Entry(i, data.get(i).getCount()));
            dateLabels.add(data.get(i).getDate());
        }

        LineDataSet dataSet = new LineDataSet(entries, "Desafios Concluídos");
        dataSet.setColor(getResources().getColor(R.color.orange));
        dataSet.setValueTextColor(getResources().getColor(R.color.black));
        dataSet.setCircleColor(getResources().getColor(R.color.orange));
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        lineChartProgress.setData(lineData);

        XAxis xAxis = lineChartProgress.getXAxis();
        xAxis.setValueFormatter(new DateAxisFormatter(dateLabels));

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-45);
        xAxis.setDrawGridLines(false);

        lineChartProgress.getDescription().setEnabled(false); // Remove o texto de descrição padrão
        lineChartProgress.getLegend().setEnabled(false); // Remove a legenda
        lineChartProgress.getXAxis().setDrawGridLines(false); // Remove linhas de grade X
        lineChartProgress.getAxisRight().setEnabled(false); // Remove eixo Y da direita
        lineChartProgress.getAxisLeft().setAxisMinimum(0f); // Começa o Y em 0
        lineChartProgress.animateX(1000); // Animação

        lineChartProgress.getAxisLeft().setAxisMinimum(0f);
        lineChartProgress.getAxisRight().setEnabled(false);
        lineChartProgress.animateX(1000);


        lineChartProgress.invalidate(); // Desenha o gráfico
    }
}

