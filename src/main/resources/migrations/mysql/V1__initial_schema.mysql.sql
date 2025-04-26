CREATE TABLE Bank (
                      userId BINARY(16) PRIMARY KEY,
                      balance BIGINT DEFAULT 0 CHECK (balance >= 0)
);

CREATE TABLE DailyRewards (
                              userId BINARY(16) PRIMARY KEY,
                              amount INT,
                              last_claim_date DATETIME
);

CREATE TABLE Player (
                        playerId BINARY(16) PRIMARY KEY,
                        name VARCHAR(255),
                        title VARCHAR(255),
                        onHandBalance BINARY(16)
);

CREATE TABLE TransactionFee (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                taxa INT
);

CREATE TABLE Transactions (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              senderUUID BINARY(16),
                              receiverUUID BINARY(16),
                              amount BIGINT CHECK (amount > 0),
                              date DATETIME DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (senderUUID) REFERENCES Bank(userId),
                              FOREIGN KEY (receiverUUID) REFERENCES Bank(userId)
);