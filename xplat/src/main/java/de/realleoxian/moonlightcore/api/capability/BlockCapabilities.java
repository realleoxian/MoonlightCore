package de.realleoxian.moonlightcore.api.capability;

import de.realleoxian.moonlightcore.api.MoonlightCore;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface BlockCapabilities {
    static BlockCapabilities get() {
        return MoonlightCore.ABSTRACTION.getBlockCapabilities();
    }

    @Nullable
    <T, C> T find(Level level, BlockPos blockPos, @Nullable BlockState blockState, @Nullable BlockEntity blockEntity, C context);

    @Nullable
    default <T, C> T find(Level level, BlockPos blockPos, C context) {
        return find(level, blockPos, null, null, context);
    }

    <T, C> CapabilityType<Block, T, C> create(ResourceLocation name, Class<T> capabilityType, Class<C> contextType);

    <T, C> void registerForBlocks(CapabilityType<Block, T, C> capabilityType, Provider<T, C> provider, Supplier<Block>... blocks);

    <T, C> void registerForBlockEntities(CapabilityType<Block, T, C> capabilityType, Provider<T, C> provider, Supplier<BlockEntityType<?>>... blockEntities);

    <T, C> void registerSelf(CapabilityType<Block, T, C> capabilityType, Supplier<BlockEntityType<?>>... blockEntities);

    <T, C> void registerFallback(CapabilityType<Block, T, C> capabilityType, Provider<T, C> provider);

    <T, C> Provider<T, C> getProvider(CapabilityType<Block, T, C> capabilityType, Supplier<Block> block);

    interface Provider<T, C> {
        @Nullable
        T find(Level level, BlockPos blockPos, @Nullable BlockState blockState, @Nullable BlockEntity blockEntity, C context);
    }
}
