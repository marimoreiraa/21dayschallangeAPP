-- ============================================
-- Base de Dados
-- ============================================
CREATE DATABASE IF NOT EXISTS `21daysapp` CHARACTER SET utf8 COLLATE utf8_general_ci;

USE `21daysapp`;

-- ============================================
-- TABELA: users
-- Cadastro dos usuarios
-- ============================================
CREATE TABLE IF NOT EXISTS users (
  `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `email` varchar(64) NOT NULL,
  `password` varchar(64) NOT NULL,
  `username` varchar(64) NOT NULL,
  `lastLogin` datetime DEFAULT NULL,
  `refreshToken` tinytext
);

-- ============================================
-- TABELA: password_reset
-- Registro de recuperação de senha
-- ============================================

CREATE TABLE IF NOT EXISTS password_reset (
  `id` int(11) AUTO_INCREMENT PRIMARY KEY,
  `userId` int(11) NOT NULL,
  `used` tinyint(1) NOT NULL,
  `requestedAt` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `expiresIn` timestamp NOT NULL DEFAULT current_timestamp(),
  `code` varchar(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- ============================================
-- TABELA: suggested_challenges
-- Desafios sugeridos pelo app
-- ============================================
CREATE TABLE IF NOT EXISTS suggested_challenges (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(100),
    category VARCHAR(50),
    duration_days INT DEFAULT 21,
    frequency VARCHAR(30) DEFAULT 'Diária',
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- TABELA: user_challenges
-- Desafios iniciados pelo usuário (sugeridos ou personalizados)
-- ============================================
CREATE TABLE IF NOT EXISTS user_challenges (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    suggested_id INT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50),
    duration_days INT DEFAULT 21,
    frequency VARCHAR(30) DEFAULT 'Diária',
    start_date DATE NOT NULL,
    end_date DATE,
    notifications_enabled BOOLEAN DEFAULT FALSE,
    status ENUM('active', 'completed', 'abandoned') DEFAULT 'active',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

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
    id INT AUTO_INCREMENT PRIMARY KEY,
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
    id INT AUTO_INCREMENT PRIMARY KEY,
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
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_challenge_id INT NOT NULL,
    time TIME NOT NULL,
    message VARCHAR(200),

    FOREIGN KEY (user_challenge_id) REFERENCES user_challenges(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_notif_userchallenge 
    ON notifications(user_challenge_id);