package de.realleoxian.moonlightcore.fabric.client;

import de.realleoxian.moonlightcore.api.client.MoonlightCoreClient;
import de.realleoxian.moonlightcore.fabric.client.core.FabricClientMod;
import de.realleoxian.moonlightcore.fabric.client.model.MoonlightCoreModelLoadingPlugin;
import de.realleoxian.moonlightcore.fabric.runtime.EmptyModLoadingRuntimeContext;
import de.realleoxian.moonlightcore.impl.client.runtime.XplatMoonlightCoreClientRuntime;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

public class MoonlightCoreFabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(new MoonlightCoreModelLoadingPlugin());

        MoonlightCoreClient.initializeClientMod("moonlightcore", EmptyModLoadingRuntimeContext.INSTANCE, FabricClientMod::initializeClient);
        ((XplatMoonlightCoreClientRuntime<?>) MoonlightCoreClient.getRuntime()).initializeRuntime();
    }
}
