package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.internal.common.world.storage.GlobalStorageManager;
import de.leoxian.moonlightcore.internal.core.MoonlightCore;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.SavedDataStorage;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Shadow
    public abstract SavedDataStorage getDataStorage();

    @Inject(
            method = "<init>",
            at = @At(value = "RETURN")
    )
    private void moonlightcore$init(MinecraftServer server, Executor executor, LevelStorageSource.LevelStorageAccess levelStorage, ServerLevelData levelData, ResourceKey<Level> dimension, LevelStem levelStem, boolean isDebug, long biomeZoomSeed, List<CustomSpawner> customSpawners, boolean tickTime, CallbackInfo ci) {
        if (dimension == Level.OVERWORLD) {
            SavedDataType<GlobalStorageManager> type = new SavedDataType<>(
                    MoonlightCore.id("storage"),
                    GlobalStorageManager::new,
                    GlobalStorageManager.makeCodec(server),
                    (DataFixTypes) null
            );

            getDataStorage().computeIfAbsent(type);
        }
    }
}
