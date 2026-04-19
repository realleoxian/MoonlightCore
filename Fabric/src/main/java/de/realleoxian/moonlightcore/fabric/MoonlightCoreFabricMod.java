package de.realleoxian.moonlightcore.fabric;

import de.realleoxian.moonlightcore.api.MoonlightCore;
import de.realleoxian.moonlightcore.api.event.EventResult;
import de.realleoxian.moonlightcore.api.event.LivingEntityEvents;
import de.realleoxian.moonlightcore.api.event.ServerLevelTickEvents;
import de.realleoxian.moonlightcore.api.event.ServerPlayerNetworkEvents;
import de.realleoxian.moonlightcore.api.network.PacketSender;
import de.realleoxian.moonlightcore.fabric.core.FabricCoreMod;
import de.realleoxian.moonlightcore.fabric.runtime.EmptyModLoadingRuntimeContext;
import de.realleoxian.moonlightcore.impl.runtime.XplatMoonlightCoreRuntime;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class MoonlightCoreFabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> LivingEntityEvents.HURT.invoker().onEntityHurt(entity, source, amount).isTrue());
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> LivingEntityEvents.DEATH.invoker().onEntityDeath(entity, source).isTrue());
        ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, world) -> de.realleoxian.moonlightcore.api.event.ServerBlockEntityEvents.LOAD.invoker().onBlockEntityLoad(world, blockEntity));
        ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, world) -> de.realleoxian.moonlightcore.api.event.ServerBlockEntityEvents.UNLOAD.invoker().onBlockEntityUnload(world, blockEntity));
        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> de.realleoxian.moonlightcore.api.event.ServerChunkEvents.LOAD.invoker().onChunkLoad(world, chunk));
        ServerChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> de.realleoxian.moonlightcore.api.event.ServerChunkEvents.UNLOAD.invoker().onChunkUnload(world, chunk));
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> de.realleoxian.moonlightcore.api.event.ServerEntityEvents.LOAD.invoker().onLoad(world, entity));
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> de.realleoxian.moonlightcore.api.event.ServerEntityEvents.UNLOAD.invoker().onUnload(world, entity));
        ServerLifecycleEvents.SERVER_STARTING.register((server) -> de.realleoxian.moonlightcore.api.event.ServerLifecycleEvents.STARTING.invoker().onServerStarting(server));
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> de.realleoxian.moonlightcore.api.event.ServerLifecycleEvents.STARTED.invoker().onServerStarted(server));
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> de.realleoxian.moonlightcore.api.event.ServerLifecycleEvents.STOPPING.invoker().onServerStopping(server));
        ServerLifecycleEvents.SERVER_STOPPED.register((server) -> de.realleoxian.moonlightcore.api.event.ServerLifecycleEvents.STOPPED.invoker().onServerStopped(server));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> de.realleoxian.moonlightcore.api.event.ServerPlayerEvents.AFTER_RESPAWN.invoker().onPlayerRespawn(oldPlayer, newPlayer));
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> de.realleoxian.moonlightcore.api.event.ServerPlayerEvents.CLONE.invoker().onPlayerClone(oldPlayer, newPlayer, !alive));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ServerPlayerNetworkEvents.LOGGED_IN.invoker().onPlayerLoggedIn(handler, PacketSender.ofPlayer(handler.getPlayer()), server));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> ServerPlayerNetworkEvents.LOGGED_OUT.invoker().onPlayerLoggedOut(handler, server));
        ServerTickEvents.START_SERVER_TICK.register((server) -> de.realleoxian.moonlightcore.api.event.ServerTickEvents.START.invoker().onServerTickStart(server));
        ServerTickEvents.END_SERVER_TICK.register((server) -> de.realleoxian.moonlightcore.api.event.ServerTickEvents.END.invoker().onServerTickEnd(server));
        ServerTickEvents.START_WORLD_TICK.register((level) -> ServerLevelTickEvents.START.invoker().onServerLevelTickStart(level));
        ServerTickEvents.END_WORLD_TICK.register((level) -> ServerLevelTickEvents.END.invoker().onServerLevelTickEnd(level));

        MoonlightCore.initializeMod("moonlightcore", EmptyModLoadingRuntimeContext.INSTANCE, FabricCoreMod::initialize);
        ((XplatMoonlightCoreRuntime<?>) MoonlightCore.getRuntime()).initializeRuntime();
    }
}
