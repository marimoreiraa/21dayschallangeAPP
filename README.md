# 🏋️‍♀️ 21DaysChallenge App

Aplicação composta por um **backend Spring Boot** e um **aplicativo Android**, desenvolvidos para gerenciar desafios pessoais de 21 dias com login, registro de usuários e acompanhamento de progresso.

---

## 📂 Estrutura do Repositório
```
/21DaysChallengeAPP
│
├── backend21daysapp/ → API REST em Spring Boot
│ ├── src/
│ ├── pom.xml
│ └── ...
│
└── app-android/ → Aplicativo Android (Java)
├── app/src/
├── build.gradle
└── ...

```
---

## ⚙️ Requisitos

- **Java 21+**
- **Maven 3.8+**
- **Android Studio (versão 2022.3+ ou superior)**
- **Emulador Android** ou dispositivo físico com Android 9+

---

## 🚀 Executando o Backend (Spring Boot)

* Acesse a pasta do backend:
```
cd backend21daysapp
```
* Compile e execute:

```
mvn spring-boot:run
```
* O servidor iniciará em:

```
http://localhost:8080
```

## 🧑‍💻 Testando o Backend com cURL
* Registro de Usuário
```
curl -X POST http://localhost:8080/api/auth/register \
-H "Content-Type: application/json" \
-d '{
  "email": "teste@exemplo.com",
  "password": "123456"
}'
```
* Login de Usuário
```
curl -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{
  "email": "teste@exemplo.com",
  "password": "123456"
}'
```

 ## 📱 Executando o Aplicativo Android

1. Abra a pasta app-android no Android Studio.
2. Sincronize o projeto (File > Sync Project with Gradle Files).
3. Certifique-se de que o backend já está rodando.
4. Clique em ▶️ Run App para iniciar o aplicativo:
   * No emulador Android (preferencialmente leve, como Nox ou Android Emulator com x86_64)
   * Ou em um dispositivo físico via USB.
5. O app abrirá na tela de Login, permitindo:
   * Login com o usuário cadastrado via backend.
   * Registro de novos usuários diretamente pelo app.

