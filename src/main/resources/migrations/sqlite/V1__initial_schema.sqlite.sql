CREATE TABLE Bank (
                      userId BLOB PRIMARY KEY,
                      balance INTEGER DEFAULT 0 CHECK (balance >= 0)
);

CREATE TABLE DailyRewards (
                              userId BLOB PRIMARY KEY,
                              amount INTEGER,
                              last_claim_date DATETIME,
                              FOREIGN KEY (userId) REFERENCES Bank(userId)
);

CREATE TABLE Player (
                        playerId BLOB PRIMARY KEY,
                        name TEXT,
                        title TEXT,
                        onHandBalance INTEGER
);

CREATE TABLE TransactionFee (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                taxa INTEGER
);

CREATE TABLE Transactions (
                              id INTEGER PRIMARY KEY AUTOINCREMENT,
                              senderUUID BLOB,
                              receiverUUID BLOB,
                              amount INTEGER CHECK (amount > 0),
                              date DATETIME DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (senderUUID) REFERENCES Bank(userId),
                              FOREIGN KEY (receiverUUID) REFERENCES Bank(userId)
);
