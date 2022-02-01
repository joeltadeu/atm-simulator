CREATE TABLE account
(
    id             INTEGER AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL,
    pin            VARCHAR(10) NOT NULL,
    balance        DECIMAL     NOT NULL,
    overdraft      DECIMAL     NOT NULL
);