package de.leowgc.moonlightcore.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class BlockEntityBase extends BlockEntity {
    private boolean isChanged;

    public BlockEntityBase(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void setChangedFast() {
        if(this.level == null) return;

        this.level.blockEntityChanged(this.getBlockPos());
        this.isChanged = true;
    }

    public void dispatchIfChanged() {
        if(!isChanged) return;

        BlockEntityHelper.dispatchToNearbyPlayers(this);
        this.isChanged = false;
    }

    public void setChangedAndDispatch() {
        this.setChanged();
        this.dispatchIfChanged();

        this.isChanged = true;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.isChanged = true;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::saveWithFullMetadata);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithFullMetadata();
    }
}
