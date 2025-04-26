CREATE TABLE Bank (
                      userId BINARY(16) PRIMARY KEY,
                      balance DECIMAL(20, 2) DEFAULT 0 CHECK (balance >= 0)
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
                        onHandBalance DECIMAL(20, 2)
);

CREATE TABLE TransactionFee (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                taxa INT
);

CREATE TABLE Transactions (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              senderUUID BINARY(16),
                              receiverUUID BINARY(16),
                              amount DECIMAL(20, 2) CHECK (amount > 0),
                              date DATETIME DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (senderUUID) REFERENCES Bank(userId),
                              FOREIGN KEY (receiverUUID) REFERENCES Bank(userId)
);
