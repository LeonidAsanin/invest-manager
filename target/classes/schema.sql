CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS purchase (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    tag VARCHAR(255),
    amount INT,
    price DOUBLE,
    commission DOUBLE,
    date_time DATETIME,
    FOREIGN KEY (owner_id) REFERENCES user(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sale (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    tag VARCHAR(255),
    amount INT,
    price DOUBLE,
    commission DOUBLE,
    date_time DATETIME,
    absolute_profit DOUBLE,
    relative_profit DOUBLE,
    FOREIGN KEY (seller_id) REFERENCES user(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS product (
    owner_id BIGINT,
    name VARCHAR(255),
    current_price DOUBLE,
    PRIMARY KEY (owner_id, name),
    FOREIGN KEY (owner_id) REFERENCES user(id) ON DELETE CASCADE
);