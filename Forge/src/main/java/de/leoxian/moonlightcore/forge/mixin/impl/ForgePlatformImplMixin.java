package de.leoxian.moonlightcore.forge.mixin.impl;

import de.leoxian.moonlightcore.platform.EnvironmentSide;
import de.leoxian.moonlightcore.platform.PlatformEnvironment;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.nio.file.Path;

@Mixin(value = PlatformEnvironment.class, remap = false)
public class ForgePlatformImplMixin {

     @Overwrite
     public boolean isModLoaded(String modId) {
          return ModList.get().isLoaded(modId);
     }

     @Overwrite
     public String getPlatformName() {
          return "Forge";
     }

     @Overwrite
     public EnvironmentSide getEnvironmentSide() {
          return FMLLoader.getDist().isClient() ? EnvironmentSide.CLIENT : EnvironmentSide.SERVER;
     }

     @Overwrite
     public Path getGameDirectory() {
          return FMLPaths.GAMEDIR.get();
     }

     @Overwrite
     public Path getConfigDirectory() {
          return FMLPaths.CONFIGDIR.get();
     }

     @Overwrite
     public boolean isDevelopmentEnvironment() {
          return !FMLLoader.isProduction();
     }

}
