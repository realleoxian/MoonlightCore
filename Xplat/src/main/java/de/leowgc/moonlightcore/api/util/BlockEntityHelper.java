package de.leowgc.moonlightcore.api.util;

import de.leowgc.moonlightcore.core.MoonlightCoreConfiguration;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class BlockEntityHelper {

    public static void dispatchToNearbyPlayers (BlockEntity blockEntity) {
        var level = blockEntity.getLevel();
        if (level == null) return;

        var packet = blockEntity.getUpdatePacket();
        if (packet == null) return;

        var players = level.players();
        for (var player : players) {
            if (player instanceof ServerPlayer sp) {
                    if (Math.hypot(sp.getX() - blockEntity.getBlockPos().getX() + 0.5, sp.getZ() - blockEntity.getBlockPos().getZ() + 0.5) < MoonlightCoreConfiguration.SERVER.syncRange()) {
                    sp.connection.send(packet);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTicker (BlockEntityType<A> candidate, BlockEntityType<E> desired, BlockEntityTicker<? super E> ticker) {
        return desired == candidate ? (BlockEntityTicker<A>) ticker : null;
    }

}
