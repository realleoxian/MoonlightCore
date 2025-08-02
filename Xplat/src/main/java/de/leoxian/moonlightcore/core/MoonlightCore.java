package de.leoxian.moonlightcore.core;

import com.mojang.logging.LogUtils;
import de.leoxian.moonlightcore.api.event.common.RegistryEvents;
import de.leoxian.moonlightcore.api.event.server.ServerLifecycleEvent;
import de.leoxian.moonlightcore.api.event.server.ServerPlayerEvents;
import de.leoxian.moonlightcore.config.sync.ConfigSyncRegistry;
import de.leoxian.moonlightcore.world.biome.BiomeApi;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;

public final class MoonlightCore {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "moonlightcore";

    public static final MooonlightCorePacketDispatcher PACKET_DISPATCHER = new MooonlightCorePacketDispatcher();

    public static void init() {
        ServerLifecycleEvent.STARTING.subscribe(BiomeApi::setupBiomeApi);
        ServerPlayerEvents.JOIN_SERVER.subscribe((server, player) -> ConfigSyncRegistry.createPackets().forEach((packet) -> PACKET_DISPATCHER.sendToPlayer(player, packet)));

        RegistryEvents.REGISTER.subscribe((currentRegistry, output) -> {
            if(currentRegistry == Registries.ITEM) {
                output.register(Registries.ITEM, prefix("test"), () -> new Item(new Item.Properties()));
            }
        });

        MoonlightCoreConfiguration.init();
    }

    public static ResourceLocation prefix(String location) {
        return new ResourceLocation(MOD_ID, location);
    }

    private MoonlightCore() {}
}
