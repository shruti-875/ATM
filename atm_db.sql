CREATE DATABASE atm_db;

USE atm_db;

CREATE TABLE users (
    user_id VARCHAR(50) PRIMARY KEY,
    pin VARCHAR(50) NOT NULL,
    balance DOUBLE DEFAULT 0.0
);

CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50),
    transaction_type VARCHAR(50),
    amount DOUBLE,
    recipient_user_id VARCHAR(50),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

INSERT INTO users (user_id, pin, balance) VALUES ('admin', '1234', 1000.0);
