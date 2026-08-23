package de.leoxian.moonlightcore.neoforge.common.capability;

import de.leoxian.moonlightcore.common.capability.item.ItemCapability;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NeoforgeItemCapability<A, C> implements ItemCapability<A, C> {
    private final Identifier id;
    private final Class<A> apiClass;
    private final Class<C> contextClass;
    private final net.neoforged.neoforge.capabilities.ItemCapability<A, C> neoCapability;
    private final List<Consumer<RegisterCapabilitiesEvent>> pendingRegistrations = new ArrayList<>();

    public NeoforgeItemCapability(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        this.id = id;
        this.apiClass = apiClass;
        this.contextClass = contextClass;
        this.neoCapability = net.neoforged.neoforge.capabilities.ItemCapability.create(
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
    public @Nullable A find(ItemStack stack, C context) {
        return stack.getCapability(this.neoCapability, context);
    }

    @Override
    public void registerForItem(Supplier<ItemLike> item, Provider<A, C> provider) {
        this.pendingRegistrations.add(event -> event.registerItem(this.neoCapability, provider::find, item.get()));
    }

    @Override
    public void registerSelf(Supplier<ItemLike> item) {
        registerForItem(item, (stack, ctx) -> {
            if (apiClass.isInstance(stack.getItem())) {
                return this.apiClass.cast(stack.getItem());
            }
            return null;
        });
    }

    @Override
    public void registerFallbackProvider(Provider<A, C> provider) {
        this.pendingRegistrations.add(event -> {
           for (final Item item : BuiltInRegistries.ITEM) {
               event.registerItem(this.neoCapability, provider::find, item);
           }
        });
    }

    @Override
    public @Nullable Provider<A, C> getProvider(Supplier<Item> item) {
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
