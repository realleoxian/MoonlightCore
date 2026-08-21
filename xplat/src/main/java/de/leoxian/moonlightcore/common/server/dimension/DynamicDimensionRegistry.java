package de.leoxian.moonlightcore.common.server.dimension;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import de.leoxian.moonlightcore.internal.common.server.dimension.DynamicDimensionProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import org.jspecify.annotations.Nullable;

public interface DynamicDimensionRegistry {
    static @Nullable DynamicDimensionRegistry get() {
        MinecraftServer current = XplatAbstraction.INSTANCE.getCurrentServer();
        if (current == null) {
            return null;
        }
        return ((DynamicDimensionProvider) current).moonlightcore$registry();
    }

    ServerLevel createDynamicDimension(Identifier id, ChunkGenerator chunkGenerator, DimensionType dimensionType);

    void unloadDynamicDimension(ServerLevel level, @Nullable PlayerRemover playerRemover);

    void deleteDynamicDimension(ServerLevel level, @Nullable PlayerRemover playerRemover);

    boolean anyDimensionExists(Identifier identifier);

    boolean canCreateDimension(Identifier identifier);

    boolean canDeleteDimension(Identifier identifier);
}
