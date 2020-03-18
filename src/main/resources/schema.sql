-- noinspection SqlNoDataSourceInspectionForFile

DROP TABLE IF EXISTS event;

CREATE TABLE event
(
    event_id INT PRIMARY KEY AUTO_INCREMENT,
    group_id INT NOT NULL,
    user_id VARCHAR(50),
    event_payload VARCHAR(2500),
    visibility BOOLEAN
);
