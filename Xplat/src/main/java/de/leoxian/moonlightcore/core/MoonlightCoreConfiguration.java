package de.leoxian.moonlightcore.core;

import de.leoxian.moonlightcore.api.config.ModConfigSpec;

public final class MoonlightCoreConfiguration {

    public static final Common COMMON = ModConfigSpec.build(Common::new);

    static void init() {}

    private MoonlightCoreConfiguration() {}

    public static final class Common {

        @SuppressWarnings("FieldCanBeLocal")
        private final ModConfigSpec spec;

        private final ModConfigSpec.ConfigCategory worldGeneration;

        public Common(ModConfigSpec.Builder builder) {
            builder.pushCategory("worldGeneration", this::worldGenCategory);

            this.spec = builder.build(MoonlightCore.MOD_ID, ModConfigSpec.Side.COMMON);

            this.worldGeneration = spec.getCategoryOrThrow("worldGeneration");
        }

        private void worldGenCategory(ModConfigSpec.ConfigCategory.Builder builder) {
            builder.description("The number of times the overworld biomes will be zoomed, these zooms makes the biomes bigger or smaller.");
            builder.defineInt("overworldBiomeZooms", () -> 3);

            builder.description("The number of times the nether biomes will be zoomed, these zooms makes the biomes bigger or smaller.");
            builder.defineInt("netherBiomeZooms", () -> 2);

            builder.description("The number of times the end biomes will be zoomed, these zooms makes the biomes bigger or smaller.");
            builder.defineInt("endBiomeZooms", () -> 2);
        }

        // ------------------------------------------------------------------------------------------------------------------------

        public int overworldBiomeZooms() {
            return this.worldGeneration.<Integer>getUncheckedKeyOrThrow("overworldBiomeZooms").get();
        }

        public int netherBiomeZooms() {
            return this.worldGeneration.<Integer>getUncheckedKeyOrThrow("netherBiomeZooms").get();
        }

        public int endBiomeZooms() {
            return this.worldGeneration.<Integer>getUncheckedKeyOrThrow("endBiomeZooms").get();
        }

        // ------------------------------------------------------------------------------------------------------------------------

    }
}
