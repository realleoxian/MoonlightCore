package de.realleoxian.moonlightcore.api.client.screenshake;

import de.realleoxian.moonlightcore.impl.client.screenshake.ScreenshakeHandlerImpl;

import java.util.function.Consumer;

public interface ScreenshakeHandler {
    static void addScreenshake(int durationTicks, Consumer<ScreenshakeInstance.Builder> builderModifier) {
        ScreenshakeHandlerImpl.addScreenshake(durationTicks, builderModifier);
    }
}
