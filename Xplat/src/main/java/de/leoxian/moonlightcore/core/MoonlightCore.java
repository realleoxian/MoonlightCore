package de.leoxian.moonlightcore.core;

import com.mojang.logging.LogUtils;
import de.leoxian.moonlightcore.attachment.AttachmentHolder;
import de.leoxian.moonlightcore.attachment.AttachmentHolderImpl;
import de.leoxian.moonlightcore.attachment.AttachmentInternals;
import de.leoxian.moonlightcore.attachment.sync.AttachmentChange;
import de.leoxian.moonlightcore.config.ConfigManager;
import de.leoxian.moonlightcore.config.ConfigSerializer;
import de.leoxian.moonlightcore.config.ModConfigSpec;
import de.leoxian.moonlightcore.core.network.ModPacketDispatcher;
import de.leoxian.moonlightcore.event.common.ChunkEvent;
import de.leoxian.moonlightcore.event.common.PlayerEvent;
import de.leoxian.moonlightcore.event.common.RegistryCreationEvent;
import de.leoxian.moonlightcore.event.common.ServerLifecycleEvent;
import de.leoxian.moonlightcore.lookup.entity.EntityApiLookup;
import de.leoxian.moonlightcore.util.MoonlightRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class MoonlightCore {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String MOD_ID = "moonlightcore";
    public static final String MOD_NAME = "Moonlight Core";
    public static final ModPacketDispatcher PACKET_DISPATCHER = new ModPacketDispatcher();

    private static boolean init = false;

    public static void initialize() {
        if(init) {
            return;
        }
        ModConfig.init();
        setupAttachments();

        RegistryCreationEvent.EVENT.subscribe(MoonlightCore::setupModRegistries);
        PlayerEvent.JOIN_SERVER.subscribe(MoonlightCore::setupConfigSync);
        ServerLifecycleEvent.STARTING.subscribe(EntityApiLookup::checkSelfImplementingTypes);

        init = true;
    }

    private static void setupAttachments() {
        ChunkEvent.SAVE.subscribe((chunkAccess, level, nbt) -> ((AttachmentHolderImpl) chunkAccess).mlcore_writePersistentAttachments(nbt));
        ChunkEvent.LOAD.subscribe((chunkAccess, level, nbt) -> ((AttachmentHolderImpl) chunkAccess).mlcore_readPersistentAttachments(nbt));

        PlayerEvent.AFTER_RESPAWN.subscribe((oldPlayer, newPlayer, alive) -> AttachmentInternals.transfer((AttachmentHolder) oldPlayer, (AttachmentHolder) newPlayer, !alive));

        // Sync

        PlayerEvent.JOIN_SERVER.subscribe((player, server) -> {
            List<AttachmentChange> changes = new ArrayList<>();

            ((AttachmentHolderImpl) player.level()).mlcore_computeInitialAttachmentChanges(player, changes::add);
            ((AttachmentHolderImpl) player).mlcore_computeInitialAttachmentChanges(player, changes::add);

            if(!changes.isEmpty()) {
                AttachmentChange.partitionAndSendPacket(player, changes);
            }
        });

        PlayerEvent.CHANGE_DIMENSION.subscribe((player, oldLevel, newLevel) -> {
            List<AttachmentChange> changes = new ArrayList<>();
            ((AttachmentHolderImpl) player.server.getLevel(newLevel)).mlcore_computeInitialAttachmentChanges(player, changes::add);

            if(!changes.isEmpty()) {
                AttachmentChange.partitionAndSendPacket(player, changes);
            }
        });
    }

    private static void setupModRegistries(RegistryCreationEvent.Output output) {
        output.register(MoonlightRegistries.ATTACHMENT_TYPE);
        output.register(MoonlightRegistries.SYNCED_ATTACHMENT_TYPE, true);
    }

    private static void setupConfigSync(ServerPlayer player, MinecraftServer server) {
        if(server.isSingleplayer()) {
            for(ModConfigSpec spec : ConfigManager.getSyncedSpecs()) {
                ConfigSerializer.readFromFile(spec);
            }
        } else {
            ConfigManager.createSyncPackets().forEach((packet) -> PACKET_DISPATCHER.sendToPlayer(player, packet));
        }
    }

     public static ResourceLocation location(String location) {
          return new ResourceLocation(MOD_ID, location);
     }

    public static String nbt(String nbt) {
        return MOD_ID + ":" + nbt;
    }
}
