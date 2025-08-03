package de.leowgc.moonlightcore.core;

import de.leowgc.moonlightcore.api.config.ModConfigSpec;

public final class MoonlightCoreConfiguration {

    public static final Common COMMON = ModConfigSpec.build(Common::new);
    public static final Server SERVER = ModConfigSpec.build(Server::new);

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
            return this.worldGeneration.<Integer>getValue("overworldBiomeZooms");
        }

        public int netherBiomeZooms() {
            return this.worldGeneration.<Integer>getValue("netherBiomeZooms");
        }

        public int endBiomeZooms() {
            return this.worldGeneration.<Integer>getValue("endBiomeZooms");
        }

        // ------------------------------------------------------------------------------------------------------------------------

    }

    public static final class Server {

        private final ModConfigSpec spec;

        private final ModConfigSpec.ConfigCategory root;

        public Server(ModConfigSpec.Builder builder) {
            builder.pushCategory("root", this::syncCategory);

            this.spec = builder.build(MoonlightCore.MOD_ID, ModConfigSpec.Side.SERVER);
            this.root = this.spec.getCategoryOrThrow("root");
        }

        private void syncCategory(ModConfigSpec.ConfigCategory.Builder builder) {
            builder.description("Define the range of blocks were the mod syncs");
            builder.defineInt("syncRange", (val) -> val >= 32,  () -> 32);
        }

        public int syncRange() {
            return this.root.getValue("syncRange");
        }
    }
}
