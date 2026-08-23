package de.leoxian.moonlightcore.fabric.client.event;

import de.leoxian.moonlightcore.client.event.ClientLevelTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class ClientEventHooks {
    public static void bindFabricApiEvents() {
        ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, level) ->
                de.leoxian.moonlightcore.client.event.ClientBlockEntityEvents.LOAD.doFire().onBlockEntityLoad(level, blockEntity));
        ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, level) ->
                de.leoxian.moonlightcore.client.event.ClientBlockEntityEvents.UNLOAD.doFire().onBlockEntityUnload(level, blockEntity));

        ClientChunkEvents.CHUNK_LOAD.register((level, chunk) ->
                de.leoxian.moonlightcore.client.event.ClientChunkEvents.LOAD.doFire().onChunkLoad(level, chunk));
        ClientChunkEvents.CHUNK_UNLOAD.register((level, chunk) ->
                de.leoxian.moonlightcore.client.event.ClientChunkEvents.UNLOAD.doFire().onChunkUnload(level, chunk));

        ClientEntityEvents.ENTITY_LOAD.register((entity, level) ->
                de.leoxian.moonlightcore.client.event.ClientEntityEvents.LOAD.doFire().onEntityLoad(level, entity));
        ClientEntityEvents.ENTITY_UNLOAD.register((entity, level) ->
                de.leoxian.moonlightcore.client.event.ClientEntityEvents.UNLOAD.doFire().onEntityUnload(level, entity));

        ClientTickEvents.START_LEVEL_TICK.register((level) ->
                ClientLevelTickEvents.START.doFire().onLevelTickStart(level));
        ClientTickEvents.END_LEVEL_TICK.register((level) ->
                ClientLevelTickEvents.END.doFire().onLevelTickEnd(level));
        ClientTickEvents.START_CLIENT_TICK.register((level) ->
                de.leoxian.moonlightcore.client.event.ClientTickEvents.START.doFire().onClientTickStart(level));
        ClientTickEvents.END_CLIENT_TICK.register((level) ->
                de.leoxian.moonlightcore.client.event.ClientTickEvents.END.doFire().onClientTickEnd(level));

        ClientLifecycleEvents.CLIENT_STARTED.register((client) ->
                de.leoxian.moonlightcore.client.event.ClientLifecycleEvents.STARTED.doFire().onClientStarted(client));
        ClientLifecycleEvents.CLIENT_STOPPING.register((client) ->
                de.leoxian.moonlightcore.client.event.ClientLifecycleEvents.STOPPING.doFire().onClientStopping(client));

        ClientPlayConnectionEvents.JOIN.register((listener, $, client) ->
                de.leoxian.moonlightcore.client.event.ClientPlayConnectionEvents.JOIN.doFire().onPlayJoin(listener, client));
        ClientPlayConnectionEvents.DISCONNECT.register((listener, client) ->
                de.leoxian.moonlightcore.client.event.ClientPlayConnectionEvents.DISCONNECT.doFire().onPlayDisconnect(listener, client));
    }
}
