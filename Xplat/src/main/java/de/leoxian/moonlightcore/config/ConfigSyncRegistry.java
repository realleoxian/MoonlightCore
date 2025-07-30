package de.leoxian.moonlightcore.config;

import de.leoxian.moonlightcore.api.config.ModConfigSpec;

import java.util.HashMap;
import java.util.Map;

public final class ConfigSyncRegistry {
    private static final Map<String, ModConfigSpec> SYNCED_SPECS = new HashMap<>();

    static void tryAdd(ModConfigSpec spec) {
        if(spec.side().isSynced()) {
           SYNCED_SPECS.put(spec.modId(), spec);
        }
    }
}
