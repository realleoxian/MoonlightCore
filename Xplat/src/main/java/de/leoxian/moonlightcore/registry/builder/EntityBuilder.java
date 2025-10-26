package de.leoxian.moonlightcore.registry.builder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EntityBuilder<T extends Entity> extends AbstractBuilder<EntityType<?>, EntityType<T>> {
    public static <T extends Entity> EntityBuilder<T> of(ResourceLocation id, EntityType.EntityFactory<T> factory, MobCategory category) {
        return new EntityBuilder<>(id, factory, category);
    }

    private final Supplier<EntityType.Builder<T>> builder;
    private Consumer<EntityType.Builder<T>> builderCallback = $ -> {};

    protected EntityBuilder(ResourceLocation id, EntityType.EntityFactory<T> factory, MobCategory category) {
        super(Registries.ENTITY_TYPE, id);
        this.builder = () -> EntityType.Builder.of(factory, category);
    }

    public EntityBuilder<T> properties(Consumer<EntityType.Builder<T>> callback) {
        this.builderCallback = callback;
        return this;
    }

    @Override
    protected EntityType<T> buildEntry() {
        EntityType.Builder<T> builder = this.builder.get();
        builderCallback.accept(builder);

        return builder.build(this.id.getPath());
    }
}
