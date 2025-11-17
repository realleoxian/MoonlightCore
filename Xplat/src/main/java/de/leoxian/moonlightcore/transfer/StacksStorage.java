package de.leoxian.moonlightcore.transfer;

import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import de.leoxian.moonlightcore.util.NBTSerializable;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public abstract class StacksStorage<V, R extends TransferResource<V>, S>  implements Storage<V, R>, NBTSerializable<CompoundTag> {
    public static final String NBT_KEY = MoonlightCore.nbt("stacks");

    private final List<StorageView<V, R>> views;
    private final List<StackJournal> snapshotJournals;

    protected final S emptyStack;
    protected final Codec<NonNullList<S>> codec;

    protected NonNullList<S> stacks;

    protected StacksStorage(int size, S emptyStack, Codec<S> stackCodec) {
        this(NonNullList.withSize(size, emptyStack), emptyStack, stackCodec);
    }

    protected StacksStorage(NonNullList<S> stacks, S emptyStack, Codec<S> stackCodec) {
        this.emptyStack = emptyStack;
        this.stacks = mutableCopy(stacks);
        this.codec = stackCodec.listOf().xmap(this::mutableCopy, Function.identity());
        this.snapshotJournals = new ArrayList<>(this.stacks.size());
        this.views = new ArrayList<>(this.stacks.size());

        for(int i = 0; i < this.stacks.size(); i++) {
            this.snapshotJournals.add(new StackJournal(i));
            this.views.add(new StackView(i));
        }
    }

    @Override
    public int insert(Transaction tx, R resource, int amount) {
        StorageInternals.checkNonEmptyNonNegative(resource, amount);

        int remaining = amount;
        for(StorageView<V, R> view : views) {
            if(remaining <= 0) {
                break;
            }

            remaining -= view.insert(tx, resource, amount);
        }

        return amount - remaining;
    }

    @Override
    public int extract(Transaction tx, R resource, int amount) {
        StorageInternals.checkNonEmptyNonNegative(resource, amount);

        int remaining = amount;
        for(StorageView<V, R> view : views) {
            if(remaining <= 0) {
                break;
            }

            remaining -= view.extract(tx, resource, amount);
        }

        return amount - remaining;
    }

    public void set(int index, R resource, int amount) {
        StorageInternals.checkNonNegative(amount);
        if(resource.isEmpty() && amount > 0) {
            throw new IllegalArgumentException("Resources is empty but the amount is positive");
        }

        S oldContent = this.stacks.set(index, getStackFrom(resource, amount));
        onContentsChanged(index, oldContent);
    }

    protected abstract R getResourceFrom(S stack);

    protected abstract int getAmountFrom(S stack);

    protected abstract S getStackFrom(R resource, int amount);

    protected abstract S copyOf(S stack);

    protected abstract int getCapacity(int index, R resource);

    protected void onContentsChanged(int index, S previousContent) {}

    @Override
    public CompoundTag writeToNBT() {
        CompoundTag tag = new CompoundTag();

        this.codec.encodeStart(NbtOps.INSTANCE, this.stacks)
                .resultOrPartial(error -> MoonlightCore.LOGGER.error("Failed to encode StacksStorage: {}", error))
                .ifPresent(nbt -> tag.put(NBT_KEY, nbt));

        return tag;
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        if(!tag.contains(NBT_KEY, Tag.TAG_LIST)) {
            return;
        }

        this.codec.parse(NbtOps.INSTANCE, tag.getCompound(NBT_KEY))
                .resultOrPartial(error -> MoonlightCore.LOGGER.error("Failed to parse StacksStorage: {}", error))
                .ifPresent(parsed -> stacks = parsed);
    }

    @Override
    public @NotNull StorageView<V, R> get(int index) {
        return this.views.get(index);
    }

    @Override
    public @NotNull Iterator<StorageView<V, R>> iterator() {
        return this.views.iterator();
    }

    @Override
    public int size() {
        return this.stacks.size();
    }

    public NonNullList<S> copyToList() {
        return mutableCopy(this.stacks);
    }

    @SuppressWarnings("unchecked")
    private NonNullList<S> mutableCopy(Collection<S> list) {
        return NonNullList.of(this.emptyStack, (S[]) list.toArray(Object[]::new));
    }

    private class StackView implements StorageView<V, R> {
        private final int index;

        private StackView(int index) {
            this.index = index;
        }

        @Override
        public int insert(Transaction tx, R resource, int amount) {
            StorageInternals.checkNonEmptyNonNegative(resource, amount);

            S currentStack = stacks.get(this.index);
            int currentAmount = getAmountFrom(currentStack);

            if((currentAmount == 0 || isResourceValid(resource))) {
                int inserted = Math.min(amount, getCapacity(resource) - currentAmount);

                if(inserted > 0) {
                    snapshotJournals.get(this.index).updateSnapshots(tx);
                    stacks.set(index, getStackFrom(resource, currentAmount + inserted));
                    return inserted;
                }
            }

            return 0;
        }

        @Override
        public int extract(Transaction tx, R resource, int amount) {
            StorageInternals.checkNonEmptyNonNegative(resource, amount);

            S currentStack = stacks.get(index);
            if(isResourceValid(resource)) {
                int currentAmount = getAmountFrom(currentStack);
                int extracted = Math.min(amount, currentAmount);

                if(extracted > 0) {
                    snapshotJournals.get(this.index).updateSnapshots(tx);
                    stacks.set(index, getStackFrom(resource, currentAmount - extracted));
                    return extracted;
                }
            }

            return 0;
        }

        @Override
        public boolean isResourceValid(R resource) {
            S currentStack = stacks.get(this.index);
            R currentResource = getResourceFrom(currentStack);

            return currentResource.isEmpty() || currentResource.fullyMatches(resource.get(), resource.getNBT());
        }

        @Override
        public int getCapacity(R resource) {
            return StacksStorage.this.getCapacity(this.index, resource);
        }

        @Override
        public R resource() {
            S stack = stacks.get(this.index);
            return getResourceFrom(stack);
        }

        @Override
        public int amount() {
            S stack = stacks.get(this.index);
            return getAmountFrom(stack);
        }
    }

    private class StackJournal extends SnapshotJournal<S> {
        private final int index;

        private StackJournal(int index) {
            this.index = index;
        }

        @Override
        public S createSnapshot() {
            return copyOf(stacks.get(this.index));
        }

        @Override
        public void revertToSnapshot(S snapshot) {
            stacks.set(this.index, snapshot);
        }

        @Override
        public void onRootCommit(S originalState) {
            onContentsChanged(this.index, originalState);
        }
    }
}
