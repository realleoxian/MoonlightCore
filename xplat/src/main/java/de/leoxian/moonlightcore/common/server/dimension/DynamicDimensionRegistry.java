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
    /// Get the current server's dynamic dimension registry. May be `null` if there is no current server
    /// @return The current server's dynamic dimension registry
    static @Nullable DynamicDimensionRegistry get() {
        MinecraftServer current = XplatAbstraction.INSTANCE.getCurrentServer();
        if (current == null) {
            return null;
        }
        return ((DynamicDimensionProvider) current).moonlightcore$registry();
    }

    /// Creates a new dynamic dimension that can be unloaded, deleted and then re-created
    /// @param id The identifier of the dimension
    /// @param chunkGenerator The chunk generator of the dimension
    /// @param dimensionType The dimension's type
    /// @return A new [ServerLevel] instance for the dimension
    ServerLevel createDynamicDimension(Identifier id, ChunkGenerator chunkGenerator, DimensionType dimensionType);

    /// Unloads the given dimension from the server and removes the players in it
    /// @param level The dimension being unloaded
    /// @param playerRemover The player remover, may be `null` to use default remover
    void unloadDynamicDimension(ServerLevel level, @Nullable DimensionPlayerRemover playerRemover);

    /// Unloads and then deletes the given dimension's data from the server, removing the players in it
    /// @param level The dimension being deleted
    /// @param playerRemover The player remover, may be `null` to use default remover
    void deleteDynamicDimension(ServerLevel level, @Nullable DimensionPlayerRemover playerRemover);

    /// @param identifier The dimension's identifier
    /// @return Whether the dimension exists
    boolean anyDimensionExists(Identifier identifier);

    /// @param identifier The dimension's identifier
    /// @return Whether the dimension can be created
    boolean canCreateDimension(Identifier identifier);

    /// @param identifier The dimension's identifier
    /// @return Whether the dimension can be deleted
    boolean canDeleteDimension(Identifier identifier);
}
