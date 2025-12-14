# 🏋️‍♀️ 21DaysChallenge App

Aplicação composta por um **backend em Node.js** e um **aplicativo Android**, desenvolvidos para gerenciar desafios pessoais de 21 dias com login, registro de usuários e acompanhamento de progresso.

---

## 📂 Estrutura do Repositório
```
/21DaysChallengeAPP
│
├── backend/ → API em Node.js
│ ├── src/
│ ├── env/
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
- **Node JS**
- **Android Studio (versão 2022.3+ ou superior)**
- **Emulador Android** ou dispositivo físico com Android 9+

---

## 🚀 Executando o Backend (Node)

* Acesse a pasta do backend:
```
cd backend
```
* Compile e execute:

```
npm run localhost
```
* O servidor iniciará em:

```
http://localhost:8080
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


