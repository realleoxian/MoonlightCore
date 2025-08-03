package de.leowgc.moonlightcore.config.sync;

import de.leowgc.moonlightcore.api.config.ModConfigSpec;
import de.leowgc.moonlightcore.config.EzcConfigParser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class ConfigSyncRegistry {
    private static final Map<String, ModConfigSpec> SYNCED_SPECS = new HashMap<>();

    public static Stream<ConfigSyncPacket> createPackets() {
        return SYNCED_SPECS.values().stream().map((spec) -> new ConfigSyncPacket(spec.modId(), EzcConfigParser.writeToBytes(spec)));
    }

    public static void tryAdd(ModConfigSpec spec) {
        if(spec.side().isSynced()) {
           SYNCED_SPECS.put(spec.modId(), spec);
        }
    }

    public static Optional<ModConfigSpec> get(String modId) {
        return Optional.ofNullable(SYNCED_SPECS.get(modId));
    }
}
