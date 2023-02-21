DROP TABLE IF EXISTS endpointhit;

CREATE TABLE IF NOT EXISTS endpointhit
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    app          VARCHAR(255) NOT NULL,
    uri          VARCHAR(512) NOT NULL,
    ip           VARCHAR(512) NOT NULL,
    timestamp    TIMESTAMP WITHOUT TIME ZONE
    );