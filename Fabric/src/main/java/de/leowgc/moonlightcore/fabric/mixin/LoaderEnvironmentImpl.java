package de.leowgc.moonlightcore.fabric.mixin;

import de.leowgc.moonlightcore.util.LoaderEnvironment;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.nio.file.Path;

@SuppressWarnings("all")
@Mixin(value = LoaderEnvironment.class, remap = false)
public class LoaderEnvironmentImpl {

    @Overwrite
    public static boolean isDevEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Overwrite
    public static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir();
    }
    
}
