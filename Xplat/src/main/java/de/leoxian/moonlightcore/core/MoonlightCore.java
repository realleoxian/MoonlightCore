package de.leoxian.moonlightcore.core;

import com.google.common.collect.ImmutableList;
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
import de.leoxian.moonlightcore.levelgen.*;
import de.leoxian.moonlightcore.levelgen.biome.BiomeProvider;
import de.leoxian.moonlightcore.levelgen.biome.BiomeProviderRegistry;
import de.leoxian.moonlightcore.lookup.entity.EntityApiLookup;
import de.leoxian.moonlightcore.mixin.accessor.MultiNoiseBiomeSourceAccessor;
import de.leoxian.moonlightcore.util.MoonlightRegistries;
import de.leoxian.moonlightcore.util.ServerLifecycleHooks;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MoonlightCore {
    public static final Logger LOGGER = LoggerFactory.getLogger(MoonlightCore.class);

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
        PlayerEvent.JOIN_SERVER.subscribe(MoonlightCore::onPlayerJoin);
        ServerLifecycleEvent.STARTING.subscribe(MoonlightCore::onServerStarting);
        ServerLifecycleEvent.STOPPED.subscribe(ServerLifecycleHooks::onServerStopped);

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

    private static void onPlayerJoin(ServerPlayer player, MinecraftServer server) {
        if(server.isSingleplayer()) {
            for(ModConfigSpec spec : ConfigManager.getSyncedSpecs()) {
                ConfigSerializer.readFromFile(spec);
            }
        } else {
            ConfigManager.createSyncPackets().forEach((packet) -> PACKET_DISPATCHER.sendToPlayer(player, packet));
        }
    }

    private static void onServerStarting(MinecraftServer server) {
        RegistryAccess registryAccess = server.registryAccess();
        Registry<LevelStem> levelStemRegistry = registryAccess.registryOrThrow(Registries.LEVEL_STEM);
        long seed = server.getWorldData().worldGenOptions().seed();

        for(var entry : levelStemRegistry.entrySet()) {
            LevelStem stem = entry.getValue();
            setupLevelGeneration(registryAccess, entry.getKey(), stem.generator(), stem.generator().getBiomeSource(), seed);
        }

        ServerLifecycleHooks.onServerStarting(server);
        EntityApiLookup.checkSelfImplementingTypes(server);
    }

    private static void setupLevelGeneration(RegistryAccess registryAccess, ResourceKey<LevelStem> levelKey, ChunkGenerator chunkGenerator, BiomeSource biomeSource, long seed) {
        if(!(chunkGenerator instanceof NoiseBasedChunkGenerator noiseBasedChunkGenerator)) {
            return;
        }

        NoiseGeneratorSettings generatorSettings = noiseBasedChunkGenerator.generatorSettings().value();
        if(biomeSource instanceof TheEndBiomeSource endSource) {
            ((TheEndBiomeSourceExtension) endSource).mlcore_initialize(registryAccess, seed);
            ((NoiseGeneratorSettingsExtension) (Object) generatorSettings).mlcore_setDimension(SurfaceRuleRegistry.Dimension.THE_END);

            return;
        } else if(!(biomeSource instanceof MultiNoiseBiomeSource)) {
            return;
        }

        BiomeProviderRegistry.Dimension biomeDimension = null;
        SurfaceRuleRegistry.Dimension surfaceDimension = null;
        if(levelKey == LevelStem.OVERWORLD) {
            biomeDimension = BiomeProviderRegistry.Dimension.OVERWORLD;
            surfaceDimension = SurfaceRuleRegistry.Dimension.OVERWORLD;
        } else if(levelKey == LevelStem.NETHER) {
            biomeDimension = BiomeProviderRegistry.Dimension.NETHER;
            surfaceDimension = SurfaceRuleRegistry.Dimension.NETHER;
        }

        if(biomeDimension == null || surfaceDimension == null) {
            return;
        }

        Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);
        MultiNoiseBiomeSource multiNoiseBiomeSource = (MultiNoiseBiomeSource) biomeSource;
        Climate.ParameterList<Holder<Biome>> parameters = ((MultiNoiseBiomeSourceAccessor) multiNoiseBiomeSource).invokeParameters();
        BiomeProviderRegistry registry = BiomeProviderRegistry.get(biomeDimension);

        ((NoiseGeneratorSettingsExtension) (Object) generatorSettings).mlcore_setDimension(surfaceDimension);
        ((ParameterListExtension<Holder<Biome>>) parameters).mlcore_initialize(registryAccess, biomeDimension, seed);

        ImmutableList.Builder<Holder<Biome>> moddedBiomes = ImmutableList.builder();

        for(ResourceLocation name : registry.keys()) {
            BiomeProvider provider = registry.get(name);
            if(provider == null) {
                continue;
            }

            provider.bootstrap((key, point) -> {
                if(biomeRegistry.containsKey(key)) {
                    moddedBiomes.add(biomeRegistry.getHolderOrThrow(key));
                }
            });
        }

        ((BiomeSourceExtension) multiNoiseBiomeSource).mlcore_mergeBiomes(moddedBiomes.build());
    }

     public static ResourceLocation location(String location) {
          return new ResourceLocation(MOD_ID, location);
     }

    public static String nbt(String nbt) {
        return MOD_ID + ":" + nbt;
    }
}
