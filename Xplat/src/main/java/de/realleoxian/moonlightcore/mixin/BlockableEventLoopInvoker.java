package de.realleoxian.moonlightcore.mixin;

import net.minecraft.util.thread.BlockableEventLoop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.concurrent.CompletableFuture;

@Mixin(BlockableEventLoop.class)
public interface BlockableEventLoopInvoker {
    @Invoker("submitAsync")
    CompletableFuture<Void> submitAsync(Runnable task);
}
