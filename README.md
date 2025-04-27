# dEconomy

## Description

dEconomy is an economy plugin for Minecraft servers, offering features such as balance management, transactions, daily
rewards, and bank account integration. The goal is to provide a robust and customizable solution for servers
implementing economic systems.

## Features

- **Balance Management**: Commands to view and manage player balances.
- **Banking System**: Deposit, withdraw, and manage bank accounts.
- **Daily Rewards**: Configurable cooldown-based daily rewards system.
- **Transactions**: Transaction history between players.
- **Administration**: Commands for administrators to manage configurations and balances.
- **Database Integration**: Persistent data storage support.

## Commands

### Player Commands

- `/balance` or `/bal`: Displays the player's balance.
- `/balance <player>`: Displays another player's balance.
- `/pay <player> <amount>`: Transfers money to another player.
- `/bank deposit <amount>`: Deposits money into the bank account.
- `/bank withdraw <amount>`: Withdraws money from the bank account.
- `/dailyreward`: Claims the daily reward.

### Admin Commands

- `/setbalance <player> <value> <type>`: Sets a player's balance (cash or bank).
- `/deconomy reload <config/messages>`: Reloads configuration or message files.
- `/transactions [player]`: Displays a player's transaction history.

## Technologies Used

- **Language**: Java
- **Frameworks**: Bukkit/Spigot API, Lamp
- **Database**: MySQL or SQLITE
- **Dependency Management**: Maven

## How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/sudoaptgetmach/dEconomy.git
   ```
2. Build the project:
   ```bash
   mvn clean package
   ```
3. Copy the generated `.jar` file to your Minecraft server's `plugins` folder.
4. Start the server to generate configuration files.
5. Configure the database in the generated `config.yml` file.

## Database Structure

### Main Tables

- **Bank**: Stores bank account information.
    - `userId`: Unique identifier for the player.
    - `balance`: Bank account balance.
- **DailyRewards**: Stores daily reward information.
    - `userId`: Unique identifier for the player.
    - `amount`: Last reward amount.
    - `last_claim_date`: Date of the last reward claim.
- **Transactions**: Stores transaction history.
    - `id`: Unique transaction identifier.
    - `senderUUID`: UUID of the sender.
    - `receiverUUID`: UUID of the receiver.
    - `amount`: Transferred amount.
    - `date`: Transaction date.

## Contribution

Contributions are welcome! Follow these steps:

1. Fork the repository.
2. Create a branch for your feature:
   ```bash
   git checkout -b my-feature
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add my feature"
   ```
4. Push to the remote repository:
   ```bash
   git push origin my-feature
   ```
5. Open a Pull Request.

## License

This project is licensed under the MIT License.
