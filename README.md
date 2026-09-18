# Blockchain-Based Rupee Transaction System

A Java-based blockchain simulation for secure digital Rupee transactions.

This project demonstrates how blockchain technology can be used to create a tamper-evident transaction ledger with user accounts, wallets, transactions, proof-of-work mining, hash-based validation, transaction history, backups, and tampering detection.

> **Note:** This is an academic/project simulation. No real money or real digital currency is involved.

---

## Features

### User Features

- User registration
- User login
- Profile management
- Wallet management
- Rupee balance display
- Rupee transfers between users
- Pending transaction management
- Transaction history
- Transaction status tracking

### Admin Features

- Admin authentication
- View pending transactions
- Mine pending transactions
- View blockchain
- Validate blockchain integrity
- Simulate blockchain tampering
- Restore blockchain from backup
- Export blockchain
- Create blockchain backup
- View transaction log

### Blockchain Features

- Block creation
- SHA-256 hashing
- Previous-hash linking
- Proof-of-work mining
- Nonce generation
- Transaction storage inside blocks
- Blockchain integrity validation
- Tampering detection
- Blockchain backup and restoration

---

## Technology Stack

- **Java 17**
- **Maven**
- **H2 Database**
- **JUnit 5**
- **SHA-256**
- **Git & GitHub**

---

## Project Structure

```text
BlockchainRupeeSystem/
│
├── .mvn/
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── rupeeblockchain/
│   │               ├── App.java
│   │               │
│   │               ├── database/
│   │               │   ├── BlockRepository.java
│   │               │   ├── DatabaseManager.java
│   │               │   ├── TransactionRepository.java
│   │               │   ├── UserRepository.java
│   │               │   └── WalletRepository.java
│   │               │
│   │               ├── model/
│   │               │   ├── Admin.java
│   │               │   ├── Block.java
│   │               │   ├── MiningResult.java
│   │               │   ├── Transaction.java
│   │               │   ├── TransactionStatus.java
│   │               │   ├── User.java
│   │               │   └── Wallet.java
│   │               │
│   │               └── service/
│   │                   ├── AuthService.java
│   │                   ├── BlockchainService.java
│   │                   ├── FileService.java
│   │                   ├── HashUtil.java
│   │                   ├── MiningWorker.java
│   │                   ├── MultithreadedMiningService.java
│   │                   └── TransactionService.java
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── rupeeblockchain/
│                   ├── AppTest.java
│                   ├── BlockTest.java
│                   ├── BlockchainValidationTest.java
│                   ├── DatabaseTest.java
│                   ├── HashUtilTest.java
│                   ├── LoginTest.java
│                   ├── RegistrationTest.java
│                   ├── TransactionTest.java
│                   ├── WalletBalanceTest.java
│                   └── WalletTest.java
│
├── pom.xml
├── .gitignore
└── README.md