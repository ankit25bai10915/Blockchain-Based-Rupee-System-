package com.rupeeblockchain.service;

import com.rupeeblockchain.model.Block;
import com.rupeeblockchain.model.MiningResult;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public class MiningWorker
        implements Callable<MiningResult> {

    private final Block block;
    private final int difficulty;
    private final long startNonce;
    private final long endNonce;
    private final AtomicBoolean miningCompleted;

    public MiningWorker(
            Block block,
            int difficulty,
            long startNonce,
            long endNonce,
            AtomicBoolean miningCompleted) {

        this.block = block;
        this.difficulty = difficulty;
        this.startNonce = startNonce;
        this.endNonce = endNonce;
        this.miningCompleted = miningCompleted;
    }

    @Override
    public MiningResult call() {

        String target =
                "0".repeat(difficulty);

        for (
                long nonce = startNonce;
                nonce <= endNonce;
                nonce++
        ) {

            /*
             * If another thread already found
             * a valid nonce, stop searching.
             */
            if (miningCompleted.get()) {
                return new MiningResult(
                        false,
                        -1,
                        null
                );
            }

            String hash =
                    block.calculateHashForNonce(
                            nonce
                    );

            if (hash.startsWith(target)) {

                /*
                 * Only the first thread that successfully
                 * changes false -> true becomes the winner.
                 */
                if (miningCompleted.compareAndSet(
                        false,
                        true
                )) {

                    return new MiningResult(
                            true,
                            nonce,
                            hash
                    );
                }

                return new MiningResult(
                        false,
                        -1,
                        null
                );
            }
        }

        return new MiningResult(
                false,
                -1,
                null
        );
    }
}