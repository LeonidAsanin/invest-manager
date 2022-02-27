CREATE TABLE IF NOT EXISTS account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS purchase (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    amount INT,
    price DOUBLE,
    commission DOUBLE,
    date DATE,
    FOREIGN KEY (owner_id) REFERENCES account(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sale (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    amount INT,
    price DOUBLE,
    commission DOUBLE,
    date DATE,
    absolute_benefit DOUBLE,
    relative_benefit DOUBLE,
    FOREIGN KEY (seller_id) REFERENCES account(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS product (
    owner_id BIGINT,
    name VARCHAR(255),
    current_price DOUBLE,
    PRIMARY KEY (owner_id, name),
    FOREIGN KEY (owner_id) REFERENCES account(id) ON DELETE CASCADE
);