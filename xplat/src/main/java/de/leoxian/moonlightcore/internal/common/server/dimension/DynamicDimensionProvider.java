package de.leoxian.moonlightcore.internal.common.server.dimension;

import de.leoxian.moonlightcore.common.server.dimension.DynamicDimensionRegistry;
import de.leoxian.moonlightcore.common.server.dimension.PlayerRemover;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface DynamicDimensionProvider {
    void moonlightcore$removeLevel(ResourceKey<Level> key, @Nullable PlayerRemover playerRemover, boolean removeFiles);

    void moonlightcore$deleteLevelData(ResourceKey<Level> key);

    void moonlightcore$registerLevel(ServerLevel level);

    boolean moonlightcore$isPendingCreation(ServerLevel level);

    DynamicDimensionRegistry moonlightcore$registry();
}
