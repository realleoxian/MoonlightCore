package de.leoxian.moonlightcore.lookup.block;

import com.mojang.logging.LogUtils;
import de.leoxian.moonlightcore.lookup.ApiLookupMap;
import de.leoxian.moonlightcore.lookup.ApiProviderMap;
import de.leoxian.moonlightcore.mixin.accessor.BlockEntityTypeAccessor;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class BlockApiLookup<A, C> {
    private static final ApiLookupMap<BlockApiLookup<?, ?>> LOOKUPS = ApiLookupMap.create(BlockApiLookup::new);
    private static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("unchecked")
    public static <A, C> BlockApiLookup<A, C> get(ResourceLocation id, Class<A> apiClass, Class<C> contextClass) {
        return (BlockApiLookup<A, C>) LOOKUPS.getLookup(id, apiClass, contextClass);
    }

    private final ApiProviderMap<Block, BlockApiProvider<A, C>> providerMap = ApiProviderMap.create();
    private final List<BlockApiProvider<A, C>> fallbackProviders = new CopyOnWriteArrayList<>();

    private final ResourceLocation id;
    private final Class<A> apiClass;
    private final Class<C> contextClass;

    private BlockApiLookup(ResourceLocation id, Class<A> apiClass, Class<C> contextClass) {
        this.id = id;
        this.apiClass = apiClass;
        this.contextClass = contextClass;
    }

    public A find(Level level, BlockPos pos, C context) {
        return this.find(level, pos, null, null, context);
    }

    public A find(Level level, BlockPos pos, @Nullable BlockState blockState, @Nullable BlockEntity blockEntity, C context) {
        Objects.requireNonNull(level, "Level may not be null");
        Objects.requireNonNull(pos, "BlockPos may not be null");

        if(blockEntity == null) {
            if(blockState == null) {
                blockState = level.getBlockState(pos);
            }

            if(blockState.hasBlockEntity()) {
                blockEntity = level.getBlockEntity(pos);
            }
        } else {
            if(blockState == null) {
                blockState = blockEntity.getBlockState();
            }
        }

        BlockApiProvider<A, C> provider = getProvider(blockState.getBlock());
        A instance = null;

        if(provider != null) {
            instance = provider.get(level, pos, blockState, blockEntity, context);
        }

        if(instance != null) {
            return instance;
        }

        for(BlockApiProvider<A, C> fallback : this.fallbackProviders) {
            instance = fallback.get(level, pos, blockState, blockEntity, context);

            if(instance != null) {
                return instance;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public void registerSelf(BlockEntityType<?>... blockEntityTypes) {
        for(BlockEntityType<?> type : blockEntityTypes) {
            Block supportBlock = ((BlockEntityTypeAccessor) type).getValidBlocks().iterator().next();
            Objects.requireNonNull(supportBlock, "Could not get a support block for block entity type");

            BlockEntity be = type.create(BlockPos.ZERO, supportBlock.defaultBlockState());
            Objects.requireNonNull(be, "Instantiated block entity may not be null");

            if(!this.apiClass.isAssignableFrom(type.getClass())) {
                String errorMessage = String.format(
                        "Failed to register self-implementing block entities. API class %s is not assignable from block entity class %s",
                        apiClass.getCanonicalName(),
                        be.getClass().getCanonicalName()
                );

                throw new IllegalArgumentException(errorMessage);
            }

            registerForBlockEntities((blockEntity, ctx) -> (A) blockEntity, blockEntityTypes);
        }
    }

    public void registerForBlockEntities(BlockEntityApiProvider<A, C> provider, BlockEntityType<?>... blockEntityTypes) {
        Objects.requireNonNull(provider, "BlockEntityApiProvider may not be null");

        if(blockEntityTypes.length == 0) {
            throw new IllegalArgumentException("Must register at least one block entity type instance with a BlockEntityApiProvider");
        }

        BlockApiProvider<A, C> nullCHeckProvider = (level, pos, state, blockEntity, context) -> {
            if(blockEntity == null) {
                return null;
            } else {
                return provider.find(blockEntity, context);
            }
        };

        for(BlockEntityType<?> blockEntityType : blockEntityTypes) {
            Objects.requireNonNull(blockEntityType, "Encountered null block entity type while registering a block entity API provider mapping");

            Block[] validBlocks = ((BlockEntityTypeAccessor) blockEntityType).getValidBlocks().toArray(Block[]::new);
            registerForBlocks(nullCHeckProvider, validBlocks);
        }
    }

    public void registerForBlocks(BlockApiProvider<A, C> provider, Block... blocks) {
        Objects.requireNonNull(provider, "BlockApiProvider may not be null");

        if(blocks.length == 0) {
            throw new IllegalArgumentException("Must register at least one block instance with a BlockApiProvider");
        }

        for(Block block : blocks) {
            Objects.requireNonNull(block, "Encountered a null block while register a block API provider mapping");

            if(providerMap.putIfAbsent(block, provider) != null) {
                LOGGER.warn("Encountered duplicated API provider registration for block: {}", BuiltInRegistries.BLOCK.getId(block));
            }
        }
    }

    public void registerFallback(BlockApiProvider<A, C> fallbackProvider) {
        Objects.requireNonNull(fallbackProvider, "BlockApiProvider may not be null");
        this.fallbackProviders.add(fallbackProvider);
    }

    public BlockApiProvider<A, C> getProvider(Block block) {
        return providerMap.get(block);
    }

    public ResourceLocation id() {
        return this.id;
    }

    public Class<A> apiClass() {
        return this.apiClass;
    }

    public Class<C> contextClass() {
        return this.contextClass;
    }

    @FunctionalInterface
    public interface BlockApiProvider<A, C> {
        @Nullable
        A get(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, C context);
    }

    public interface BlockEntityApiProvider<A, C> {
        @Nullable
        A find(BlockEntity block, C context);
    }
}
