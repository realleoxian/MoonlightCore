package de.leoxian.moonlightcore.fabric.mixin.impl;

import de.leoxian.moonlightcore.platform.EnvironmentSide;
import de.leoxian.moonlightcore.platform.PlatformEnvironment;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.nio.file.Path;

@Mixin(value = PlatformEnvironment.class, remap = false)
public class FabricPlatformImplMixin {

     @Overwrite
     public boolean isModLoaded(String modId) {
          return FabricLoader.getInstance().isModLoaded(modId);
     }

     @Overwrite
     public String getPlatformName() {
          return "Fabric";
     }

     @Overwrite
     public EnvironmentSide getEnvironmentSide() {
          return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? EnvironmentSide.CLIENT : EnvironmentSide.SERVER;
     }

     @Overwrite
     public Path getGameDirectory() {
          return FabricLoader.getInstance().getGameDir();
     }

     @Overwrite
     public Path getConfigDirectory() {
          return FabricLoader.getInstance().getConfigDir();
     }

     @Overwrite
     public boolean isDevelopmentEnvironment() {
          return FabricLoader.getInstance().isDevelopmentEnvironment();
     }

}
