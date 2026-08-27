package de.leoxian.moonlightcore.common;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;

@FunctionalInterface
public interface ModEntrypoint {
    static void init(final String modId, final ModEntrypoint entrypoint) {
        XplatAbstraction.INSTANCE.initializeMod(modId, entrypoint);
    }

    void initialize();
}
