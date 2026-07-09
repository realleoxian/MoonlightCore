package de.leoxian.moonlightcore.common.entrypoint;

import de.leoxian.moonlightcore.common.ModContainer;

public interface ClientModInitializer {
    void onInitializedClient(ModContainer container);
}
