INSERT INTO categories (name) VALUES
    ('Зарплата'),
    ('Фриланс'),
    ('Продукты'),
    ('Транспорт'),
    ('Развлечения'),
    ('Коммунальные услуги'),
    ('Здоровье'),
    ('Одежда'),
    ('Образование'),
    ('Прочее')
ON CONFLICT (name) DO NOTHING;

UPDATE categories SET type = 'INCOME'  WHERE name IN ('Зарплата', 'Фриланс');
UPDATE categories SET type = 'EXPENSE' WHERE name IN ('Продукты', 'Транспорт', 'Развлечения', 'Коммунальные услуги', 'Здоровье', 'Одежда', 'Образование');
