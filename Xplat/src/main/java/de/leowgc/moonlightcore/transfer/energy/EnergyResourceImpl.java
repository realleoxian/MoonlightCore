package de.leowgc.moonlightcore.transfer.energy;

import de.leowgc.moonlightcore.api.transfer.TransferResource;
import de.leowgc.moonlightcore.api.transfer.energy.EnergyResource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public final class EnergyResourceImpl implements EnergyResource {
    private long energy;

    public EnergyResourceImpl(long energy) {
        this.energy = energy;
    }

    @Override
    public Long get() {
        return this.energy;
    }

    @Override
    public int amount() {
        return (int) this.energy;
    }

    @Override
    public TransferResource<Long> copy() {
        return new EnergyResourceImpl(this.energy);
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("energy", this.energy);

        return nbt;
    }

    @Override
    public void fromNBT(CompoundTag nbt) {
        this.energy = nbt.getLong("energy");
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf byteBuf) {
        byteBuf.writeLong(this.energy);
    }

    @Override
    public void readFromBuffer(FriendlyByteBuf byteBuf) {
        this.energy = byteBuf.readLong();
    }
}
