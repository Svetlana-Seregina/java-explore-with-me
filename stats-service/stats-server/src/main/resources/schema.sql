DROP TABLE IF EXISTS endpointhit, application;

CREATE TABLE IF NOT EXISTS application
(
    id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name   VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS endpointhit
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    app_id       BIGINT NOT NULL,
    uri          VARCHAR(512) NOT NULL,
    ip           VARCHAR(512) NOT NULL,
    timestamp    TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_app_id FOREIGN KEY (app_id) REFERENCES application(id)
);


