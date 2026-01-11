package com.ifmg.a21dayschallangeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText editCurrentPassword, editNewPassword;
    private Button btnSavePassword, btnCancelar;
    private ImageView btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        editCurrentPassword = findViewById(R.id.editCurrentPassword);
        editNewPassword = findViewById(R.id.editNewPassword);
        btnSavePassword = findViewById(R.id.btnSavePassword);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnVoltar = findViewById(R.id.btnVoltar);

        btnVoltar.setOnClickListener(v -> finish());
        btnCancelar.setOnClickListener(v -> finish());

        btnSavePassword.setOnClickListener(v -> {
            validarETrocarSenha();
        });

        setupBottomNavigation();
    }

    private void validarETrocarSenha() {
        String atual = editCurrentPassword.getText().toString().trim();
        String nova = editNewPassword.getText().toString().trim();

        if (atual.isEmpty() || nova.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nova.length() < 6) {
            Toast.makeText(this, "A nova senha deve ter pelo menos 6 caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }

        enviarRequisicaoTroca(atual, nova);
    }

    private void enviarRequisicaoTroca(String senhaAtual, String senhaNova) {
        new Thread(() -> {
            try {
                JSONObject payload = new JSONObject();
                payload.put("currentPassword", senhaAtual);
                payload.put("newPassword", senhaNova);

                JSONObject response = ApiClient.post(this, "/auth/change-password", payload.toString());

                runOnUiThread(() -> {
                    if (response != null && response.optInt("code") == 200) {
                        Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String erro = response != null ? response.optString("message", "Erro ao mudar senha") : "Erro de conexão";
                        Toast.makeText(this, erro, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Erro interno na aplicação.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_profile);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_progress) {
                startActivity(new Intent(this, ProgressActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_challenge) {
                startActivity(new Intent(this, CreateChallengeActivity.class));
                finish();
                return true;
            }
            return true;
        });
    }
}