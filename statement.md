# Project Statement — Blockchain-Based Rupee Transaction System

## Problem Statement

Conventional digital payment systems store balances and transaction history in
centralised relational databases. Those records are authoritative only because
the institution operating them is trusted — a privileged administrator can
technically update a row, backdate a timestamp, or delete an entry, and the
database itself preserves no evidence that the record ever looked different.
Independent auditors must therefore trust logs produced by the same party
being audited.

This project addresses that gap by implementing a **digital rupee (e-Rupee /
CBDC) transaction ledger** in which:

- Settled transactions are cryptographically immutable.
- Every record is independently verifiable by recomputing its hash, rather
  than trusted on the operator's word.
- The transaction lifecycle (`PENDING` → `CONFIRMED`) is explicit and
  observable at every step.
- Any tampering attempt — an altered amount, a modified sender/receiver, a
  reordered record — is detected automatically and reported at the exact
  block where it occurred.

The system is a local, offline academic simulation. No real currency, bank,
or network peer is involved at any point.

## Scope of the Project

**In scope:**
- User and administrator account management with wallet provisioning.
- Peer-to-peer digital rupee transfers staged in a pending memory pool.
- Proof-of-Work block mining (single-threaded and multi-threaded) sealing
  pending transactions into cryptographically linked blocks.
- Full blockchain validation by hash recomputation.
- A controlled tamper simulator to demonstrate fraud detection.
- Persistence via an embedded H2 relational database, plus human-readable
  flat-file audit exports.
- An automated JUnit 5 test suite covering hashing, persistence, wallets,
  transactions, and chain integrity.

**Out of scope:**
- Real currency, banking integration, or regulatory compliance.
- A peer-to-peer network of multiple nodes (this is a single-node simulation).
- Asymmetric key transaction signing (authentication is password-based).
- A graphical or web front end (the interface is a console application).

## Target Users

- **Account holders (citizens)** — register, hold a wallet, and transfer
  digital rupees to other users.
- **System administrators / miners** — seal pending transactions into
  blocks, run chain validation, and trigger tamper simulations.
- **Auditors / evaluators** — inspect the ledger and independently verify
  its integrity using the validation and export tools.

## High-Level Features

- Secure user and admin registration/login with credential validation.
- Automatic Genesis Block creation and persistent, restart-safe blockchain.
- Peer-to-peer digital rupee transfers with balance validation.
- Pending transaction memory pool with visibility into unconfirmed transfers.
- Proof-of-Work mining, including a multi-threaded miner with coordinated
  cancellation across worker threads.
- Full-chain integrity validation via SHA-256 hash recomputation.
- Controlled tamper simulation that demonstrably triggers detection.
- Embedded H2 database persistence across five relational tables (users,
  wallets, transactions, blocks, block–transaction links).
- Human-readable audit log and blockchain backup export to flat files.
- Comprehensive JUnit 5 regression suite (20 test cases across 10 classes).
