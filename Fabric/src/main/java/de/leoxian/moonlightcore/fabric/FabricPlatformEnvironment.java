package de.leoxian.moonlightcore.fabric;

import de.leoxian.moonlightcore.platform.EnvironmentSide;
import de.leoxian.moonlightcore.platform.PlatformEnvironment;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FabricPlatformEnvironment implements PlatformEnvironment {
    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public String getPlatformName() {
        return "fabric";
    }

    @Override
    public EnvironmentSide getEnvironmentSide() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? EnvironmentSide.CLIENT : EnvironmentSide.SERVER;
    }

    @Override
    public Path getGameDirectory() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
