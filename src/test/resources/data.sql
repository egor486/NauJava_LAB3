-- Инициализация справочника статусов задач для тестовой базы FOR_TEST
-- PostgreSQL синтаксис с поддержкой повторных запусков

INSERT INTO task_status (id, name) VALUES (1, 'НОВАЯ') ON CONFLICT (id) DO NOTHING;
INSERT INTO task_status (id, name) VALUES (2, 'В ПРОГРЕССЕ') ON CONFLICT (id) DO NOTHING;
INSERT INTO task_status (id, name) VALUES (3, 'ЗАВЕРШЕНА') ON CONFLICT (id) DO NOTHING;

