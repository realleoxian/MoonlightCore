/*
 * This source code file is subject to the terms of the Mozilla Public License, v. 2.0.
 * Based on code from Tterrag1098's Registrate (https://github.com/tterrag1098/Registrate).
 * Modifications by Leoxian, 2025
 */
package de.leoxian.moonlightcore.registry.builder;

import de.leoxian.moonlightcore.event.client.RenderingEvents;
import de.leoxian.moonlightcore.platform.EnvironmentSide;
import de.leoxian.moonlightcore.registry.DeferredRegistrar;
import de.leoxian.moonlightcore.util.nullness.NonnullFunction;
import de.leoxian.moonlightcore.util.nullness.NonnullSupplier;
import de.leoxian.moonlightcore.util.nullness.NonnullUnaryOperator;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;

import java.util.Objects;
import java.util.function.Supplier;

public class ItemBuilder<T extends Item> extends AbstractBuilder<Item, T, ItemBuilder<T>> {

    public static <T extends Item> ItemBuilder<T> builder(DeferredRegistrar<Item> registrar, String name, NonnullFunction<Item.Properties, T> factory) {
        return new ItemBuilder<>(registrar, name, factory);
    }

    private final NonnullFunction<Item.Properties, T> factory;

    private NonnullSupplier<Item.Properties> initialProperties = Item.Properties::new;
    private NonnullFunction<Item.Properties, Item.Properties> propertiesCallback = NonnullUnaryOperator.identity();

    private @Nullable NonnullSupplier<Supplier<ItemColor>> colorHandler = null;

    protected ItemBuilder(DeferredRegistrar<Item> registrar, String name, NonnullFunction<Item.Properties, T> factory) {
        super(registrar, name);
        this.factory = factory;
    }

    public ItemBuilder<T> initialProperties(NonnullSupplier<Item.Properties> initialProperties) {
        this.initialProperties = Objects.requireNonNull(initialProperties, "Initial properties may not be null");
        return this;
    }

    public ItemBuilder<T> properties(NonnullUnaryOperator<Item.Properties> callback) {
        Objects.requireNonNull(callback, "Properties callback may not be null");
        this.propertiesCallback = propertiesCallback.andThen(callback);
        return this;
    }

    public ItemBuilder<T> color(NonnullSupplier<Supplier<ItemColor>> colorHandler) {
        Objects.requireNonNull(colorHandler, "Color handler may not be null");
        if(this.colorHandler == null) onRegister(this::setupItemColor);
        this.colorHandler = colorHandler;
        return this;
    }

    @Override
    protected T buildEntry() {
        Item.Properties properties = initialProperties.get();
        properties = propertiesCallback.apply(properties);

        return factory.apply(properties);
    }

    private void setupItemColor(T entry) {
        EnvironmentSide.CLIENT.runIfCurrent(() -> () -> {
            var colorHandler = this.colorHandler;

            if(colorHandler != null) {
                RenderingEvents.BLOCK_COLOR_REGISTRATION.subscribe(output -> output.register(colorHandler.get().get(), entry));
            }
        });
    }

}
