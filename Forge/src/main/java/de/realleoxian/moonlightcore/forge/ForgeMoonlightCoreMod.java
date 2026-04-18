package de.realleoxian.moonlightcore.forge;

import de.realleoxian.moonlightcore.api.MoonlightCore;
import de.realleoxian.moonlightcore.api.client.MoonlightCoreClient;
import de.realleoxian.moonlightcore.api.runtime.MoonlightCoreRuntime;
import de.realleoxian.moonlightcore.core.client.CoreClientMod;
import de.realleoxian.moonlightcore.forge.runtime.ForgeModLoadingContext;
import de.realleoxian.moonlightcore.core.CoreMod;
import de.realleoxian.moonlightcore.impl.runtime.XplatMoonlightCoreRuntime;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(value = "moonlightcore")
public class ForgeMoonlightCoreMod {
    public ForgeMoonlightCoreMod(FMLJavaModLoadingContext context) {
        MoonlightCore.initializeMod("moonlightcore", new ForgeModLoadingContext(context.getModEventBus()), CoreMod::initialize);
        if (FMLLoader.getDist().isClient()) MoonlightCoreClient.initializeClientMod("moonlightcore", new ForgeModLoadingContext(context.getModEventBus()), CoreClientMod::initializeClient);

        ((XplatMoonlightCoreRuntime<?>) MoonlightCore.getRuntime()).initializeRuntime();
    }
}
