package de.realleoxian.moonlightcore.api.transfer.storage;

import com.mojang.serialization.Codec;
import de.realleoxian.moonlightcore.api.transfer.transaction.SnapshotJournal;
import de.realleoxian.moonlightcore.api.transfer.transaction.TransactionContext;
import de.realleoxian.moonlightcore.api.misc.NBTUtils;
import de.realleoxian.moonlightcore.impl.transfer.StoragePreconditions;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public abstract class StacksStorage<T, S> implements Storage<T> {
    protected static final String TAG_STACKS = "moonlightcore:stacks";

    private final List<StackJournal> snapshotJournals = new ArrayList<>();
    private final NonNullList<S> stacks;
    private final S emptyStack;
    private final Codec<NonNullList<S>> codec;

    public StacksStorage(int size, S emptyStack, Codec<S> codec) {
        this(NonNullList.withSize(size, emptyStack), emptyStack, codec);
    }

    public StacksStorage(NonNullList<S> stacks, S emptyStack, Codec<S> codec) {
        this.stacks = mutableCopy(stacks);
        this.emptyStack = emptyStack;
        this.codec = codec.listOf().xmap(this::mutableCopy, Function.identity());

        while(snapshotJournals.size() < stacks.size()) {
            snapshotJournals.add(new StackJournal(snapshotJournals.size()));
        }
    }

    public final void writeToNBT(CompoundTag nbt) {
        NBTUtils.store(nbt, TAG_STACKS, codec, stacks);
    }

    public final void readFromNBT(CompoundTag nbt) {
        NBTUtils.readOpt(nbt, TAG_STACKS, codec).ifPresent(nbtStacks -> {
            for(int i = 0; i < this.stacks.size(); i++) {
                this.stacks.set(i, nbtStacks.get(i));
            }
        });
    }

    protected abstract S copyStack(S stack);

    protected abstract S getStackFrom(T resource, int amount);

    protected abstract T getResourceFrom(S stack);

    protected abstract int getAmountFrom(S stack);

    protected abstract boolean isEmptyStack(S stack);

    protected void onContentsChanged(int index, S originalStack) {

    }

    @Override
    public int insert(TransactionContext tx, int index, T resource, int maxAmount) {
        Objects.checkIndex(index, size());
        StoragePreconditions.notNegative(maxAmount);

        S currentStack = stacks.get(index);
        T currentResource = getResourceFrom(currentStack);
        if((isBlank(index) || currentResource == resource) && canInsert(index, currentResource) && supportsInsertion()) {
            int currentAmount = getAmountFrom(currentStack);
            int inserted = Math.min(maxAmount, getCapacity(index, currentResource) - currentAmount);

            if(inserted > 0) {
                snapshotJournals.get(index).updateSnapshots(tx);

                S newStack = currentAmount == 0 ?
                        getStackFrom(resource, inserted) :
                        getStackFrom(currentResource, currentAmount + inserted);
                stacks.set(index, newStack);
                return inserted;
            }
        }

        return 0;
    }

    @Override
    public int extract(TransactionContext tx, int index, T resource, int maxAmount) {
        Objects.checkIndex(index, size());
        StoragePreconditions.notNegative(maxAmount);

        S currentStack = stacks.get(index);
        T currentResource = getResourceFrom(currentStack);
        if((!isBlank(index) || currentResource == resource) && canExtract(index, currentResource) && supportsExtraction()) {
            int currentAmount = getAmountFrom(currentStack);
            int extracted = Math.min(maxAmount, currentAmount);

            if(extracted > 0) {
                snapshotJournals.get(index).updateSnapshots(tx);

                S newStack = (currentAmount - extracted) > 0 ?
                        getStackFrom(currentResource, currentAmount - extracted) :
                        emptyStack;
                stacks.set(index, newStack);
                return extracted;
            }
        }

        return 0;
    }

    @Override
    public T getResource(int index) {
        S stack = stacks.get(index);
        return getResourceFrom(stack);
    }

    @Override
    public int getAmount(int index) {
        S stack = stacks.get(index);
        return getAmountFrom(stack);
    }

    @Override
    public boolean isBlank(int index) {
        S stack = stacks.get(index);
        return isEmptyStack(stack);
    }

    @Override
    public int size() {
        return stacks.size();
    }

    @SuppressWarnings("unchecked")
    private NonNullList<S> mutableCopy(Collection<S> original) {
        return NonNullList.of(emptyStack, (S[]) original.toArray(Object[]::new));
    }

    private class StackJournal extends SnapshotJournal<S> {

        private final int index;

        private StackJournal(int index) {
            this.index = index;
        }

        @Override
        public S createSnapshot() {
            return copyStack(stacks.get(index));
        }

        @Override
        public void revertToSnapshot(S snapshot) {
            stacks.set(index, snapshot);
        }

        @Override
        public void onRootCommit(S originalState) {
            onContentsChanged(index, originalState);
        }

    }
}
