package de.realleoxian.moonlightcore.forge.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import de.realleoxian.moonlightcore.api.client.event.ClientChunkEvents;
import de.realleoxian.moonlightcore.mixin.client.ClientChunkCacheStorageInvoker;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(ClientChunkCache.class)
public class ClientChunkCacheMixin {
    @Shadow
    @Final
    ClientLevel level;

    @Inject(
            method = "replaceWithPacketData",
            at = @At("TAIL")
    )
    private void moonlightcore$loadChunkFromPacket(int x, int z, FriendlyByteBuf buffer, CompoundTag tag, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> consumer, CallbackInfoReturnable<LevelChunk> cir) {
        ClientChunkEvents.LOAD.invoker().onChunkLoad(this.level, cir.getReturnValue());
    }

    @Inject(
            method = "replaceWithPacketData",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ChunkPos;)Lnet/minecraft/world/level/chunk/LevelChunk;",
                    shift = At.Shift.BEFORE
            )
    )
    private void moonlightcore$loadChunkFromPacket$1(int x, int z, FriendlyByteBuf buffer, CompoundTag tag, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> consumer, CallbackInfoReturnable<LevelChunk> cir, @Local LevelChunk levelchunk) {
        if (levelchunk != null) {
            ClientChunkEvents.UNLOAD.invoker().onChunkUnload(this.level, levelchunk);
        }
    }

    @Inject(
            method = "drop",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientChunkCache$Storage;replace(ILnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/chunk/LevelChunk;)Lnet/minecraft/world/level/chunk/LevelChunk;"
            )
    )
    private void moonlightcore$drop(int x, int z, CallbackInfo ci, @Local LevelChunk levelchunk) {
        ClientChunkEvents.UNLOAD.invoker().onChunkUnload(this.level, levelchunk);
    }

    @Inject(
            method = "updateViewRadius",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientChunkCache$Storage;inRange(II)Z"
            )
    )
    private void moonlightcore$updateViewRadius(int viewDistance, CallbackInfo ci, @Local ClientChunkCache.Storage clientchunkcache$storage, @Local ChunkPos chunkpos, @Local LevelChunk levelchunk) {
        if (!((ClientChunkCacheStorageInvoker) (Object) clientchunkcache$storage).inRange(chunkpos.x, chunkpos.z)) {
            ClientChunkEvents.UNLOAD.invoker().onChunkUnload(this.level, levelchunk);
        }
    }
}
