CREATE TABLE server
(
    id   BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    created TIMESTAMP,
    blocked BOOLEAN
)