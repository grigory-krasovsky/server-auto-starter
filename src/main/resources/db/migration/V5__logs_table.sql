CREATE TABLE logs
(
    id   BIGSERIAL PRIMARY KEY,
    createdAt TIMESTAMP,
    level TEXT,
    message TEXT,
    stack_trace TEXT,
    thread TEXT
)