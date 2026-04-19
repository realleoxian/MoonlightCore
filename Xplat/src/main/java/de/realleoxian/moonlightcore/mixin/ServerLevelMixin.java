package de.realleoxian.moonlightcore.mixin;

import de.realleoxian.moonlightcore.api.attachment.AttachmentHolder;
import de.realleoxian.moonlightcore.api.attachment.AttachmentMap;
import de.realleoxian.moonlightcore.api.attachment.AttachmentsHolderInfo;
import de.realleoxian.moonlightcore.impl.apilookup.BlockApiCacheImpl;
import de.realleoxian.moonlightcore.impl.apilookup.ServerLevelApiLookupCache;
import de.realleoxian.moonlightcore.impl.attachment.AttachmentMapImpl;
import de.realleoxian.moonlightcore.impl.attachment.AttachmentPersistentData;
import it.unimi.dsi.fastutil.objects.Object2ReferenceArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements ServerLevelApiLookupCache, AttachmentHolder {
    @Unique private final Map<BlockPos, List<WeakReference<BlockApiCacheImpl<?, ?>>>> mlcore$blockApiLookupCaches = new Object2ReferenceArrayMap<>();
    @Unique private int mlcore$lookupAccessedWithoutCleanup = 0;
    @Unique private AttachmentMap moonlightcore$attachmentsMap = null;

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    public void moonlightcore$createAttachmentsPersistentData(MinecraftServer server, Executor dispatcher, LevelStorageSource.LevelStorageAccess levelStorageAccess, ServerLevelData serverLevelData, ResourceKey dimension, LevelStem levelStem, ChunkProgressListener progressListener, boolean isDebug, long biomeZoomSeed, List customSpawners, boolean tickTime, RandomSequences randomSequences, CallbackInfo ci) {
        ServerLevel self = (ServerLevel) (Object) this;
        self.getDataStorage().computeIfAbsent((t) -> AttachmentPersistentData.read(self, t), () -> new AttachmentPersistentData(self), AttachmentPersistentData.ID);
    }

    @Override
    public void mlcore$registerBlockCache(BlockPos blockPos, BlockApiCacheImpl<?, ?> cache) {
        List<WeakReference<BlockApiCacheImpl<?, ?>>> caches = mlcore$blockApiLookupCaches.computeIfAbsent(blockPos.immutable(), ignored -> new ArrayList<>());

        caches.removeIf(reference -> reference.get() == null);
        caches.add(new WeakReference<>(cache));
        mlcore$lookupAccessedWithoutCleanup++;
    }

    @Override
    public void mlcore$invalidateBlockCache(BlockPos blockPos) {
        List<WeakReference<BlockApiCacheImpl<?, ?>>> caches = mlcore$blockApiLookupCaches.get(blockPos);
        if(caches != null) {
            caches.removeIf(reference -> reference.get() == null);

            if(caches.isEmpty()) {
                mlcore$blockApiLookupCaches.remove(blockPos);
            } else {
                caches.forEach(reference -> {
                    BlockApiCacheImpl<?, ?> cache = reference.get();

                    if(cache != null) {
                        cache.invalidate();
                    }
                });
            }
        }

        mlcore$lookupAccessedWithoutCleanup++;
        if(mlcore$lookupAccessedWithoutCleanup > 2 * mlcore$blockApiLookupCaches.size()) {
            mlcore$blockApiLookupCaches.entrySet().removeIf(entry -> {
               entry.getValue().removeIf(reference -> reference.get() == null);
               return entry.getValue().isEmpty();
            });
            mlcore$lookupAccessedWithoutCleanup = 0;
        }
    }

    @Override
    public AttachmentMap getAttachmentsMap() {
        if (this.moonlightcore$attachmentsMap == null) this.moonlightcore$attachmentsMap = AttachmentMapImpl.create(AttachmentsHolderInfo.LevelHolderInfo.INSTANCE);
        return this.moonlightcore$attachmentsMap;
    }
}
