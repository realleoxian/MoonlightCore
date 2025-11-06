package de.leoxian.moonlightcore.util;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class ServerLifecycleHooks {
    private static MinecraftServer currentServer = null;

    @Nullable
    public static MinecraftServer getCurrentServer() {
        return currentServer;
    }

    @ApiStatus.Internal
    public static void onServerStarting(MinecraftServer server) {
        currentServer = server;
    }

    @ApiStatus.Internal
    public static void onServerStopped(MinecraftServer server) {
        currentServer = null;
    }

    private ServerLifecycleHooks() {}
}
