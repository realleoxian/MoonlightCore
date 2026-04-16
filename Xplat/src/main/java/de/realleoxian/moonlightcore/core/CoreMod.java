package de.realleoxian.moonlightcore.core;

import de.realleoxian.moonlightcore.api.MoonlightCore;
import de.realleoxian.moonlightcore.api.MoonlightCoreRegistries;
import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.api.registry.RegistryInformationRegistrar;
import de.realleoxian.moonlightcore.api.registry.RegistryInformation;
import de.realleoxian.moonlightcore.impl.attachment.AttachmentInternalHooks;
import de.realleoxian.moonlightcore.impl.config.ConfigTracker;
import de.realleoxian.moonlightcore.impl.fluid.CauldronFluidContentImpl;
import de.realleoxian.moonlightcore.core.network.s2c.S2CAttachmentSyncPacket;
import de.realleoxian.moonlightcore.core.network.s2c.S2CModConfigSyncPacket;

public final class CoreMod {
    private static final String MOD_ID = "moonlightcore";
    private static boolean init = false;

    public static void initialize() {
        if(init) return;

        MoonlightCore.registryInformation(MOD_ID, CoreMod::setupRegistries);
        MoonlightCore.registry(MOD_ID, MoonlightCoreRegistries::setup);
        ConfigTracker.startTracking();
        AttachmentInternalHooks.init();
        CauldronFluidContentImpl.init();
        setupNetworkPackets(NetworkHelper.get());
        init = true;
    }

    private static void setupNetworkPackets(NetworkHelper networkHelper) {
        NetworkHelper.PacketRegistrar registrar = networkHelper.handlerThread(MOD_ID, NetworkHelper.HandlerThread.MAIN).registrar(MOD_ID);

        registrar.clientbound(S2CAttachmentSyncPacket.TYPE, S2CAttachmentSyncPacket::handle);
        registrar.clientbound(S2CModConfigSyncPacket.TYPE, S2CModConfigSyncPacket::handle);
    }

    private static void setupRegistries(RegistryInformationRegistrar registrar) {
        registrar.register(RegistryInformation.create(MoonlightCoreRegistries.Keys.ATTACHMENT_TYPE).synced(true));
    }

    private CoreMod() {}
}
