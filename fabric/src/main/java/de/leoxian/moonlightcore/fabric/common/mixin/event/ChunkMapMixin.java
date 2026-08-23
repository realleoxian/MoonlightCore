package de.leoxian.moonlightcore.fabric.common.mixin.event;

import com.llamalad7.mixinextras.sugar.Local;
import de.leoxian.moonlightcore.common.event.ChunkDataEvents;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {
    @Shadow
    @Final
    private ServerLevel level;

    @Inject(
            method = "lambda$scheduleChunkLoad$3",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/storage/SerializableChunkData;read(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/ai/village/poi/PoiManager;Lnet/minecraft/world/level/chunk/storage/RegionStorageInfo;Lnet/minecraft/world/level/ChunkPos;)Lnet/minecraft/world/level/chunk/ProtoChunk;",
                    shift = At.Shift.AFTER
            )
    )
    private void moonlihtcore$dispatchChunkLoad(ChunkPos pos, Optional<SerializableChunkData> chunkData, CallbackInfoReturnable<ChunkAccess> cir) {
        chunkData.ifPresent(serializableChunkData ->
                ChunkDataEvents.LOAD.doFire().onChunkDataLoad(this.level, cir.getReturnValue(), serializableChunkData));
    }

    @Inject(
            method = "save",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Objects;requireNonNull(Ljava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    private void moonlightcore$dispatchChunkSave(ChunkAccess chunk, CallbackInfoReturnable<Boolean> cir, @Local ChunkPos pos, @Local ChunkStatus status, @Local SerializableChunkData data) {
        ChunkDataEvents.SAVE.doFire().onChunkDataSave(this.level, chunk, data);
    }
}
