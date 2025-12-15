INSERT INTO users (email, password, username, `lastLogin`, `refreshToken`) VALUES
('alice@example.com', 'hash1', 'alice', '2025-01-10 12:30:00', NULL),
('bob@example.com', 'hash2', 'bob', '2025-01-11 09:20:00', NULL),
('carol@example.com', 'hash3', 'carol', '2025-01-08 17:50:00', NULL),
('dave@example.com', 'hash4', 'dave', '2025-01-05 14:10:00', NULL),
('erin@example.com', 'hash5', 'erin', '2025-01-04 11:30:00', NULL),
('frank@example.com', 'hash6', 'frank', '2025-01-09 18:25:00', NULL),
('gina@example.com', 'hash7', 'gina', '2025-01-07 08:55:00', NULL),
('henry@example.com', 'hash8', 'henry', '2025-01-06 16:05:00', NULL),
('irene@example.com', 'hash9', 'irene', '2025-01-03 19:40:00', NULL),
('john@example.com', 'hash10', 'john', '2025-01-02 13:15:00', NULL);

INSERT INTO password_reset (`userId`, used, `requestedAt`, `expiresIn`, code) VALUES
(1, 0, '2025-01-10 12:35:00', '2025-01-10 13:35:00', 'PRC001'),
(2, 1, '2025-01-11 09:15:00', '2025-01-11 10:15:00', 'PRC002'),
(3, 0, '2025-01-08 17:45:00', '2025-01-08 18:45:00', 'PRC003'),
(4, 0, '2025-01-05 14:15:00', '2025-01-05 15:15:00', 'PRC004'),
(5, 1, '2025-01-04 11:20:00', '2025-01-04 12:20:00', 'PRC005'),
(6, 0, '2025-01-09 18:15:00', '2025-01-09 19:15:00', 'PRC006'),
(7, 0, '2025-01-07 09:05:00', '2025-01-07 10:05:00', 'PRC007'),
(8, 1, '2025-01-06 16:00:00', '2025-01-06 17:00:00', 'PRC008'),
(9, 0, '2025-01-03 19:35:00', '2025-01-03 20:35:00', 'PRC009'),
(10,0, '2025-01-02 13:05:00', '2025-01-02 14:05:00', 'PRC010');

INSERT INTO suggested_challenges 
(title, description, icon, category, duration_days, frequency, is_active) VALUES
('Beber 2L de Água', 'Hidratação diária', 'water.png', 'Saúde', 21, 'Diária', TRUE),
('Ler 10 Páginas', 'Leitura para conhecimento', 'book.png', 'Produtividade', 21, 'Diária', TRUE),
('Meditar 5 Minutos', 'Relaxamento curto', 'meditate.png', 'Bem-estar', 21, 'Diária', TRUE),
('Caminhar 20 Min', 'Atividade leve', 'walk.png', 'Saúde', 21, 'Diária', TRUE),
('Alongamento', 'Flexibilidade diária', 'stretch.png', 'Fitness', 21, 'Diária', TRUE),
('Escrever 5 Min', 'Journaling rápido', 'write.png', 'Mental', 21, 'Diária', TRUE),
('Evitar Açúcar', 'Detox de açúcar', 'no-sugar.png', 'Nutrição', 21, 'Diária', TRUE),
('Acordar Cedo', 'Rotina matinal disciplinada', 'sun.png', 'Produtividade', 21, 'Diária', TRUE),
('Aprender Algo Novo', 'Aprendizado diário', 'brain.png', 'Educação', 21, 'Diária', TRUE),
('Organizar a Casa', 'Organização leve diária', 'home.png', 'Casa', 21, 'Diária', TRUE);

INSERT INTO user_challenges
(user_id, suggested_id, title, description, category, duration_days, frequency, start_date, end_date, notifications_enabled, status)
VALUES
(1, 1, 'Beber 2L de Água', 'Hidratação diária', 'Saúde', 21, 'Diária', '2025-01-01', '2025-01-22', TRUE, 'active'),
(2, 2, 'Ler 10 Páginas', 'Leitura diária', 'Produtividade', 21, 'Diária', '2025-01-02', '2025-01-23', TRUE, 'active'),
(3, 3, 'Meditar 5 Minutos', 'Relaxamento mental', 'Bem-estar', 21, 'Diária', '2025-01-03', '2025-01-24', TRUE, 'completed'),
(4, NULL, 'Correr 3km', 'Treino de resistência', 'Fitness', 21, 'Diária', '2025-01-04', '2025-01-25', FALSE, 'active'),
(5, 4, 'Caminhar 20 Min', 'Atividade física leve', 'Saúde', 21, 'Diária', '2025-01-05', '2025-01-26', TRUE, 'active'),
(6, NULL, 'Estudar 30 Min', 'Rotina de estudos', 'Educação', 21, 'Diária', '2025-01-06', '2025-01-27', TRUE, 'abandoned'),
(7, 7, 'Evitar Açúcar', 'Detox de açúcar', 'Nutrição', 21, 'Diária', '2025-01-07', '2025-01-28', FALSE, 'active'),
(8, NULL, 'Lavar Louça Toda Noite', 'Organizar a rotina', 'Casa', 21, 'Diária', '2025-01-08', '2025-01-29', TRUE, 'active'),
(9, 5, 'Alongamento', 'Flexibilidade diária', 'Fitness', 21, 'Diária', '2025-01-09', '2025-01-30', FALSE, 'completed'),
(10, 8, 'Acordar Cedo', 'Rotina disciplinada', 'Produtividade', 21, 'Diária', '2025-01-10', '2025-01-31', TRUE, 'active');

INSERT INTO challenge_daily_check (user_challenge_id, date, completed) VALUES
(1, '2025-01-01', TRUE),
(2, '2025-01-02', TRUE),
(3, '2025-01-03', TRUE),
(4, '2025-01-04', FALSE),
(5, '2025-01-05', TRUE),
(6, '2025-01-06', FALSE),
(7, '2025-01-07', TRUE),
(8, '2025-01-08', TRUE),
(9, '2025-01-09', TRUE),
(10, '2025-01-10', TRUE);

INSERT INTO challenge_progress_history (user_challenge_id, date, value, note) VALUES
(1, '2025-01-01', 2.0, 'Meta atingida'),
(2, '2025-01-02', 10, 'Boa leitura'),
(3, '2025-01-03', 1.0, 'Meditação leve'),
(4, '2025-01-04', 2.8, 'Ritmo ok'),
(5, '2025-01-05', 20, 'Caminhada tranquila'),
(6, '2025-01-06', 30, 'Sessão curta'),
(7, '2025-01-07', 0, 'Dieta mantida'),
(8, '2025-01-08', 1, 'Rotina feita'),
(9, '2025-01-09', 15, 'Alongamento completo'),
(10,'2025-01-10', 1, 'Acordou na hora!');

INSERT INTO notifications (user_challenge_id, time, message) VALUES
(1, '08:00:00', 'Hidratação do dia!'),
(2, '19:30:00', 'Hora de ler!'),
(3, '07:00:00', 'Momento de meditar.'),
(4, '06:30:00', 'Hora de correr!'),
(5, '08:10:00', 'Hora da caminhada!'),
(6, '20:00:00', 'Hora de estudar.'),
(7, '12:00:00', 'Evite açúcar hoje!'),
(8, '21:00:00', 'Lembre de lavar a louça.'),
(9, '09:00:00', 'Momento de alongar.'),
(10,'06:00:00', 'Acordar cedo! Bora começar o dia.');