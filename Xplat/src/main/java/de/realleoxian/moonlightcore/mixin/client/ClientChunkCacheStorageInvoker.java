package de.realleoxian.moonlightcore.mixin.client;

import net.minecraft.client.multiplayer.ClientChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientChunkCache.Storage.class)
public interface ClientChunkCacheStorageInvoker {
    @Invoker("inRange")
    boolean inRange(int x, int z);
}
