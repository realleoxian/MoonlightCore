package de.realleoxian.moonlightcore.xplat.internal.network.task;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ConfigurationTask;

import java.util.function.Consumer;

public final class ConfigSyncTask implements ConfigurationTask {
    public static final ConfigurationTask.Type TYPE = new Type("moonlightcore:config_sync");

    @Override
    public void start(Consumer<Packet<?>> consumer) {

    }

    @Override
    public Type type() {
        return TYPE;
    }
}
