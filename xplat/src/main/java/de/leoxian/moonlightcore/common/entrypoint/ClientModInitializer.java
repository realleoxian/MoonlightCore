package de.leoxian.moonlightcore.common.entrypoint;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;

public interface ClientModInitializer {
    static void initialize(String modId, Class<?> initializer) {
        XplatClientAbstraction.INSTANCE.initializeClientMod(modId, initializer);
    }

    void onInitializedClient();
}
