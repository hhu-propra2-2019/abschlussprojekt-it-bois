-- noinspection SqlNoDataSourceInspectionForFile

DROP TABLE IF EXISTS event;

CREATE TABLE event
(
    event_id INT PRIMARY KEY AUTO_INCREMENT,
    group_id INT NOT NULL,
    user_id VARCHAR(50),
    event_payload VARCHAR(255),
    visibility BOOLEAN
);

DROP TABLE IF EXISTS invite;

CREATE TABLE invite
(
    group_id INT FOREIGN KEY REFERENCES event(group_id),
    invite_link varchar(255)
)
