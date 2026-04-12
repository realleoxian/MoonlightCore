package de.realleoxian.moonlightcore.impl.internal;

import de.realleoxian.moonlightcore.api.MoonlightCore;
import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.api.registry.RegistryInformationRegistrar;
import de.realleoxian.moonlightcore.api.registry.RegistryInformation;
import de.realleoxian.moonlightcore.impl.attachment.AttachmentInternalHooks;
import de.realleoxian.moonlightcore.impl.config.ConfigTracker;
import de.realleoxian.moonlightcore.impl.fluid.CauldronFluidContentImpl;
import de.realleoxian.moonlightcore.impl.internal.network.s2c.S2CAttachmentSyncPacket;
import de.realleoxian.moonlightcore.impl.internal.network.s2c.S2CModConfigSyncPacket;

public final class InternalMod {
    private static final String MOD_ID = "moonlightcore";
    private static boolean init = false;

    public static void initialize() { // TODO: Invoke this
        if(init) return;

        MoonlightCore.registryInformation(MOD_ID, InternalMod::setupRegistries);
        setupNetworkPackets(NetworkHelper.get());
        AttachmentInternalHooks.init();
        CauldronFluidContentImpl.init();
        ConfigTracker.startTracking();
        init = true;
    }

    private static void setupNetworkPackets(NetworkHelper networkHelper) {
        NetworkHelper.PacketRegistrar registrar = networkHelper.registrar(MOD_ID, NetworkHelper.HandlerThread.MAIN);

        registrar.clientbound(S2CAttachmentSyncPacket.TYPE, S2CAttachmentSyncPacket::handle);
        registrar.clientbound(S2CModConfigSyncPacket.TYPE, S2CModConfigSyncPacket::handle);
    }

    private static void setupRegistries(RegistryInformationRegistrar registrar) {
        registrar.register(RegistryInformation.create(MoonlightCore.Registries.ATTACHMENT_TYPE).synced(true));
    }

    private InternalMod() {}
}
