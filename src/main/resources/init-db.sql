CREATE TABLE IF NOT EXISTS "user"
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    age        INTEGER CHECK (age > 0 AND age < 100),
    created_at DATE CHECK (created_at <= CURRENT_DATE)
);
