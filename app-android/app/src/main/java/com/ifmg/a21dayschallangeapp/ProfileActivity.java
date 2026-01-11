package com.ifmg.a21dayschallangeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgAvatar;

    private TextView txtNome, txtEmail, txtStatusHoje;
    private Button btnMudarSenha, btnExcluirConta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgAvatar = findViewById(R.id.imgAvatarDinamico);
        txtNome = findViewById(R.id.txtUserName);
        txtEmail = findViewById(R.id.txtUserEmail);
        txtStatusHoje = findViewById(R.id.txtStatusHoje);
        btnMudarSenha = findViewById(R.id.btnChangePassword);
        btnExcluirConta = findViewById(R.id.btnDeleteAccount);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            SessionManager.clearToken(ProfileActivity.this);
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            Toast.makeText(this, "Sessão encerrada", Toast.LENGTH_SHORT).show();
            finish();
        });

        setupBottomNavigation();

        loadUserProfileData();

        btnExcluirConta.setOnClickListener(v -> deletarConta());

        btnMudarSenha.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });


    }

    private void loadUserProfileData() {
        new Thread(() -> {
            JSONObject userResponse = ApiClient.get(this, "/auth/me");

            JSONObject challengesResponse = ApiClient.get(this, "/challenges/user");

            runOnUiThread(() -> {
                try {
                    if (userResponse != null && userResponse.optInt("code") == 200) {
                        JSONObject userData = userResponse.getJSONObject("user");
                        txtNome.setText(userData.optString("name", "Usuário"));
                        txtEmail.setText("E-mail: " + userData.optString("email", ""));
                    }

                    if (challengesResponse != null && challengesResponse.optInt("code") == 200) {
                        JSONArray challenges = challengesResponse.optJSONArray("userChallenges");
                        atualizarHumorAvatar(challenges);
                    } else {
                        txtStatusHoje.setText("Erro ao carregar desafios.");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Erro ao processar dados do perfil", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void atualizarHumorAvatar(JSONArray challenges) {
        if (challenges == null || challenges.length() == 0) {
            imgAvatar.setImageResource(R.drawable.neutral);
            txtStatusHoje.setText("Você não tem desafios ativos.");
            return;
        }

        int totalDesafios = challenges.length();
        int concluidosHoje = 0;

        for (int i = 0; i < totalDesafios; i++) {
            JSONObject obj = challenges.optJSONObject(i);
            if (obj.optBoolean("is_checked_today", false)) {
                concluidosHoje++;
            }
        }

        // Lógica de Gamificação (Avatar Dinâmico)
        if (concluidosHoje == totalDesafios) {
            imgAvatar.setImageResource(R.drawable.happy);
            txtStatusHoje.setText("Incrível! Todos os desafios de hoje concluídos!");
        } else if (concluidosHoje > 0) {
            imgAvatar.setImageResource(R.drawable.neutral);
            txtStatusHoje.setText("Bom trabalho! Você fez " + concluidosHoje + " de " + totalDesafios + ".");
        } else {
            imgAvatar.setImageResource(R.drawable.sad);
            txtStatusHoje.setText("Você ainda não completou nenhum desafio hoje.");
        }
    }

    private void deletarConta() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Excluir Conta")
                .setMessage("Esta ação é irreversível. Você tem certeza que deseja deletar sua conta e todos os seus progressos?")
                .setPositiveButton("Sim, Excluir", (dialog, which) -> {
                    executarExclusaoNoServidor();
                })
                .setNegativeButton("Cancelar", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void executarExclusaoNoServidor() {
        new Thread(() -> {
            JSONObject response = ApiClient.delete(this, "/auth/delete-account");
            runOnUiThread(() -> {
                if (response != null && response.optInt("code") == 200) {
                    Toast.makeText(this, "Conta excluída.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Erro ao excluir conta.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_profile);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_progress) {
                startActivity(new Intent(this, ProgressActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return true;
        });
    }

}