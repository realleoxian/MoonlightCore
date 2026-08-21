package de.leoxian.moonlightcore.mixin.dimension;

import com.mojang.datafixers.DataFixer;
import de.leoxian.moonlightcore.common.network.PacketDistributor;
import de.leoxian.moonlightcore.common.server.dimension.DynamicDimensionRegistry;
import de.leoxian.moonlightcore.common.server.dimension.PlayerRemover;
import de.leoxian.moonlightcore.common.util.DynamicRegistryUtils;
import de.leoxian.moonlightcore.internal.common.mod.InternalMod;
import de.leoxian.moonlightcore.internal.common.network.s2c.S2CRemoveDimensionPacket;
import de.leoxian.moonlightcore.internal.common.server.dimension.DynamicDimensionProvider;
import de.leoxian.moonlightcore.internal.common.server.dimension.DynamicDimensionRegistryImpl;
import de.leoxian.moonlightcore.internal.common.server.dimension.DynamicDimensionRemovalTicket;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.net.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements DynamicDimensionProvider {
    @Shadow
    @Final
    protected LevelStorageSource.LevelStorageAccess storageSource;
    @Shadow
    @Final
    private Map<ResourceKey<Level>, ServerLevel> levels;

    @Shadow
    public abstract LayeredRegistryAccess<RegistryLayer> registries();

    @Shadow
    public abstract PlayerList getPlayerList();

    @Unique
    private final List<ServerLevel> moonlightcore$pendingLevels = new ArrayList<>();
    @Unique
    private final List<DynamicDimensionRemovalTicket> moonlightcore$pendingRemovalTickets = new ArrayList<>();
    @Unique
    private DynamicDimensionRegistry moonlightcore$dimensionRegistry;
    @Unique
    private boolean tickingLevels = false;

    @Inject(
            method = "<init>",
            at = @At(value = "RETURN")
    )
    private void moonlightcore$initRegistry(Thread serverThread, LevelStorageSource.LevelStorageAccess storageSource, PackRepository packRepository, WorldStem worldStem, Optional gameRules, Proxy proxy, DataFixer fixerUpper, Services services, LevelLoadListener levelLoadListener, boolean propagatesCrashes, CallbackInfo ci) {
        this.moonlightcore$dimensionRegistry = new DynamicDimensionRegistryImpl((MinecraftServer) (Object) this);
    }

    @Inject(
            method = "tickChildren",
            at = @At(value = "HEAD")
    )
    private void moonlightcore$handlePendingCreationsAndDeletions(BooleanSupplier haveTime, CallbackInfo ci) {
        if (!this.moonlightcore$pendingLevels.isEmpty()) {
            this.moonlightcore$pendingLevels.forEach(this::registerLevel);
            this.moonlightcore$pendingLevels.clear();
        }

        if (!this.moonlightcore$pendingRemovalTickets.isEmpty()) {
            for (DynamicDimensionRemovalTicket ticket : this.moonlightcore$pendingRemovalTickets) {
                this.unloadLevel(ticket.key(), ticket.playerRemover());
                if (ticket.removeFiles()) {
                    this.moonlightcore$deleteLevelData(ticket.key());
                }
            }
            this.moonlightcore$pendingRemovalTickets.clear();
        }
    }

    @Inject(
            method = "tickChildren",
            at = @At(value = "HEAD")
    )
    private void moonlightcore$markTickingLevels(BooleanSupplier haveTime, CallbackInfo ci) {
        this.tickingLevels = true;
    }

    @Inject(
            method = "tickChildren",
            at = @At(value = "RETURN")
    )
    private void moonlightcore$unmarkTickingLevels(BooleanSupplier haveTime, CallbackInfo ci) {
        this.tickingLevels = false;
    }

    @Override
    public void moonlightcore$removeLevel(ResourceKey<Level> key, @Nullable PlayerRemover playerRemover, boolean removeFiles) {
        PlayerRemover remover = playerRemover == null ? PlayerRemover.DEFAULT : playerRemover;
        if (this.tickingLevels) {
            this.moonlightcore$pendingRemovalTickets.add(new DynamicDimensionRemovalTicket(key, remover, removeFiles));
        } else {
            this.unloadLevel(key, remover);
            if (removeFiles) {
                this.moonlightcore$deleteLevelData(key);
            }
        }
    }

    @Override
    public void moonlightcore$deleteLevelData(ResourceKey<Level> key) {
        Path dimensionPath = this.storageSource.getDimensionPath(key);
        if (Files.exists(dimensionPath)) {
            try {
                FileUtils.deleteDirectory(dimensionPath.toFile());
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete deleted level directory!", e);
            }
        }
    }

    @Override
    public void moonlightcore$registerLevel(ServerLevel level) {
        if (this.tickingLevels) {
            this.moonlightcore$pendingLevels.add(level);
        } else {
            this.registerLevel(level);
        }
    }

    @Override
    public boolean moonlightcore$isPendingCreation(ServerLevel level) {
        return this.moonlightcore$pendingLevels.contains(level);
    }

    @Override
    public DynamicDimensionRegistry moonlightcore$registry() {
        return this.moonlightcore$dimensionRegistry;
    }

    @Unique
    private void registerLevel(ServerLevel level) {
        this.levels.put(level.dimension(), level);
        level.tick(() -> true);
    }

    private void unloadLevel(ResourceKey<Level> key, PlayerRemover remover) {
        Identifier dimType = null;
        try (ServerLevel level = this.levels.get(key)) {
            if (level == null) {
                InternalMod.LOGGER.error("Attempted to unload non-existing level {}", key);
                return;
            }

            List<ServerPlayer> players = List.copyOf(level.players());
            for (final ServerPlayer player : players) {
                remover.removePlayer((MinecraftServer) (Object) this, player);
            }
            level.save(null, true, level.noSave());
            dimType = level.dimensionTypeRegistration().unwrapKey().get().identifier();
        } catch (IOException e) {
            InternalMod.LOGGER.error("Failed to close level upon removal! Memory may have been leaked", e);
        }
        assert dimType != null;

        DynamicRegistryUtils.unregister(this.registries().compositeAccess().lookupOrThrow(Registries.LEVEL_STEM), key.identifier());
        DynamicRegistryUtils.unregister(this.registries().compositeAccess().lookupOrThrow(Registries.DIMENSION_TYPE), dimType);
        PacketDistributor.sendToAllPlayers(new S2CRemoveDimensionPacket(key.identifier()));
    }
}
