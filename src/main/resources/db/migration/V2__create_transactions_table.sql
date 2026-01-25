CREATE TABLE transactions
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT         NOT NULL,
    type        VARCHAR(255)   NOT NULL,
    description TEXT,
    amount      NUMERIC(15, 2) NOT NULL,
    category    VARCHAR(255)   NOT NULL,
    date        DATE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);