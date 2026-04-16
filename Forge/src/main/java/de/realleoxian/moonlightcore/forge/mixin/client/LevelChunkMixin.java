package de.realleoxian.moonlightcore.forge.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import de.realleoxian.moonlightcore.api.client.event.ClientBlockEntityEvents;
import de.realleoxian.moonlightcore.api.event.ServerBlockEntityEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(LevelChunk.class)
abstract class LevelChunkMixin {
    @Shadow
    public abstract Level getLevel();

    @Inject(
            method = "setBlockEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private void moonlightcore$fireServerBlockEntityLoad(BlockEntity blockEntity, CallbackInfo ci, @Local BlockEntity removed) {
        if (blockEntity != null && blockEntity != removed) {
            if (getLevel() instanceof ClientLevel clientLevel) {
                ClientBlockEntityEvents.LOAD.invoker().onBlockEntityLoad(clientLevel, blockEntity);
            }
        }
    }

    @Inject(
            method = "setBlockEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/BlockEntity;setRemoved()V",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void moonlightcore$fireServerBlockEntityUnload$1(BlockEntity blockEntity, CallbackInfo ci, BlockPos blockPos, BlockEntity removedBlockEntity) {
        if (removedBlockEntity != null && getLevel() instanceof ClientLevel clientLevel) {
            ClientBlockEntityEvents.UNLOAD.invoker().onBlockEntityUnload(clientLevel, removedBlockEntity);
        }
    }

    @Inject(
            method = "removeBlockEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/BlockEntity;setRemoved()V"
            )
    )
    private void moonlightcore$fireServerBlockEntityUnload$2(BlockPos pos, CallbackInfo ci, @Local BlockEntity removed) {
        if (removed != null && getLevel() instanceof ClientLevel clientLevel) {
            ClientBlockEntityEvents.UNLOAD.invoker().onBlockEntityUnload(clientLevel, removed);
        }
    }

    @Redirect(
            method = "getBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/chunk/LevelChunk$EntityCreationType;)Lnet/minecraft/world/level/block/entity/BlockEntity;",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private <K, V> V moonlightcore$fireServerBlockEntityUnload$3(Map<K, V> instance, K key) {
        V removed = instance.remove(key);

        if (removed != null && getLevel() instanceof ClientLevel clientLevel) {
            ClientBlockEntityEvents.UNLOAD.invoker().onBlockEntityUnload(clientLevel, (BlockEntity) removed);
        }
        return removed;
    }
}
