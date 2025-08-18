package de.leowgc.moonlightcore.transfer;

import de.leowgc.moonlightcore.api.transfer.Transaction;

import java.util.ArrayList;
import java.util.List;

public final class TransactionImpl implements Transaction {
    public static Transaction openRoot() {
        return new TransactionImpl();
    }

    private final List<CloseCallback> closeCallbacks = new ArrayList<>();
    private final List<Transaction> nestedTransactions = new ArrayList<>();
    private State state = State.OPENED;

    private TransactionImpl() {}

    @Override
    public void commit() {
        if(!this.isOpened()) {
            throw new IllegalStateException("Transaction in state `" + this.state + "` cannot be commited");
        }

        for(Transaction nested : this.nestedTransactions) {
            if(!nested.isCommited()) {
                throw new IllegalStateException("All nested transactions must be commited first");
            }
        }

        this.state = State.COMMITTED;
    }

    @Override
    public void abort() {
        if(!this.isOpened()) {
            throw new IllegalStateException("Transaction in state `" + this.state + "` cannot be aborted");
        }

        for(Transaction nested : this.nestedTransactions) {
            if(nested.isOpened()) {
                nested.abort();
            }
        }

        this.state = State.ABORTED;
    }

    @Override
    public Transaction openNested() {
        if(!this.isOpened()) {
            throw new IllegalStateException("Can't open nested transactions when the parent transaction is already closed");
        }

        Transaction transaction = new TransactionImpl();
        this.nestedTransactions.add(transaction);

        return transaction;
    }

    @Override
    public void close() {
        if(this.isOpened()) {
            this.abort();
        }

        RuntimeException errors = null;
        for(CloseCallback callback : this.closeCallbacks) {
            try {
                callback.onClose(this.state);
            } catch (Exception e) {
                if(errors == null) {
                    errors = new RuntimeException("Transaction callback failed.");
                }

                errors.addSuppressed(e);
            }
        }

        this.state = State.CLOSED;

        if(errors != null) {
            throw errors;
        }
    }

    @Override
    public void addCloseCallback(CloseCallback callback) {
        if(!this.isOpened()) {
            throw new IllegalStateException("Can not add more transactional tasks when the transaction is already closed. Transaction state: `" + this.state + "`");
        }

        this.closeCallbacks.add(callback);
    }

    @Override
    public State getState() {
        return this.state;
    }
}
