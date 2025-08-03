package de.leowgc.moonlightcore.forge.mixin;

import de.leowgc.moonlightcore.util.LoaderEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.nio.file.Path;

@SuppressWarnings("all")
@Mixin(value = LoaderEnvironment.class, remap = false)
public class LoaderEnvironmentImpl {

    @Overwrite
    public static boolean isDevEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Overwrite
    public static Path getConfigPath() {
        return FMLPaths.CONFIGDIR.get();
    }

}
