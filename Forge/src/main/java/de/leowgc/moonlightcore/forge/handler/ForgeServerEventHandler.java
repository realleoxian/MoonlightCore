package de.leowgc.moonlightcore.forge.handler;

import de.leowgc.moonlightcore.api.event.EventDispatcher;
import de.leowgc.moonlightcore.api.event.server.ServerLifecycleEvent;
import de.leowgc.moonlightcore.api.event.server.ServerTickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER)
public final class ForgeServerEventHandler {

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        switch (event.phase) {
            case START -> EventDispatcher.INSTANCE.fire(ServerTickEvent.SERVER_TICK, (listener) -> listener.bootstrap(event.getServer(), ServerTickEvent.Phase.START));
            case END -> EventDispatcher.INSTANCE.fire(ServerTickEvent.SERVER_TICK, (listener) -> listener.bootstrap(event.getServer(), ServerTickEvent.Phase.END));
        }
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        EventDispatcher.INSTANCE.fire(ServerLifecycleEvent.STARTING, (listener) -> listener.bootstrap(event.getServer()));
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartingEvent event) {
        EventDispatcher.INSTANCE.fire(ServerLifecycleEvent.STARTED, (listener) -> listener.bootstrap(event.getServer()));
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStoppingEvent event) {
        EventDispatcher.INSTANCE.fire(ServerLifecycleEvent.STOPPING, (listener) -> listener.bootstrap(event.getServer()));
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStoppedEvent event) {
        EventDispatcher.INSTANCE.fire(ServerLifecycleEvent.STOPPED, (listener) -> listener.bootstrap(event.getServer()));
    }

}
