package com.ifmg.a21dayschallangeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtNome, edtEmail, edtSenha;
    private Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // CAMPOS DO FORM
        edtNome = findViewById(R.id.inputNome);
        edtEmail = findViewById(R.id.inputEmail);
        edtSenha = findViewById(R.id.inputSenha);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        // COMPONENTES DE NAVEGAÇÃO
        ImageView btnVoltar = findViewById(R.id.btnVoltar);
        TextView textLoginAqui = findViewById(R.id.textLoginAqui);

        // ----- BOTÃO VOLTAR -----
        btnVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // ----- TEXTO "Faça login aqui" -----
        textLoginAqui.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // ----- REGISTRO -----
        AuthController authController = new AuthController();

        btnRegistrar.setOnClickListener(v -> {
            String nome = edtNome.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String senha = edtSenha.getText().toString().trim();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                boolean success = authController.register(nome, email, senha);

                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                        finish(); // volta para Login
                    } else {
                        Toast.makeText(this, "Erro ao cadastrar (email já usado?)", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }
}
