package de.leoxian.moonlightcore.neoforge.common.capability;

import de.leoxian.moonlightcore.common.capability.block.BlockCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NeoforgeBlockCapability<A, C> implements BlockCapability<A, C> {
    private final Identifier id;
    private final Class<A> apiClass;
    private final Class<C> contextClass;
    final net.neoforged.neoforge.capabilities.BlockCapability<A, C> neoCapability;
    private final List<Consumer<RegisterCapabilitiesEvent>> pendingRegistrations = new ArrayList<>();

    NeoforgeBlockCapability(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        this.id = id;
        this.apiClass = apiClass;
        this.contextClass = contextClass;
        this.neoCapability = net.neoforged.neoforge.capabilities.BlockCapability.create(
                id,
                apiClass,
                contextClass
        );
    }

    void register(RegisterCapabilitiesEvent event) {
        this.pendingRegistrations.forEach(c -> c.accept(event));
        this.pendingRegistrations.clear();
    }

    @Override
    public @Nullable A find(Level level, BlockPos blockPos, @Nullable BlockState blockState, @Nullable BlockEntity blockEntity, C context) {
        return this.neoCapability.getCapability(level, blockPos, blockState, blockEntity, context);
    }

    @Override
    public void registerForBlock(Supplier<Block> block, Provider<A, C> provider) {
        this.pendingRegistrations.add(event ->
                event.registerBlock(this.neoCapability, provider::find, block.get()));
    }

    @Override
    public <BE extends BlockEntity> void registerForBlockEntity(Supplier<BlockEntityType<BE>> blockEntityType, BiFunction<BE, C, A> provider) {
        this.pendingRegistrations.add(event ->
                event.registerBlockEntity(this.neoCapability, blockEntityType.get(), provider::apply));
    }

    @Override
    public void registerSelf(Supplier<BlockEntityType<?>> blockEntityType) {
        registerForBlockEntity(() -> (BlockEntityType<BlockEntity>) blockEntityType, (be, c) -> {
            if (this.apiClass.isInstance(be)) {
                return this.apiClass.cast(be);
            }
            return null;
        });
    }

    @Override
    public void registerFallback(Provider<A, C> provider) {
        this.pendingRegistrations.add(event -> {
            for (final Block block : BuiltInRegistries.BLOCK) {
                event.registerBlock(this.neoCapability, provider::find, block);
            }
        });
    }

    @Override
    public @Nullable Provider<A, C> getProvider(Supplier<Block> blockEntityType) {
        return this::find;
    }

    @Override
    public Identifier id() {
        return this.id;
    }

    @Override
    public Class<A> apiClass() {
        return this.apiClass;
    }

    @Override
    public Class<C> contextClass() {
        return this.contextClass;
    }
}
