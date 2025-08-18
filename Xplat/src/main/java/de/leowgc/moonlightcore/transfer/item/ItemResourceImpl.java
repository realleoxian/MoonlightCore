package de.leowgc.moonlightcore.transfer.item;

import de.leowgc.moonlightcore.api.transfer.TransferResource;
import de.leowgc.moonlightcore.api.transfer.item.ItemResource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public final class ItemResourceImpl implements ItemResource {
    private ItemStack stack;
    private int amount;

    public ItemResourceImpl(ItemStack stack, int amount) {
        this.stack = stack.copy();
        this.amount = Math.min(amount, stack.getMaxStackSize());
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("stack", this.stack.save(new CompoundTag()));
        tag.putInt("amount", this.amount);

        return tag;
    }

    @Override
    public void fromNBT(CompoundTag nbt) {
        ItemStack savedStack = ItemStack.of(nbt.getCompound("stack"));
        int savedAmound = nbt.getInt("amount");

        this.stack = savedStack;
        this.amount = savedAmound;
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf byteBuf) {
        if(this.isBlank()) {
            byteBuf.writeBoolean(false);
        } else {
            byteBuf.writeItem(this.stack);
            byteBuf.writeVarInt(this.amount);
        }
    }

    @Override
    public void readFromBuffer(FriendlyByteBuf byteBuf) {
        if(byteBuf.readBoolean()) {
            this.stack = byteBuf.readItem();
            this.amount = byteBuf.readVarInt();
        }
    }

    @Override
    public ItemStack get() {
        return this.stack.copy();
    }

    @Override
    public int amount() {
        return this.amount;
    }

    @Override
    public TransferResource<ItemStack> copy() {
        return new ItemResourceImpl(this.stack, this.amount);
    }

    @Override
    public boolean isBlank() {
        return this.stack.isEmpty() || this.amount <= 0;
    }
}
