package com.rupeeblockchain.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.rupeeblockchain.model.Block;
import com.rupeeblockchain.model.Transaction;

public class FileService {

    private static final String DATA_DIRECTORY =
            "data";

    private static final String TRANSACTION_LOG =
            DATA_DIRECTORY
                    + File.separator
                    + "transaction_log.txt";

    private static final String BLOCKCHAIN_EXPORT =
            DATA_DIRECTORY
                    + File.separator
                    + "blockchain_export.txt";

    private static final String BLOCKCHAIN_BACKUP =
            DATA_DIRECTORY
                    + File.separator
                    + "blockchain_backup.txt";

    public FileService() {

        createDataDirectory();

        createTransactionLogIfRequired();
    }

    private void createDataDirectory() {

        File directory =
                new File(DATA_DIRECTORY);

        if (!directory.exists()) {

            boolean created =
                    directory.mkdirs();

            if (!created) {

                throw new IllegalStateException(
                        "Unable to create data directory."
                );
            }
        }
    }

    /*
     * Make sure the transaction log file exists
     * as soon as FileService is initialized.
     */
    private void createTransactionLogIfRequired() {

        File logFile =
                new File(TRANSACTION_LOG);

        if (logFile.exists()) {
            return;
        }

        try (BufferedWriter writer =
                     new BufferedWriter(
                             new FileWriter(logFile)
                     )) {

            writer.write(
                    "=========================================="
            );

            writer.newLine();

            writer.write(
                    "        RUPEE TRANSACTION LOG"
            );

            writer.newLine();

            writer.write(
                    "=========================================="
            );

            writer.newLine();

            writer.newLine();

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Failed to create transaction log.",
                    e
            );
        }
    }

    public void logTransaction(
            Transaction transaction) {

        try (BufferedWriter writer =
                     new BufferedWriter(
                             new FileWriter(
                                     TRANSACTION_LOG,
                                     true
                             )
                     )) {

            writer.write(
                    "Transaction ID : "
                            + transaction
                                    .getTransactionId()
            );

            writer.newLine();

            writer.write(
                    "Sender         : "
                            + transaction
                                    .getSenderId()
            );

            writer.newLine();

            writer.write(
                    "Receiver       : "
                            + transaction
                                    .getReceiverId()
            );

            writer.newLine();

            writer.write(
                    "Amount         : ₹"
                            + transaction
                                    .getAmount()
            );

            writer.newLine();

            writer.write(
                    "Timestamp      : "
                            + transaction
                                    .getTimestamp()
            );

            writer.newLine();

            writer.write(
                    "Status         : "
                            + transaction
                                    .getStatus()
            );

            writer.newLine();

            writer.write(
                    "------------------------------------------"
            );

            writer.newLine();

            writer.newLine();

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Failed to write transaction log.",
                    e
            );
        }
    }

    public void exportBlockchain(
            List<Block> blockchain) {

        try (BufferedWriter writer =
                     new BufferedWriter(
                             new FileWriter(
                                     BLOCKCHAIN_EXPORT
                             )
                     )) {

            writer.write(
                    "=========================================="
            );

            writer.newLine();

            writer.write(
                    "       RUPEE BLOCKCHAIN EXPORT"
            );

            writer.newLine();

            writer.write(
                    "=========================================="
            );

            writer.newLine();

            writer.newLine();

            for (Block block :
                    blockchain) {

                writeBlock(
                        writer,
                        block
                );
            }

            writer.write(
                    "=========================================="
            );

            writer.newLine();

            writer.write(
                    "       END OF BLOCKCHAIN EXPORT"
            );

            writer.newLine();

            writer.write(
                    "=========================================="
            );

            writer.newLine();

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Failed to export blockchain.",
                    e
            );
        }
    }

    public void createBlockchainBackup(
            List<Block> blockchain) {

        try (BufferedWriter writer =
                     new BufferedWriter(
                             new FileWriter(
                                     BLOCKCHAIN_BACKUP
                             )
                     )) {

            writer.write(
                    "RUPEE BLOCKCHAIN BACKUP"
            );

            writer.newLine();

            writer.write(
                    "=========================================="
            );

            writer.newLine();

            writer.newLine();

            for (Block block :
                    blockchain) {

                writeBlock(
                        writer,
                        block
                );
            }

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Failed to create blockchain backup.",
                    e
            );
        }
    }

    private void writeBlock(
            BufferedWriter writer,
            Block block)
            throws IOException {

        writer.write(
                "Block #" + block.getIndex()
        );

        writer.newLine();

        writer.write(
                "Timestamp    : "
                        + block.getTimestamp()
        );

        writer.newLine();

        writer.write(
                "Previous Hash: "
                        + block.getPreviousHash()
        );

        writer.newLine();

        writer.write(
                "Hash         : "
                        + block.getHash()
        );

        writer.newLine();

        writer.write(
                "Nonce        : "
                        + block.getNonce()
        );

        writer.newLine();

        writer.write(
                "Transactions : "
                        + block.getTransactions()
                        .size()
        );

        writer.newLine();

        for (Transaction transaction :
                block.getTransactions()) {

            writer.write(
                    "  "
                            + transaction
                                    .getTransactionId()
                            + " | "
                            + transaction
                                    .getSenderId()
                            + " -> "
                            + transaction
                                    .getReceiverId()
                            + " | ₹"
                            + transaction
                                    .getAmount()
                            + " | "
                            + transaction
                                    .getStatus()
            );

            writer.newLine();
        }

        writer.write(
                "------------------------------------------"
        );

        writer.newLine();

        writer.newLine();
    }

    public void readTransactionLog() {

        File file =
                new File(
                        TRANSACTION_LOG
                );

        if (!file.exists()) {

            System.out.println();
            System.out.println(
                    "No transaction log exists yet."
            );

            return;
        }

        System.out.println();
        System.out.println(
                "=========================================="
        );

        System.out.println(
                "          TRANSACTION LOG"
        );

        System.out.println(
                "=========================================="
        );

        try (BufferedReader reader =
                     new BufferedReader(
                             new FileReader(file)
                     )) {

            String line;

            while ((line =
                    reader.readLine()) != null) {

                System.out.println(line);
            }

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Failed to read transaction log.",
                    e
            );
        }

        System.out.println(
                "=========================================="
        );
    }

    public String getTransactionLogPath() {

        return TRANSACTION_LOG;
    }

    public String getBlockchainExportPath() {

        return BLOCKCHAIN_EXPORT;
    }

    public String getBlockchainBackupPath() {

        return BLOCKCHAIN_BACKUP;
    }
}