package de.realleoxian.moonlightcore.mixin;

import de.realleoxian.moonlightcore.impl.apilookup.BlockApiCacheImpl;
import de.realleoxian.moonlightcore.impl.apilookup.ServerLevelApiLookupCache;
import it.unimi.dsi.fastutil.objects.Object2ReferenceArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements ServerLevelApiLookupCache {
    @Unique private final Map<BlockPos, List<WeakReference<BlockApiCacheImpl<?, ?>>>> mlcore$blockApiLookupCaches = new Object2ReferenceArrayMap<>();
    @Unique private int mlcore$lookupAccessedWithoutCleanup = 0;

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
}
