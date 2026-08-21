package de.leoxian.moonlightcore.common.transfer.storage;

import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.common.transfer.resource.Resource;
import de.leoxian.moonlightcore.common.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.common.transfer.transaction.Transaction;
import de.leoxian.moonlightcore.common.util.ValueIOSerializable;
import de.leoxian.moonlightcore.internal.common.transfer.StorageInternals;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class StacksStorage<T extends Resource, S> implements Storage<T>, ValueIOSerializable {
    public static final String VALUE_IO_KEY = "moonlightcore:stacks";

    private final S emptyStack;
    private NonNullList<S> stacks;
    private final List<SnapshotJournalImpl> journals;
    private final Codec<NonNullList<S>> codec;

    @SuppressWarnings("unchecked")
    public StacksStorage(Codec<S> stackCodec, S emptyStack, NonNullList<S> stacks) {
        this.codec = stackCodec.listOf().xmap(this::copyList, Function.identity());
        this.emptyStack = emptyStack;
        this.stacks = NonNullList.of(emptyStack, (S[]) stacks.toArray(Object[]::new));

        this.journals = new ArrayList<>();
        for (int i = 0; i < this.stacks.size(); i++) {
            this.journals.add(i, new SnapshotJournalImpl(i));
        }
    }

    public StacksStorage(Codec<S> stackCodec, S emptyStack, int size) {
        this (stackCodec, emptyStack, NonNullList.withSize(size, emptyStack));
    }

    @Override
    public void serialize(ValueOutput output) {
        output.list(VALUE_IO_KEY, this.codec).add(this.stacks);
    }

    @Override
    public void deserialize(ValueInput input) {
        input.read(VALUE_IO_KEY, this.codec).ifPresent(stacks -> this.stacks = copyList(stacks));
    }

    protected abstract S createStack(T resource, int amount);

    protected abstract T getResourceFrom(S stack);

    protected abstract int getAmountFrom(S stack);

    protected abstract S copyStack(S stack);

    protected void onContentChanged(int index, S oldStack, S newStack) {

    }

    @Override
    public int insert(Transaction transaction, int index, T resource, int maxAmount) {
        StorageInternals.checkIndex(index, this);
        StorageInternals.checkNotEmpty(resource);
        StorageInternals.checkNotNegative(maxAmount);

        S currentStack = stacks.get(index);
        T currentResource = getResourceFrom(currentStack);
        int currentAmount = getAmountFrom(currentStack);
        if ((currentResource.isEmpty() || currentResource.equals(resource)) && canInsert(index, resource) && supportsInsertion()) {
            int inserted = Math.min(maxAmount, getCapacity(index, resource) - currentAmount);
            if (inserted > 0) {
                this.journals.get(index).updateSnapshots(transaction);

                S newStack;
                if (currentResource.isEmpty()) {
                    newStack = createStack(resource, inserted);
                } else {
                    newStack = createStack(currentResource, currentAmount + inserted);
                }
                this.stacks.set(index, newStack);
                return inserted;
            }
        }
        return 0;
    }

    @Override
    public int extract(Transaction transaction, int index, T resource, int maxAmount) {
        StorageInternals.checkIndex(index, this);
        StorageInternals.checkNotEmpty(resource);
        StorageInternals.checkNotNegative(maxAmount);

        S currentStack = stacks.get(index);
        T currentResource = getResourceFrom(currentStack);
        int currentAmount = getAmountFrom(currentStack);
        if (currentResource.equals(resource) && canExtract(index, resource) && supportsExtraction()) {
            int extracted = Math.min(maxAmount, currentAmount);
            if (extracted > 0) {
                this.journals.get(index).updateSnapshots(transaction);

                S newStack;
                int remainingAmount = currentAmount - extracted;
                if (remainingAmount == 0) {
                    newStack = copyStack(this.emptyStack);
                } else {
                    newStack = createStack(currentResource, remainingAmount);
                }
                this.stacks.set(index, newStack);
                return extracted;
            }
        }
        return 0;
    }

    @Override
    public T getResource(int index) {
        StorageInternals.checkIndex(index, this);
        return getResourceFrom(stacks.get(index));
    }

    @Override
    public int getAmount(int index) {
        StorageInternals.checkIndex(index, this);
        return getAmountFrom(stacks.get(index));
    }

    @SuppressWarnings("unchecked")
    private NonNullList<S> copyList(Collection<?> collection) {
        return NonNullList.of(this.emptyStack, (S[]) collection.toArray(Object[]::new));
    }

    private class SnapshotJournalImpl extends SnapshotJournal<S> {
        private final int index;

        private SnapshotJournalImpl(int index) {
            this.index = index;
        }

        @Override
        protected S createSnapshot() {
            return copyStack(stacks.get(this.index));
        }

        @Override
        protected void readSnapshot(S snapshot) {
            stacks.set(this.index, snapshot);
        }

        @Override
        protected void onRootCommit(S originalState) {
            S currentStack = stacks.get(index);
            onContentChanged(this.index, originalState, currentStack);
        }
    }
}
