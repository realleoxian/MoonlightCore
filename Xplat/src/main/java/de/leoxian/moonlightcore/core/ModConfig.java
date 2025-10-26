package de.leoxian.moonlightcore.core;

import com.mojang.datafixers.util.Pair;
import de.leoxian.moonlightcore.config.ModConfigSpec;

public class ModConfig {
     public static final Common COMMON;

     static {
          Pair<ModConfigSpec, Common> specPair = ModConfigSpec.configure(MoonlightCore.MOD_ID, "common", true, Common::new);
          COMMON = specPair.getSecond();
     }

     static void init() {}

     private ModConfig() {}

     public static class Common {
          private ModConfigSpec.ValueKey<Integer> overworldBiomeZooms;
          private ModConfigSpec.ValueKey<Integer> netherBiomeZooms;
          private ModConfigSpec.ValueKey<Integer> endBiomeZooms;

          private Common(ModConfigSpec.Builder builder) {
               builder.category("world", catBuilder -> {
                 this.overworldBiomeZooms = catBuilder.comment("The number of times the overworld biomes will be expanded").defineInt("overworldBiomeZooms", () -> 3);
                 this.netherBiomeZooms = catBuilder.comment("The number of times the nether biomes will be expanded").defineInt("netherBiomeZooms", () -> 2);
                 this.endBiomeZooms = catBuilder.comment("The number of times the end biomes will be expanded").defineInt("endBiomeZooms", () -> 2);
               });
          }

          public int overworldBiomeZooms() {
               return overworldBiomeZooms.get();
          }

          public int netherBiomeZooms() {
               return netherBiomeZooms.get();
          }

          public int endBiomeZooms() {
               return endBiomeZooms.get();
          }
     }
}
