CREATE TABLE server_status
(
    id BIGSERIAL PRIMARY KEY,
    created TIMESTAMP,
    creation_initiated BOOLEAN,
    server_id BIGSERIAL NOT NULL REFERENCES server(id),
    status TEXT,
    error TEXT
)