CREATE TABLE IF NOT EXISTS users (
    id                 SERIAL PRIMARY KEY,
    email              VARCHAR(100) NOT NULL UNIQUE,
    password           VARCHAR(255) NOT NULL,
    role               VARCHAR(20)  NOT NULL DEFAULT 'USER',
    verified           BOOLEAN      NOT NULL DEFAULT FALSE,
    verification_token VARCHAR(100)
);

ALTER TABLE users ADD COLUMN IF NOT EXISTS blocked BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE IF NOT EXISTS categories (
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

ALTER TABLE categories ADD COLUMN IF NOT EXISTS type VARCHAR(10) DEFAULT NULL;

CREATE TABLE IF NOT EXISTS transactions (
    id             SERIAL PRIMARY KEY,
    user_id        INT            NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id    INT            REFERENCES categories(id) ON DELETE SET NULL,
    amount         NUMERIC(15, 2) NOT NULL,
    type           VARCHAR(10)    NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    description    VARCHAR(255),
    operation_date TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS financial_goals (
    id             SERIAL PRIMARY KEY,
    user_id        INT            NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title          VARCHAR(100)   NOT NULL,
    target_amount  NUMERIC(15, 2) NOT NULL,
    current_amount NUMERIC(15, 2) NOT NULL DEFAULT 0,
    deadline       DATE
);

CREATE TABLE IF NOT EXISTS invites (
    id          SERIAL PRIMARY KEY,
    sender_id   INT         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    receiver_id INT         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'DECLINED'))
);
