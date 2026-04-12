package de.realleoxian.moonlightcore.impl.apilookup;

import de.realleoxian.moonlightcore.api.apilookup.ApiLookupRegistry;
import de.realleoxian.moonlightcore.api.apilookup.block.BlockApiLookup;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public final class BlockApiLookupImpl<A, C extends @Nullable Object> extends ApiLookupImpl<A, C> implements BlockApiLookup<A, C> {
    private static final ApiLookupRegistry<BlockApiLookup<?, ?>> REGISTRY = ApiLookupRegistry.create(BlockApiLookupImpl::new);

    @SuppressWarnings("unchecked")
    public static <A, C extends @Nullable Object> BlockApiLookup<A, C> find(ResourceLocation name, Class<A> apiClass, Class<C> contextClass) {
        return (BlockApiLookup<A, C>) REGISTRY.create(name, apiClass, contextClass);
    }

    private final Map<Block, BlockApiLookup.Provider<A, C>> providers = new IdentityHashMap<>();
    private final List<BlockApiLookup.Provider<A, C>> fallbackProviders = new ArrayList<>();

    private BlockApiLookupImpl(ResourceLocation name, Class<A> apiClass, Class<C> contextClass) {
        super(name, apiClass, contextClass);
    }

    @Override
    public @Nullable A find(Level level, BlockPos blockPos, @Nullable BlockState blockState, @Nullable BlockEntity blockEntity, C context) {
        Objects.requireNonNull(level, "Level may not be 'null'");
        Objects.requireNonNull(blockEntity, "BlockPos may not be 'null'");
        Objects.requireNonNull(context, "API context may not be 'null'");

        if(blockEntity == null) {
            if(blockState == null) {
                blockState = level.getBlockState(blockPos);
            }

            if(blockState.hasBlockEntity()) {
                blockEntity = level.getBlockEntity(blockPos);
            }
        } else {
            if(blockState == null) {
                blockState = blockEntity.getBlockState();
            }
        }

        BlockApiLookup.Provider<A, C> provider = providers.get(blockState.getBlock());
        if(provider == null) {
            return null;
        }

        A instance = provider.get(level, blockPos, blockState, blockEntity, context);
        if(instance == null) {
            for(BlockApiLookup.Provider<A, C> fallback : fallbackProviders) {
                instance = fallback.get(level, blockPos, blockState, blockEntity, context);

                if(instance != null) {
                    break;
                }
            }
        }

        return instance;
    }

    @Override
    public void register(Provider<A, C> provider, Block... blocks) {
        Objects.requireNonNull(provider, "Block API provider cannot be 'null'");

        if(blocks.length == 0) {
            throw new IllegalArgumentException("Must register at least one block with an BlockApiLookup$Provider");
        }

        for(Block block : blocks) {
            Objects.requireNonNull(block, "Block cannot be 'null'");

            if(providers.putIfAbsent(block, provider) != null) {
                throw new IllegalStateException("Duplicated API definition for block '" + BuiltInRegistries.BLOCK.getKey(block) + "'");
            }
        }
    }

    @Override
    public void registerFallback(Provider<A, C> fallback) {
        Objects.requireNonNull(fallback, "Fallback BlockApiLookup provider may not be 'null'");
        fallbackProviders.add(fallback);
    }

    @Override
    public Provider<A, C> getProvider(Block block) {
        Objects.requireNonNull(block, "Block cannot be 'null'");
        return providers.get(block);
    }

    @Override
    public List<Provider<A, C>> getFallbackProviders() {
        return Collections.unmodifiableList(fallbackProviders);
    }
}
