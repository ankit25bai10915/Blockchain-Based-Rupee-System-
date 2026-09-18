package com.rupeeblockchain.service;

import com.rupeeblockchain.model.Block;
import com.rupeeblockchain.model.MiningResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class MultithreadedMiningService {

    public MiningResult mineBlock(
            Block block,
            int difficulty,
            int threadCount) {

        if (threadCount <= 0) {
            throw new IllegalArgumentException(
                    "Thread count must be greater than zero."
            );
        }

        if (difficulty <= 0) {
            throw new IllegalArgumentException(
                    "Mining difficulty must be greater than zero."
            );
        }

        System.out.println();
        System.out.println(
                "=========================================="
        );
        System.out.println(
                "       MULTITHREADED PROOF-OF-WORK"
        );
        System.out.println(
                "=========================================="
        );

        System.out.println(
                "Number of threads : "
                        + threadCount
        );

        System.out.println(
                "Mining difficulty: "
                        + difficulty
        );

        System.out.println(
                "Required prefix   : "
                        + "0".repeat(difficulty)
        );

        System.out.println();
        System.out.println(
                "Starting mining threads..."
        );

        ExecutorService executorService =
                Executors.newFixedThreadPool(
                        threadCount
                );

        CompletionService<MiningResult>
                completionService =
                new ExecutorCompletionService<>(
                        executorService
                );

        AtomicBoolean miningCompleted =
                new AtomicBoolean(false);

        List<Future<MiningResult>> futures =
                new ArrayList<>();

        long rangeSize =
                Long.MAX_VALUE / threadCount;

        long startNonce = 1;

        long startTime =
                System.currentTimeMillis();

        try {

            for (int i = 0;
                    i < threadCount;
                    i++) {

                long endNonce;

                if (i == threadCount - 1) {

                    endNonce =
                            Long.MAX_VALUE;

                } else {

                    endNonce =
                            startNonce
                                    + rangeSize
                                    - 1;
                }

                MiningWorker worker =
                        new MiningWorker(
                                block,
                                difficulty,
                                startNonce,
                                endNonce,
                                miningCompleted
                        );

                Future<MiningResult> future =
                        completionService.submit(
                                worker
                        );

                futures.add(future);

                System.out.println(
                        "Thread "
                                + (i + 1)
                                + " searching nonce range "
                                + startNonce
                                + " - "
                                + endNonce
                );

                startNonce =
                        endNonce + 1;
            }

            MiningResult winningResult =
                    null;

            int completedTasks = 0;

            while (
                    completedTasks
                            < threadCount
            ) {

                Future<MiningResult> future =
                        completionService.take();

                completedTasks++;

                MiningResult result =
                        future.get();

                if (result != null
                        && result.isSuccessful()) {

                    winningResult =
                            result;

                    break;
                }
            }

            if (winningResult == null) {

                throw new IllegalStateException(
                        "No valid nonce was found."
                );
            }

            long endTime =
                    System.currentTimeMillis();

            block.applyMiningResult(
                    winningResult.getNonce(),
                    winningResult.getHash()
            );

            System.out.println();
            System.out.println(
                    "Multithreaded mining completed!"
            );

            System.out.println(
                    "Winning nonce : "
                            + winningResult.getNonce()
            );

            System.out.println(
                    "Winning hash  : "
                            + winningResult.getHash()
            );

            System.out.println(
                    "Mining time   : "
                            + (endTime - startTime)
                            + " ms"
            );

            System.out.println(
                    "Other mining threads have been stopped."
            );

            System.out.println(
                    "=========================================="
            );

            return winningResult;

        } catch (Exception e) {

            miningCompleted.set(true);

            throw new IllegalStateException(
                    "Multithreaded mining failed.",
                    e
            );

        } finally {

            /*
             * Cancel all remaining mining tasks.
             */
            for (Future<MiningResult> future :
                    futures) {

                if (!future.isDone()) {
                    future.cancel(true);
                }
            }

            executorService.shutdownNow();
        }
    }
}