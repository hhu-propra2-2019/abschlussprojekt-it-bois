DROP TABLE IF EXISTS event;

CREATE TABLE event
(
    event_id      INT PRIMARY KEY AUTO_INCREMENT,
    group_id      VARCHAR(36) NOT NULL,
    user_id       VARCHAR(50),
    event_type    VARCHAR(32),
    event_payload VARCHAR(2500)
);

CREATE TABLE invite
(
    invite_id   INT PRIMARY KEY AUTO_INCREMENT,
    group_id    VARCHAR(36) NOT NULL,
    invite_link VARCHAR(36) NOT NULL
);
