package de.leoxian.moonlightcore.fabric.client;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import de.leoxian.moonlightcore.common.ClientModEntrypoint;
import de.leoxian.moonlightcore.internal.common.mod.client.InternalClientMod;
import net.fabricmc.api.ClientModInitializer;

public class MoonlightCoreClientFabricMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        XplatClientAbstraction.INSTANCE.initialize();
        ClientModEntrypoint.init("moonlightcore", InternalClientMod::initializeClientMod);
    }
}
