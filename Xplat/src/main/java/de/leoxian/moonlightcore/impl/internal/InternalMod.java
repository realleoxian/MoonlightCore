package de.leoxian.moonlightcore.impl.internal;

import com.google.common.base.Suppliers;
import de.leoxian.moonlightcore.api.MoonlightCore;
import de.leoxian.moonlightcore.api.network.NetworkHelper;
import de.leoxian.moonlightcore.api.registry.RegistryCreatorInitializer;
import de.leoxian.moonlightcore.api.registry.RegistryInformation;
import de.leoxian.moonlightcore.api.runtime.MoonlightCoreRuntime;
import de.leoxian.moonlightcore.impl.attachment.AttachmentInternalHooks;
import de.leoxian.moonlightcore.impl.fluid.CauldronFluidContentImpl;
import de.leoxian.moonlightcore.impl.internal.network.s2c.S2CAttachmentSyncPacket;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Supplier;

public final class InternalMod {
    public static final String MOD_ID = "moonlightcore";

    private static boolean init = false;

    public static String nbtPrefix(String tag) {
        return MOD_ID + ":" + tag;
    }

    public static ResourceLocation location(String path) {
        return new ResourceLocation(path);
    }

    public static <R> ResourceKey<Registry<R>> internalRegistryKey(String name) {
        return ResourceKey.createRegistryKey(location(name));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <R> Supplier<Registry<R>> supplyRegistry(ResourceKey<Registry<R>> key) {
        return Suppliers.memoize(() -> (Registry<R>) Objects.requireNonNull(BuiltInRegistries.REGISTRY.get((ResourceKey) key), "Registry may not be 'null'"));
    }

    public static void initialize() {
        if(init) {
            return;
        }

        MoonlightCoreRuntime.RUNTIME.registryCreator(MOD_ID, InternalMod::setupRegistries);

        NetworkHelper networkHelper = NetworkHelper.get();
        setupNetworkPackets(networkHelper.registrar(MOD_ID, NetworkHelper.HandlerThread.MAIN));

        AttachmentInternalHooks.init();
        CauldronFluidContentImpl.init();
        init = true;
    }

    private static void setupNetworkPackets(NetworkHelper.PacketRegistrar registrar) {
        registrar.clientbound(S2CAttachmentSyncPacket.TYPE, S2CAttachmentSyncPacket::handle);
    }

    private static void setupRegistries(RegistryCreatorInitializer initializer) {
        initializer.register(RegistryInformation.create(MoonlightCore.Registries.ATTACHMENT_TYPES).synced(true));
        initializer.register(RegistryInformation.create(MoonlightCore.Registries.DATA_MAP_TYPES).synced(true));
    }

}
