package de.leoxian.moonlightcore.forge;

import de.leoxian.moonlightcore.platform.EnvironmentSide;
import de.leoxian.moonlightcore.platform.PlatformEnvironment;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ForgePlatformEnvironment implements PlatformEnvironment {
    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public String getPlatformName() {
        return "forge";
    }

    @Override
    public EnvironmentSide getEnvironmentSide() {
        return FMLLoader.getDist().isClient() ? EnvironmentSide.CLIENT : EnvironmentSide.SERVER;
    }

    @Override
    public Path getGameDirectory() {
        return FMLLoader.getGamePath();
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }
}
