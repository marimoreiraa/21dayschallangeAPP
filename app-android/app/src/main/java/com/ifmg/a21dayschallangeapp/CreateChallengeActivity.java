package com.ifmg.a21dayschallangeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreateChallengeActivity extends AppCompatActivity {

    private EditText edtNomeDesafio;
    private EditText edtDescricao;
    private Spinner spinnerDuracao;
    private Spinner spinnerFrequencia;
    private Switch switchNotificacao;
    private Button btnCriarDesafio;
    private Button btnCancelar;
    private ImageView btnVoltar;
    private Spinner spinnerCategoria;
    private Spinner spinnerIcone;

    // Lógica de categorias
    private List<Button> categoryButtons = new ArrayList<>();
    private String selectedCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_challenge);

        // Inicializar Views
        initializeViews();

        // Configurar Spinners
        setupSpinners();

        // Configurar botões de categoria e lógica de clique
        setupCategoryButtons();

        // Configurar Ações dos Botões
        setupButtonActions();

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        bottomNavigation.setSelectedItemId(R.id.nav_challenge);

        bottomNavigation.setOnItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_progress) {
                intent = new Intent(this, ProgressActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_home) {
                intent = new Intent(this, MainActivity.class);
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

    private void initializeViews() {
        edtNomeDesafio = findViewById(R.id.edtNomeDesafio);
        edtDescricao = findViewById(R.id.edtDescricao);
        spinnerDuracao = findViewById(R.id.spinnerDuracao);
        spinnerFrequencia = findViewById(R.id.spinnerFrequencia);
        spinnerIcone = findViewById(R.id.spinnerIcone);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        switchNotificacao = findViewById(R.id.switchNotificacao);
        btnCriarDesafio = findViewById(R.id.btnCriarDesafio);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnVoltar = findViewById(R.id.btnVoltar);


    }

    private void setupSpinners() {
        ArrayAdapter<String> duracaoAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"7 Dias", "15 Dias", "21 Dias", "30 Dias", "60 Dias"});
        duracaoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDuracao.setAdapter(duracaoAdapter);

        ArrayAdapter<String> frequenciaAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Diária", "Semanal", "Mensal"});
        frequenciaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrequencia.setAdapter(frequenciaAdapter);

        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Pessoal", "Produtividade", "Bem-Estar", "Saúde"});
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(categoriaAdapter);

        List<IconItem> iconList = new ArrayList<>();
        iconList.add(new IconItem("ic_water", R.drawable.ic_water));
        iconList.add(new IconItem("ic_run", R.drawable.ic_run));
        iconList.add(new IconItem("ic_gym", R.drawable.ic_gym));
        iconList.add(new IconItem("ic_fruits", R.drawable.ic_fruits));
        iconList.add(new IconItem("ic_book", R.drawable.ic_book));
        iconList.add(new IconItem("ic_default", R.drawable.ic_default));

        IconSpinnerAdapter iconAdapter = new IconSpinnerAdapter(this, iconList);
        spinnerIcone.setAdapter(iconAdapter);
    }

    private void setupCategoryButtons() {
        View.OnClickListener categoryClickListener = v -> {
            Button clickedButton = (Button) v;
            selectedCategory = clickedButton.getText().toString();

            for (Button btn : categoryButtons) {

                btn.setBackgroundTintList(getResources().getColorStateList(R.color.white, getTheme()));
                btn.setTextColor(getResources().getColor(R.color.orange, getTheme()));
            }

            clickedButton.setBackgroundTintList(getResources().getColorStateList(R.color.orange, getTheme()));
            clickedButton.setTextColor(getResources().getColor(R.color.white, getTheme()));

            Toast.makeText(this, "Categoria selecionada: " + selectedCategory, Toast.LENGTH_SHORT).show();
        };

        for (Button btn : categoryButtons) {
            btn.setOnClickListener(categoryClickListener);
        }

        if (!categoryButtons.isEmpty()) {
            categoryButtons.get(0).performClick();
        }
    }

    private void setupButtonActions() {
        btnVoltar.setOnClickListener(v -> finish());

        btnCancelar.setOnClickListener(v -> finish());

        btnCriarDesafio.setOnClickListener(v -> createChallenge());
    }

    private void createChallenge() {
        final String nome = edtNomeDesafio.getText().toString().trim();
        final String descricao = edtDescricao.getText().toString().trim();
        final String duracaoStr = spinnerDuracao.getSelectedItem().toString().replace(" Dias", "");
        final int duracao = Integer.parseInt(duracaoStr.split(" ")[0]); // Pega apenas o número (21)
        final String frequencia = spinnerFrequencia.getSelectedItem().toString();
        final boolean notificacao = switchNotificacao.isChecked();
        final String categoria = spinnerCategoria.getSelectedItem().toString();
        IconItem selectedIconItem = (IconItem) spinnerIcone.getSelectedItem();
        final String iconName = selectedIconItem.getIconName();

        if (nome.isEmpty() || duracaoStr.isEmpty() || frequencia.isEmpty() || categoria.isEmpty()) {
            Toast.makeText(this, "Preencha o nome, duração, frequência e selecione uma categoria.", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            try {
                JSONObject payload = new JSONObject();


                payload.put("title", nome);
                payload.put("description", descricao);
                payload.put("icon_name", iconName);
                payload.put("category", categoria);
                payload.put("duration_days", duracao);
                payload.put("frequency", frequencia);
                payload.put("notifications_enabled", notificacao);

                JSONObject response = ApiClient.post(this, "/challenges/create-custom", payload.toString());

                runOnUiThread(() -> {
                    if (response != null && response.optInt("code") == 200) {
                        Toast.makeText(this, "Desafio criado com sucesso!", Toast.LENGTH_SHORT).show();
                        // Redireciona para a Home para ver o novo desafio
                        Intent intent = new Intent(CreateChallengeActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        String message = response != null ? response.optString("message", "Erro desconhecido.") : "Erro de rede/servidor.";
                        Toast.makeText(this, "Falha ao criar desafio: " + message, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Erro de processamento: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}