package de.realleoxian.moonlightcore.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "net.minecraft.client.multiplayer.ClientChunkCache$Storage")
public interface ClientChunkCacheStorageInvoker {
    @Invoker
    boolean invokeInRange(int x, int z);
}
