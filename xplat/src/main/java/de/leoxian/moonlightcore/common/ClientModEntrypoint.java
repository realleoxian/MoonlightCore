package de.leoxian.moonlightcore.common;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;

@FunctionalInterface
public interface ClientModEntrypoint {
    static void init(final String modId, final ClientModEntrypoint entrypoint) {
        XplatClientAbstraction.INSTANCE.initializeClientMod(modId, entrypoint);
    }

    void initializeClientMod();
}
