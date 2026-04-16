package de.realleoxian.moonlightcore.forge.mixin;

import de.realleoxian.moonlightcore.api.event.ServerChunkEvents;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin {
    @Shadow
    @Final
    ServerLevel level;

    @Inject(
            method = "lambda$scheduleUnload$14",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/LevelChunk;setLoaded(Z)V"
            )
    )
    private void moonlightcore$scheduleUnload(ChunkHolder chunkHolder, CompletableFuture<ChunkAccess> completablefuture, long chunkPos, ChunkAccess p_203002_, CallbackInfo ci) {
        ServerChunkEvents.UNLOAD.invoker().onChunkUnload(this.level, p_203002_);
    }

    @Inject(
            method = "lambda$protoChunkToFullChunk$34",
            at = @At(value = "TAIL")
    )
    private void moonlightcore$fireUnloadChunkEvent(ChunkHolder holder, ChunkAccess p_214856_, CallbackInfoReturnable<ChunkAccess> cir) {
        ServerChunkEvents.LOAD.invoker().onChunkLoad(this.level, cir.getReturnValue());
    }
}
