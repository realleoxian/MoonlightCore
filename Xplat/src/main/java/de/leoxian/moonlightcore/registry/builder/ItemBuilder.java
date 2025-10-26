package de.leoxian.moonlightcore.registry.builder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ItemBuilder<T extends Item> extends AbstractBuilder<Item, T> {
    public static <T extends Item> ItemBuilder<T> of(ResourceLocation id, Function<Item.Properties, T> properties) {
        return new ItemBuilder<>(id, properties);
    }

    private final Function<Item.Properties, T> itemFactory;

    private Function<Item.Properties, Item.Properties> propertiesCallback = UnaryOperator.identity();
    private Supplier<Item.Properties> initialProperties = Item.Properties::new;

    protected ItemBuilder(ResourceLocation id, Function<Item.Properties, T> itemFactory) {
        super(Registries.ITEM, id);
        this.itemFactory = itemFactory;
    }

    public ItemBuilder<T> initialProperties(Supplier<Item.Properties> properties) {
        this.initialProperties = properties;
        return this;
    }

    public ItemBuilder<T> properties(UnaryOperator<Item.Properties> propertiesCallback) {
        this.propertiesCallback = this.propertiesCallback.andThen(propertiesCallback);
        return this;
    }

    @Override
    protected T buildEntry() {
        Item.Properties properties = this.initialProperties.get();
        properties = this.propertiesCallback.apply(properties);

        return this.itemFactory.apply(properties);
    }
}
