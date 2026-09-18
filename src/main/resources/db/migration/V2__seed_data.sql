INSERT INTO offices (id, address)
VALUES ('office-001', 'г. Москва, ул. Тверская, д. 1')
    ON CONFLICT (id) DO NOTHING;

INSERT INTO administrators (
    id, username, password_hash, full_name, email,
    email_verified, is_active, is_hr, office_id
)
VALUES (
           'admin-001',
           'admin',
           '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
           'Администратор Системы',
           'admin@internetcafe.com',
           TRUE, TRUE, TRUE,
           'office-001'
       ) ON CONFLICT (id) DO NOTHING;

INSERT INTO clients (
    id, username, password_hash, full_name, email,
    email_verified, balance
)
VALUES (
           'client-001',
           'client',
           '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
           'Клиент Иванов',
           'client@internetcafe.com',
           TRUE, 100.00
       ) ON CONFLICT (id) DO NOTHING;

INSERT INTO device_types (id, code, display_name, description, is_active)
VALUES
    ('dtype-001', 'PS5', 'PlayStation 5', 'Sony PlayStation 5', TRUE),
    ('dtype-002', 'PS4', 'PlayStation 4', 'Sony PlayStation 4', TRUE),
    ('dtype-003', 'XBOX_SERIES_X', 'Xbox Series X', 'Microsoft Xbox Series X', TRUE),
    ('dtype-004', 'XBOX_SERIES_S', 'Xbox Series S', 'Microsoft Xbox Series S', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO devices (
    id, office_id, type_id, condition,
    day_rate, night_rate
)
VALUES (
           'device-001',
           'office-001',
           'dtype-001',
           'WORKING',
           500.00, 300.00
       ) ON CONFLICT (id) DO NOTHING;


INSERT INTO games (id, name, description)
VALUES ('game-001', 'The Last of Us Part II', 'Экшен-приключение от Naughty Dog')
    ON CONFLICT (id) DO NOTHING;


INSERT INTO device_games (device_id, game_id)
VALUES ('device-001', 'game-001')
    ON CONFLICT (device_id, game_id) DO NOTHING;