package de.leoxian.moonlightcore.common.entrypoint;

import de.leoxian.moonlightcore.common.ModContainer;

public interface ModInitializer {
    void onInitialized(ModContainer container);
}
