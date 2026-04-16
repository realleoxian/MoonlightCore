package de.realleoxian.moonlightcore.forge.mixin;

import de.realleoxian.moonlightcore.api.event.ChunkDataEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {
    @Inject(
            method = "write",
            at = @At(value = "RETURN")
    )
    private static void moonlightcore$fireChunkDataSave(ServerLevel level, ChunkAccess chunk, CallbackInfoReturnable<CompoundTag> cir) {
        ChunkDataEvents.SAVE.invoker().onChunkDataSave(level, chunk, cir.getReturnValue());
    }

    @Inject(
            method = "read",
            at = @At(value = "RETURN")
    )
    private static void moonlightcore$fireChunkDataLoad(ServerLevel level, PoiManager poiManager, ChunkPos pos, CompoundTag tag, CallbackInfoReturnable<ProtoChunk> cir) {
        ChunkDataEvents.LOAD.invoker().onChunkDataLoad(level, cir.getReturnValue(), tag);
    }
}
