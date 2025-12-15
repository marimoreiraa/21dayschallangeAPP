# 🏋️‍♀️ Aplicativo Desafio de 21 Dias

Aplicação composta por um **backend em Node.js** e um **aplicativo Android**, desenvolvidos para gerenciar desafios pessoais de 21 dias com login, registro de usuários e acompanhamento de progresso.

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

## 🚀 Primeiros Passos (Desenvolvimento Local)

Esta seção detalha como configurar e executar o backend e o aplicativo Android localmente.

### 1. Pré-requisitos
*   Java 21+
*   Node JS
*   Android Studio (versão 2022.3+ ou superior)
*   Emulador Android ou dispositivo físico com Android 9+
*   Docker e Docker Compose

### 2. Configuração e Execução do Backend

*   **Instalação e Rede Docker:**
    *   Certifique-se de que o Docker e o Docker Compose estejam instalados em seu sistema.
    *   Crie a rede Docker `proxy`: `sudo docker network create proxy` (se ainda não tiver sido criada).
*   **Configuração do Host do Banco de Dados:**
    *   O `DB_HOST` no arquivo `backend/env/localhost.env` deve ser configurado para `localhost`.
*   **Iniciar Serviços do Backend:**
    *   Navegue até o diretório `21dayschallangeAPP/backend/`: `cd 21dayschallangeAPP/backend`
    *   Execute `sudo docker compose up -d` para iniciar o banco de dados MySQL e o backend Node.js.
*   **Inicializar Banco de Dados:**
    *   Execute `npm run database-setup` a partir do diretório `21dayschallangeAPP/backend/`. Isso criará as tabelas e as preencherá com dados iniciais.
*   **Acessar o Backend:**
    *   O servidor estará em execução em `http://localhost:8080`.

### 3. Configuração e Execução do Aplicativo Android

1.  Abra a pasta `21dayschallangeAPP/app-android` no Android Studio.
2.  Sincronize o projeto (File > Sync Project with Gradle Files).
3.  Certifique-se de que os serviços do backend estejam em execução (conforme descrito acima).
4.  Clique em ▶️ Run App para iniciar o aplicativo:
    *   Em um emulador Android (preferencialmente leve, como Nox ou Android Emulator com x86_64)
    *   Ou em um dispositivo físico via USB.
5.  O aplicativo abrirá na tela de Login, permitindo:
    *   Login com `alice@example.com` e senha `hash1` (após a configuração do banco de dados).
    *   Registro de novos usuários diretamente pelo aplicativo.

---

## ✨ Funcionalidades Implementadas

Este projeto inclui as seguintes funcionalidades:

### 1. Gerenciamento de Desafios do Usuário (CRUD)

*   **Atualizar Desafio do Usuário:** `PUT /api/challenges/user/:id`
    *   Atualiza um desafio de usuário existente com os campos fornecidos. Requer autenticação.
*   **Excluir Desafio do Usuário:** `DELETE /api/challenges/user/:id`
    *   Exclui um desafio de usuário. Requer autenticação.

### 2. Gerenciamento de Tarefas Diárias

*   **Marcar Desafio Diário como Concluído:** `POST /api/challenges/user/:id/check`
    *   Marca um desafio diário específico como concluído para o dia atual. Se uma entrada existir, ela é atualizada; caso contrário, uma nova é criada. Requer autenticação.
*   **Obter Status Diário do Usuário:** `GET /api/challenges/user/daily-status`
    *   Recupera todos os desafios ativos para o usuário autenticado e seu status de conclusão para o dia atual. Requer autenticação.

### 3. Acompanhamento de Progresso

*   **Registrar Progresso do Desafio:** `POST /api/challenges/user/:id/progress`
    *   Registra o progresso numérico para um desafio do usuário. Requer um `value` no corpo da requisição. `date` (formato `YYYY-MM-DD`, padrão para a data atual) e `note` (string) são opcionais. Requer autenticação.
*   **Obter Histórico de Progresso do Desafio:** `GET /api/challenges/user/:id/progress-history`
    *   Recupera todos os registros de histórico de progresso para um desafio do usuário, ordenados por data. Útil para gráficos de progresso. Requer autenticação.

### 4. Estatísticas do Usuário

*   **Obter Estatísticas do Usuário:** `GET /api/challenges/user/statistics`
    *   Recupera estatísticas agregadas para o usuário autenticado, incluindo o total de desafios ativos, o total de desafios concluídos e o total de verificações diárias concluídas. Requer autenticação.