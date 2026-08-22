package de.leoxian.moonlightcore.common.entrypoint;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;

public interface ModInitializer {
    static void initializeMod(String modId, Class<?> initializer) {
        XplatAbstraction.INSTANCE.initializeMod(modId, initializer);
    }

    void onInitialized();
}
