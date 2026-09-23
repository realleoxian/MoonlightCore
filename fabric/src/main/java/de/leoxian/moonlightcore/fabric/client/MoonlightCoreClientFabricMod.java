package de.leoxian.moonlightcore.fabric.client;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import de.leoxian.moonlightcore.fabric.api.MoonlightCoreInitializer;
import de.leoxian.moonlightcore.internal.common.core.client.MoonlightCoreClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class MoonlightCoreClientFabricMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        XplatClientAbstraction.INSTANCE.initialize();
        MoonlightCoreClient.initializeClientMod();

        FabricLoader.getInstance().getEntrypointContainers("moonlightcore", MoonlightCoreInitializer.class).forEach(entrypoint -> {
            MoonlightCoreInitializer initializer = entrypoint.getEntrypoint();
            initializer.onInitializedClient();
        });
    }
}
