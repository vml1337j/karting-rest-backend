CREATE TABLE event
(
    id               BIGINT NOT NULL,
    title            VARCHAR(255),
    category         VARCHAR(255),
    date             date,
    number_of_racers INT,
    url              VARCHAR(255),
    is_filled        BOOLEAN,
    CONSTRAINT pk_event PRIMARY KEY (id)
);

ALTER TABLE event
    ADD CONSTRAINT uc_event_id UNIQUE (id);

ALTER TABLE event
    ADD CONSTRAINT uc_event_title UNIQUE (title);

ALTER TABLE event
    ADD CONSTRAINT uc_event_url UNIQUE (url);

CREATE TABLE racer
(
    id          BIGINT NOT NULL,
    sws_id      VARCHAR(255),
    firstname   VARCHAR(255),
    lastname    VARCHAR(255),
    age         INT,
    gender      VARCHAR(255),
    country     VARCHAR(255),
    profile_url VARCHAR(255),
    is_filled   BOOLEAN,
    CONSTRAINT pk_racer PRIMARY KEY (id)
);

ALTER TABLE racer
    ADD CONSTRAINT uc_racer_id UNIQUE (id);

ALTER TABLE racer
    ADD CONSTRAINT uc_racer_profile_url UNIQUE (profile_url);

ALTER TABLE racer
    ADD CONSTRAINT uc_racer_sws_id UNIQUE (sws_id);

CREATE TABLE result
(
    position INT,
    racer_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    CONSTRAINT pk_result PRIMARY KEY (racer_id, event_id)
);

ALTER TABLE result
    ADD CONSTRAINT FK_RESULT_ON_EVENT FOREIGN KEY (event_id) REFERENCES event (id);

ALTER TABLE result
    ADD CONSTRAINT FK_RESULT_ON_RACER FOREIGN KEY (racer_id) REFERENCES racer (id);
