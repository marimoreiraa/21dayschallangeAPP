-- ============================================
-- TABELA: users
-- Cadastro dos usuarios
-- ============================================
CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  email varchar(64) NOT NULL,
  password varchar(64) NOT NULL,
  username varchar(64) NOT NULL,
  "lastLogin" TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL,
  "refreshToken" TEXT
);

-- ============================================
-- TABELA: password_reset
-- Registro de recuperação de senha
-- ============================================

CREATE TABLE IF NOT EXISTS password_reset (
  id SERIAL PRIMARY KEY,
  "userId" INT NOT NULL,
  used BOOLEAN NOT NULL,
  "requestedAt" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "expiresIn" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  code varchar(32) NOT NULL
);

-- ============================================
-- TABELA: suggested_challenges
-- Desafios sugeridos pelo app
-- ============================================
CREATE TABLE IF NOT EXISTS suggested_challenges (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(100),
    category VARCHAR(50),
    duration_days INT DEFAULT 21,
    frequency VARCHAR(30) DEFAULT 'Diária',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- TABELA: user_challenges
-- Desafios iniciados pelo usuário (sugeridos ou personalizados)
-- ============================================
CREATE TABLE IF NOT EXISTS user_challenges (
    id SERIAL PRIMARY KEY,
    "user_id" INT NOT NULL,
    suggested_id INT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50),
    duration_days INT DEFAULT 21,
    frequency VARCHAR(30) DEFAULT 'Diária',
    start_date DATE NOT NULL,
    end_date DATE,
    notifications_enabled BOOLEAN DEFAULT FALSE,
    status VARCHAR(10) DEFAULT 'active', -- ENUM substituído por VARCHAR para compatibilidade
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (suggested_id) REFERENCES suggested_challenges(id)
        ON DELETE SET NULL
);

-- Índices recomendados:
CREATE INDEX IF NOT EXISTS idx_userch_user ON user_challenges(user_id);
CREATE INDEX IF NOT EXISTS idx_userch_suggested ON user_challenges(suggested_id);

-- ============================================
-- TABELA: challenge_daily_check
-- Registro diário de presença (checkbox)
-- ============================================
CREATE TABLE IF NOT EXISTS challenge_daily_check (
    id SERIAL PRIMARY KEY,
    user_challenge_id INT NOT NULL,
    date DATE NOT NULL,
    completed BOOLEAN DEFAULT FALSE,

    FOREIGN KEY (user_challenge_id) REFERENCES user_challenges(id)
        ON DELETE CASCADE
);

-- Para evitar duplicação de registros no mesmo dia:
CREATE UNIQUE INDEX IF NOT EXISTS idx_daily_unique 
    ON challenge_daily_check (user_challenge_id, date);

-- ============================================
-- TABELA: challenge_progress_history
-- Histórico numérico de progresso (ex: litros, páginas etc)
-- ============================================
CREATE TABLE IF NOT EXISTS challenge_progress_history (
    id SERIAL PRIMARY KEY,
    user_challenge_id INT NOT NULL,
    date DATE NOT NULL,
    value DECIMAL(10, 2) NULL,
    note TEXT NULL,

    FOREIGN KEY (user_challenge_id) REFERENCES user_challenges(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_progress_userchallenge 
    ON challenge_progress_history(user_challenge_id);

-- ============================================
-- TABELA: notifications (opcional)
-- Horários e mensagens para enviar lembretes
-- ============================================
CREATE TABLE IF NOT EXISTS notifications (
    id SERIAL PRIMARY KEY,
    user_challenge_id INT NOT NULL,
    time TIME NOT NULL,
    message VARCHAR(200),

    FOREIGN KEY (user_challenge_id) REFERENCES user_challenges(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_notif_userchallenge 
    ON notifications(user_challenge_id);