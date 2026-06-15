package de.realleoxian.moonlightcore.api.ext;

import net.minecraft.server.network.ConfigurationTask;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface MoonlightCoreServerConfigurationPacketListenerExtension {
    default void moonlightcore$addTask(ConfigurationTask task) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    default void moonlightcore$completeTask(ConfigurationTask.Type type) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }
}
