package de.leoxian.moonlightcore.core;

import com.mojang.logging.LogUtils;
import de.leoxian.moonlightcore.api.config.ModConfigSpec;
import de.leoxian.moonlightcore.api.event.server.ServerPlayerEvents;
import de.leoxian.moonlightcore.config.sync.ConfigSyncRegistry;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public final class MoonlightCore {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "moonlightcore";

    public static final ModPacketDispatcher PACKET_DISPATCHER = new ModPacketDispatcher();
    public static final ModConfig CONFIG = ModConfigSpec.build(ModConfig::new);

    public static void init() {
        ServerPlayerEvents.JOIN_SERVER.subscribe((server, player) -> ConfigSyncRegistry.createPackets().forEach((packet) -> PACKET_DISPATCHER.sendToPlayer(player, packet)));

        LOGGER.info("subCategoryInt: {}", CONFIG.subCategoryInt());
        LOGGER.info("testBool: {}", CONFIG.testBool());
    }

    public static ResourceLocation prefix(String location) {
        return new ResourceLocation(MOD_ID, location);
    }

    private MoonlightCore() {}
}
