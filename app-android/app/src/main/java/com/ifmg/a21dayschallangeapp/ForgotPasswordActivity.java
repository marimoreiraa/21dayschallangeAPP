package com.ifmg.a21dayschallangeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edtEmail = findViewById(R.id.inputEmail);
        btnEnviar = findViewById(R.id.btnEnviarRecuperacao);
        ImageView btnVoltar = findViewById(R.id.btnVoltar);

        AuthController authController = new AuthController();

        // Voltar para Login
        btnVoltar.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            finish();
        });

        // Enviar email de recuperação
        btnEnviar.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Digite seu email", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                boolean success = authController.requestPasswordReset(email);

                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(this,
                                "Se o email existir, enviamos instruções para recuperar a senha.",
                                Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(this,
                                "Erro ao enviar recuperação. Tente novamente.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }
}
