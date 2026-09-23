package de.leoxian.moonlightcore.common.transfer.transaction;

import de.leoxian.moonlightcore.internal.common.transfer.transaction.TransactionManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface Transaction extends TransactionContext, AutoCloseable {
	/// Opens a new transaction that may be attached to a parent transaction
	/// @param parent The parent transaction, may be `null` if root
	static Transaction open(@Nullable TransactionContext parent) {
		return TransactionManager.open(parent);
	}

	/// @return Opens a root transaction on this thread
	/// @throws IllegalStateException If there is a current active transaction on this thread
	static Transaction openRoot() {
		return TransactionManager.openRoot();
	}

	/// @return The current lifecycle of the transaction stack on this thread
	static Lifecycle getLifecycle() {
		return TransactionManager.getLifecycle();
	}

	/// Registers a callback that will be invoked when this transaction gets closed.
	/// @param closeCallback The callback
	/// @throws IllegalStateException If this function was invoked from a thread the transaction doesn't belong to
	/// @throws IllegalStateException If this transaction was closed
	void addCloseCallback(CloseCallback closeCallback);

	/// Close the current transaction, commiting all changes that happened during this transaction and the
	/// commited transactions opened with [#openNested()] from this transaction.
	/// If this transaction it's the root, all changes get applied. If this transaction it's a 'child' from
	/// other transaction, changes will apply when and if the root transaction commits.
	/// @throws IllegalStateException If this function was invoked from a thread the transaction doesn't belong to
	/// @throws IllegalStateException If this transaction isn't the current transaction
	/// @throws IllegalStateException If this transaction was closed
	void commit();

	/// Close the current transaction, rolling back all changes made from this and all other transactions
	/// which this one it's the parent.
	/// @throws IllegalStateException If this function was invoked from a thread the transaction doesn't belong to
	/// @throws IllegalStateException If this transaction isn't the current transaction
	/// @throws IllegalStateException If this transaction was closed
	void abort();

	/// Aborts the transaction
	@Override
	void close();

	@FunctionalInterface
	interface CloseCallback {
		/// A callback invoked when a transaction gets closed
		/// @param context The transaction context
		/// @param wasAborted Wether the transaction was aborted
		void onTransactionClose(TransactionContext context, boolean wasAborted);
	}
}
