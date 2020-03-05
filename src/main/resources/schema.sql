-- noinspection SqlNoDataSourceInspectionForFile

DROP TABLE IF EXISTS teilnehmer;
CREATE TABLE teilnehmer (
    teilnehmer_id INT PRIMARY KEY AUTO_INCREMENT,
    vorname VARCHAR(50) NOT NULL,
    nachname VARCHAR(50) NOT NULL ,
    email VARCHAR(255) NOT NULL
);

DROP TABLE IF EXISTS gruppe;
CREATE TABLE gruppe
(
    gruppe_id INTEGER PRIMARY KEY auto_increment,
    titel TEXT NOT NULL,
    beschreibung TEXT NOT NULL
);

DROP TABLE IF EXISTS teilnahme;
CREATE TABLE teilnahme
(
    id INTEGER PRIMARY KEY auto_increment,
    teilnehmer_dto INTEGER REFERENCES teilnehmer(teilnehmer_id),
    gruppe_dto INTEGER REFERENCES gruppe(gruppe_id)
);
