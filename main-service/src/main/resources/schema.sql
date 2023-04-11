DROP TABLE IF EXISTS users, categories, locations, events, compilation, list_of_entities, participation_request, comment CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS USER_EMAIL_UINDEX on USERS (email);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(512) NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS CATEGORIES_NAME_UINDEX on CATEGORIES (name);

CREATE TABLE IF NOT EXISTS locations
(
    id  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    lat DOUBLE PRECISION,
    lon DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    annotation         VARCHAR(2000) NOT NULL,
    category_id        BIGINT,
    created_on         TIMESTAMP WITHOUT TIME ZONE,
    description        VARCHAR(7000) NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE,
    user_id            BIGINT,
    location_id        BIGINT,
    paid               BOOLEAN,
    participant_limit  BIGINT,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN,
    state              VARCHAR(50),
    title              VARCHAR(120),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_category_id FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_location_id FOREIGN KEY (location_id) REFERENCES locations (id)
);

CREATE TABLE IF NOT EXISTS compilation
(
    id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id BIGINT,
    pinned   BOOLEAN,
    title    VARCHAR(512),
    CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES events (id)
);

CREATE TABLE IF NOT EXISTS list_of_entities
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    compilation_id  BIGINT,
    events_id       BIGINT,
    CONSTRAINT fk_event_id FOREIGN KEY (events_id) REFERENCES events (id),
    CONSTRAINT fk_compilation_id FOREIGN KEY (compilation_id) REFERENCES compilation (id)
);


CREATE TABLE IF NOT EXISTS participation_request
(
    id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    created  TIMESTAMP WITHOUT TIME ZONE,
    event_id BIGINT,
    user_id  BIGINT,
    status   VARCHAR(50),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES events (id)
);

CREATE TABLE IF NOT EXISTS comment
(
    id       		BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id        BIGINT,
    user_id         BIGINT,
    text   			VARCHAR(7000),
	created_date    TIMESTAMP WITHOUT TIME ZONE,
	updated_date    TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES events (id)
);