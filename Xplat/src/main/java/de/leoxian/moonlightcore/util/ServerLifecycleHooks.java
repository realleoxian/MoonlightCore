package de.leoxian.moonlightcore.util;

import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;

public class ServerLifecycleHooks {
    private static MinecraftServer currentServer = null;

    public static @Nullable MinecraftServer getCurrentServer() {
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
