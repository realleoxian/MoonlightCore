package de.realleoxian.moonlightcore.fabric.client;

import de.realleoxian.moonlightcore.api.client.MoonlightCoreClient;
import de.realleoxian.moonlightcore.api.client.event.ClientLevelTickEvents;
import de.realleoxian.moonlightcore.api.client.event.ClientPlayerNetworkEvents;
import de.realleoxian.moonlightcore.api.network.PacketSender;
import de.realleoxian.moonlightcore.fabric.client.core.FabricClientMod;
import de.realleoxian.moonlightcore.fabric.client.model.MoonlightCoreModelLoadingPlugin;
import de.realleoxian.moonlightcore.fabric.runtime.EmptyModLoadingRuntimeContext;
import de.realleoxian.moonlightcore.impl.client.runtime.XplatMoonlightCoreClientRuntime;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class MoonlightCoreFabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(new MoonlightCoreModelLoadingPlugin());

        ClientLifecycleEvents.CLIENT_STARTED.register((mc) -> de.realleoxian.moonlightcore.api.client.event.ClientLifecycleEvents.STARTED.invoker().onClientStarted(mc));
        ClientLifecycleEvents.CLIENT_STOPPING.register((mc) -> de.realleoxian.moonlightcore.api.client.event.ClientLifecycleEvents.STOPPING.invoker().onClientStopping(mc));
        ClientChunkEvents.CHUNK_LOAD.register((level, chunk) -> de.realleoxian.moonlightcore.api.client.event.ClientChunkEvents.LOAD.invoker().onChunkLoad(level, chunk));
        ClientChunkEvents.CHUNK_UNLOAD.register((level, chunk) -> de.realleoxian.moonlightcore.api.client.event.ClientChunkEvents.UNLOAD.invoker().onChunkUnload(level, chunk));
        ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, level) -> de.realleoxian.moonlightcore.api.client.event.ClientBlockEntityEvents.LOAD.invoker().onBlockEntityLoad(level, blockEntity));
        ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, level) -> de.realleoxian.moonlightcore.api.client.event.ClientBlockEntityEvents.UNLOAD.invoker().onBlockEntityUnload(level, blockEntity));
        ClientEntityEvents.ENTITY_LOAD.register((entity, level) -> de.realleoxian.moonlightcore.api.client.event.ClientEntityEvents.LOAD.invoker().onLoad(level, entity));
        ClientEntityEvents.ENTITY_UNLOAD.register((entity, level) -> de.realleoxian.moonlightcore.api.client.event.ClientEntityEvents.UNLOAD.invoker().onUnload(level, entity));
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ClientPlayerNetworkEvents.LOGGED_IN.invoker().onPlayerLoggedIn(handler, PacketSender.client(), client));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientPlayerNetworkEvents.LOGGED_OUT.invoker().onPlayerLoggedOut(handler, client));
        ClientTickEvents.START_CLIENT_TICK.register((mc) -> de.realleoxian.moonlightcore.api.client.event.ClientTickEvents.TICK_START.invoker().onStartClientTick(mc));
        ClientTickEvents.END_CLIENT_TICK.register((mc) -> de.realleoxian.moonlightcore.api.client.event.ClientTickEvents.TICK_END.invoker().onEndClientTick(mc));
        ClientTickEvents.START_WORLD_TICK.register((level) -> ClientLevelTickEvents.START.invoker().onClientLevelTickStart(level));
        ClientTickEvents.END_WORLD_TICK.register((level) -> ClientLevelTickEvents.END.invoker().onClientLevelTickEnd(level));

        MoonlightCoreClient.initializeClientMod("moonlightcore", EmptyModLoadingRuntimeContext.INSTANCE, FabricClientMod::initializeClient);
        ((XplatMoonlightCoreClientRuntime<?>) MoonlightCoreClient.getRuntime()).initializeRuntime();
    }
}
