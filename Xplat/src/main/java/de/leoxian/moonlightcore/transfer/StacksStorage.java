package de.leoxian.moonlightcore.transfer;

import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.util.nullness.Nonnull;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;

import java.util.*;
import java.util.function.Function;

public abstract class StacksStorage<T, S> implements Storage<T> {
    public static final String NBT_KEY = MoonlightCore.nbt("stacks");

    private final List<StorageView<T>> views;
    private final List<StackJournal> snapshotJournals;

    protected final Codec<NonNullList<S>> codec;
    protected final S emptyStack;
    protected final int size;
    protected NonNullList<S> stacks;

    protected StacksStorage(int size, S emptyStack, Codec<S> stackCodec) {
        this(NonNullList.createWithCapacity(size), emptyStack, stackCodec);
    }

    protected StacksStorage(NonNullList<S> stacks, S emptyStack, Codec<S> stackCodec) {
        this.emptyStack = emptyStack;
        this.stacks = mutableCopy(stacks);
        this.codec = stackCodec.listOf().xmap(this::mutableCopy, Function.identity());
        this.snapshotJournals = new ArrayList<>();
        this.views = new ArrayList<>();
        this.size = this.stacks.size();

        for(int i = 0; i < stacks.size(); i++) {
            this.snapshotJournals.set(i, new StackJournal(i));
            this.views.set(i, new StackView(i));
        }
    }

    @Override
    public int insert(TransactionContext context, T insertedResource, int maxAmount) {
        StoragePreconditions.notNegative(maxAmount);
        int remaining = maxAmount;

        for(StorageView<T> view : views) {
            remaining -= view.insert(context, insertedResource, maxAmount);

            if(remaining == 0) {
                break;
            }
        }

        return maxAmount - remaining;
    }

    @Override
    public int extract(TransactionContext context, T extractedResource, int maxAmount) {
        StoragePreconditions.notNegative(maxAmount);
        int remaining = maxAmount;

        for(StorageView<T> view : views) {
            remaining -= view.extract(context, extractedResource, maxAmount);

            if(remaining == 0) {
                break;
            }
        }

        return maxAmount - remaining;
    }

    @Override
    public StorageView<T> get(int index) {
        Objects.checkIndex(index, this.size());
        return this.views.get(index);
    }

    @Override
    public @Nonnull Iterator<StorageView<T>> iterator() {
        return this.views.iterator();
    }

    @Override
    public int size() {
        return this.size;
    }

    public void writeToNBT(CompoundTag tag) {
        this.codec.encodeStart(NbtOps.INSTANCE, this.stacks)
                .resultOrPartial(partial -> MoonlightCore.LOGGER.error("Failed to encode StacksStorage. Error:\n{}", partial))
                .ifPresent(result -> tag.put(NBT_KEY, result));
    }

    public void readFromNBT(CompoundTag tag) {
        if(!tag.contains(NBT_KEY)) {
            return;
        }

        this.codec.parse(NbtOps.INSTANCE, tag.get(NBT_KEY))
                .resultOrPartial(partial -> MoonlightCore.LOGGER.error("Failed to decode StacksStorage. Error: \n{}", partial))
                .ifPresent(stacks -> this.stacks = stacks);
    }

    public void set(int index, T resource, int amount) {
        Objects.checkIndex(index, this.size());
        StoragePreconditions.notNegative(amount);

        S oldContent = this.stacks.set(index, getStackFrom(resource, amount));
        onContentsChanged(index, oldContent);
    }

    public S getStack(int index) {
        Objects.checkIndex(index, this.size());
        return this.stacks.get(index);
    }

    public boolean canInsert(int index, T resource) {
        Objects.checkIndex(index, this.size());
        return true;
    }

    public boolean canExtract(int index, T resource) {
        Objects.checkIndex(index, this.size());
        return true;
    }

    protected abstract T getResourceFrom(S stack);

    protected abstract S getStackFrom(T resource, int amount);

    protected abstract S copyOf(S stack);

    protected abstract int getAmountFrom(S stack);

    public abstract int getCapacity(int index, T resource);

    protected void onContentsChanged(int index, S previousContent) {}

    @SuppressWarnings("unchecked")
    private NonNullList<S> mutableCopy(Collection<S> collection) {
        return NonNullList.of(this.emptyStack, (S[]) collection.toArray(Object[]::new));
    }

    private class StackView implements StorageView<T> {
        private final int index;

        private StackView(int index) {
            this.index = index;
        }

        @Override
        public int insert(TransactionContext context, T insertedResource, int maxAmount) {
            StoragePreconditions.notNegative(maxAmount);

            S currentStack = stacks.get(index);
            T currentResource = getResourceFrom(currentStack);
            int currentAmount = getAmountFrom(currentStack);

            if((currentAmount == 0 || insertedResource.equals(currentResource)) && canInsert(index, insertedResource)) {
                int inserted = Math.min(maxAmount, getCapacity(insertedResource) - currentAmount);

                if(inserted > 0) {
                    snapshotJournals.get(index).updateSnapshots(context);
                    stacks.set(index, getStackFrom(insertedResource, currentAmount + inserted));
                    return inserted;
                }
            }

            return 0;
        }

        @Override
        public int extract(TransactionContext context, T extractedResource, int maxAmount) {
            StoragePreconditions.notNegative(maxAmount);

            S currentStack = stacks.get(index);
            T currentResource = getResourceFrom(currentStack);
            int currentAmount = getAmountFrom(currentStack);

            if((currentAmount > 0 || extractedResource.equals(currentResource)) && canExtract(index, extractedResource)) {
                int extracted = Math.min(maxAmount, currentAmount);

                if(extracted > 0) {
                    snapshotJournals.get(index).updateSnapshots(context);
                    stacks.set(index, getStackFrom(extractedResource, currentAmount - extracted));
                    return extracted;
                }
            }

            return 0;
        }

        @Override
        public int getCapacity(T resource) {
            return StacksStorage.this.getCapacity(index, resource);
        }

        @Override
        public int getAmount() {
            S stack = stacks.get(index);
            return getAmountFrom(stack);
        }

        @Override
        public boolean isResourceBlank() {
            return getAmount() == 0;
        }

        @Override
        public T getResource() {
            S stack = stacks.get(index);
            return getResourceFrom(stack);
        }
    }

    private class StackJournal extends SnapshotJournal<S> {
        private final int index;

        StackJournal(int index) {
            this.index = index;
        }

        @Override
        public S createSnapshot() {
            return copyOf(stacks.get(index));
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
