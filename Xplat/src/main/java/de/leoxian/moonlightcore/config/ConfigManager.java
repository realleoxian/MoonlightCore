package de.leoxian.moonlightcore.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import de.leoxian.moonlightcore.core.network.clientbound.S2CConfigSyncRequestPacket;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ConfigManager {
    private static final Map<String, Map<String, ModConfigSpec>> CONFIGURATION_SPECS = new HashMap<>();

    static void registerSpec(ModConfigSpec spec) {
        if (CONFIGURATION_SPECS.computeIfAbsent(spec.getModId(), $ -> new HashMap<>()).putIfAbsent(spec.getFilename(), spec) != null) {
            throw new IllegalStateException("Duplicated spec filename (%s) on mod %s".formatted(spec.getFilename(), spec.getModId()));
        }
    }

    @Nullable
    public static ModConfigSpec getSpec(String modId, String filename) {
        if(!CONFIGURATION_SPECS.containsKey(modId)) {
            return null;
        }

        return CONFIGURATION_SPECS.get(modId).get(filename);
    }

    public static boolean hasSpec(String modId, String filename) {
        ModConfigSpec spec = getSpec(modId, filename);
        return spec != null;
    }

    public static ModConfigSpec getSyncedSpec(String modId, String fileName) {
        ModConfigSpec spec = getSpec(modId, fileName);
        return (spec == null || !spec.isSync()) ? null : spec;
    }

    public static boolean isSyncedSpec(String modId, String filename) {
        ModConfigSpec spec = getSpec(modId, filename);
        return spec != null && spec.isSync();
    }

    @ApiStatus.Internal
    public static List<S2CConfigSyncRequestPacket> createSyncPackets() {
        ImmutableList.Builder<S2CConfigSyncRequestPacket> packets = ImmutableList.builder();

        for(ModConfigSpec spec : getSyncedSpecs()) {
            packets.add(new S2CConfigSyncRequestPacket(spec.getModId(), spec.getFilename()));
        }

        return packets.build();
    }

    public static Set<ModConfigSpec> getSyncedSpecs() {
        ImmutableSet.Builder<ModConfigSpec> specList = ImmutableSet.builder();

        for(String modId : CONFIGURATION_SPECS.keySet()) {
            specList.addAll(CONFIGURATION_SPECS.get(modId).values().stream().filter(ModConfigSpec::isSync).toList());
        }

        return specList.build();
    }
}
