package de.leoxian.moonlightcore.fabric.common.capability;

import de.leoxian.moonlightcore.common.capability.block.BlockCapability;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class FabricBlockCapability<A, C> implements BlockCapability<A, C> {
    private static final Map<Identifier, FabricBlockCapability<?, ?>> CAPABILITIES = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <A, C> BlockCapability<A, C> get(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        FabricBlockCapability<?, ?> existing = CAPABILITIES.computeIfAbsent(id, key -> new FabricBlockCapability<>(key, apiClass, contextClass));

        if (existing.apiClass() != apiClass) {
            throw new IllegalStateException("Attempted to register capability " + id + " with existing type class " + existing.apiClass() + " != " + apiClass);
        }
        if (existing.contextClass() != contextClass) {
            throw new IllegalStateException("Attempted to register capability " + id + " with existing context class " + existing.contextClass() + " != " + contextClass);
        }

        return (BlockCapability<A, C>) existing;
    }

    final BlockApiLookup<A, C> apiLookup;

    public FabricBlockCapability(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        this.apiLookup = BlockApiLookup.get(id, apiClass, contextClass);
    }

    @Override
    public @Nullable A find(Level level, BlockPos blockPos, @Nullable BlockState blockState, @Nullable BlockEntity blockEntity, C context) {
        return apiLookup.find(level, blockPos, blockState, blockEntity, context);
    }

    @Override
    public void registerForBlock(Supplier<Block> block, Provider<A, C> provider) {
        apiLookup.registerForBlocks(provider::find, block.get());
    }

    @Override
    public <BE extends BlockEntity> void registerForBlockEntity(Supplier<BlockEntityType<BE>> blockEntityType, BiFunction<BE, C, A> provider) {
        apiLookup.registerForBlockEntity(provider, blockEntityType.get());
    }

    @Override
    public void registerSelf(Supplier<BlockEntityType<?>> blockEntityType) {
        apiLookup.registerSelf(blockEntityType.get());
    }

    @Override
    public void registerFallback(Provider<A, C> provider) {
        apiLookup.registerFallback(provider::find);
    }

    @Override
    public @Nullable Provider<A, C> getProvider(Supplier<Block> blockEntityType) {
        BlockApiLookup.BlockApiProvider<A, C> provider = apiLookup.getProvider(blockEntityType.get());
        if (provider == null) {
            return null;
        }
        return provider::find;
    }

    @Override
    public Identifier id() {
        return apiLookup.getId();
    }

    @Override
    public Class<A> apiClass() {
        return apiLookup.apiClass();
    }

    @Override
    public Class<C> contextClass() {
        return apiLookup.contextClass();
    }
}
