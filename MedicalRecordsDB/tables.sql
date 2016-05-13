SET foreign_key_checks = 0 ;

DROP TABLE IF EXISTS staff;
DROP TABLE IF EXISTS specialties;
DROP TABLE IF EXISTS records;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS entry;

CREATE TABLE patients (
    p_username VARCHAR(15),
    first_name VARCHAR(20),
    last_name VARCHAR(20),
    address VARCHAR(255),
    PRIMARY KEY (p_username),
    FOREIGN KEY (p_username)
        REFERENCES users (username)
);

CREATE TABLE users (
    username VARCHAR(15),
    password VARCHAR(255),
    saltValue VARCHAR (64),
    PRIMARY KEY (username)
);

CREATE TABLE doctors (
    d_username VARCHAR(15),
    specialty VARCHAR(30),
    emergency BOOL,
    publicKey VARCHAR(216),
    PRIMARY KEY (d_username),
    FOREIGN KEY (d_username)
        REFERENCES users (username),
	FOREIGN KEY (specialty)
        REFERENCES specialties (spec_name)
);

CREATE TABLE specialties (
	spec_name VARCHAR(30),
    PRIMARY KEY (spec_name)
);

CREATE TABLE staff (
    s_username VARCHAR(15),
    FOREIGN KEY (s_username)
        REFERENCES users (username)
);

CREATE TABLE records (
    r_id INTEGER NOT NULL AUTO_INCREMENT,
    patient_name VARCHAR(15),
    creation_date TIMESTAMP,
    r_hash VARCHAR(64),
    PRIMARY KEY (r_id),
    FOREIGN KEY (patient_name)
        REFERENCES patients (p_username)
);

CREATE TABLE entry (
    entry_id INTEGER NOT NULL AUTO_INCREMENT,
    record_id INTEGER,
    entry_Date TIMESTAMP,
	description VARCHAR(255),
    doc_name VARCHAR(15),
    specialty VARCHAR(15),
    signature VARCHAR(250),
    PRIMARY KEY (entry_id),
    FOREIGN KEY (record_id)
        REFERENCES records (r_id),
    FOREIGN KEY (doc_name)
        REFERENCES doctors (d_username),
	FOREIGN KEY (specialty)
        REFERENCES specialties (spec_name)
);

