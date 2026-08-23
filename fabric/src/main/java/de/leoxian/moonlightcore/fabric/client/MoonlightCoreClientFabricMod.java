package de.leoxian.moonlightcore.fabric.client;

import de.leoxian.moonlightcore.client.event.ViewportEvents;
import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import de.leoxian.moonlightcore.common.event.base.EventResult;
import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import de.leoxian.moonlightcore.internal.common.mod.client.InternalClientMod;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Mth;

public class MoonlightCoreClientFabricMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        XplatClientAbstraction.INSTANCE.initialize();
        XplatClientAbstraction.INSTANCE.initializeClientMod("moonlightcore", InternalClientMod.class);
    }
}
