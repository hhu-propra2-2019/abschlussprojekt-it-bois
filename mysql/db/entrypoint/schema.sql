CREATE TABLE event
(
    event_id INT PRIMARY KEY AUTO_INCREMENT,
    group_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(50),
    event_type VARCHAR(36),
    event_payload JSON
);
