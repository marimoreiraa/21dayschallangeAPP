package com.ifmg.a21dayschallangeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtEmail, edtResposta, edtNovaSenha;
    private Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Inicialização
        edtEmail = findViewById(R.id.inputEmail);
        edtResposta = findViewById(R.id.inputResposta);
        edtNovaSenha = findViewById(R.id.inputNovaSenha);
        btnEnviar = findViewById(R.id.btnEnviarRecuperacao);
        ImageView btnVoltar = findViewById(R.id.btnVoltar);

        btnVoltar.setOnClickListener(v -> finish());

        btnEnviar.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String resposta = edtResposta.getText().toString().trim();
            String novaSenha = edtNovaSenha.getText().toString().trim();

            if (email.isEmpty() || resposta.isEmpty() || novaSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            processarRecuperacao(email, resposta, novaSenha);
        });
    }

    private void processarRecuperacao(String email, String resposta, String novaSenha) {
        AuthController authController = new AuthController();

        new Thread(() -> {
            boolean success = authController.recoverPassword(this, email, resposta, novaSenha);

            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(this, "Senha redefinida com sucesso!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this, "Dados incorretos ou erro de conexão.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}