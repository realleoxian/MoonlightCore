/*
 * This source code file is subject to the terms of the Mozilla Public License, v. 2.0.
 * Based on code from Tterrag1098's Registrate (https://github.com/tterrag1098/Registrate).
 * Modifications by Leoxian, 2025
 */
package de.leoxian.moonlightcore.registry.builder;

import de.leoxian.moonlightcore.event.client.RenderingEvents;
import de.leoxian.moonlightcore.event.common.EntityEvent;
import de.leoxian.moonlightcore.platform.EnvironmentSide;
import de.leoxian.moonlightcore.registry.DeferredRegistrar;
import de.leoxian.moonlightcore.util.nullness.NonnullConsumer;
import de.leoxian.moonlightcore.util.nullness.NonnullFunction;
import de.leoxian.moonlightcore.util.nullness.NonnullSupplier;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Supplier;

public class EntityBuilder<T extends Entity> extends AbstractBuilder<EntityType<?>, EntityType<T>, EntityBuilder<T>> {

    private final NonnullSupplier<EntityType.Builder<T>> builder;

    private NonnullConsumer<EntityType.Builder<T>> builderCallback = $ -> {};
    private NonnullSupplier<NonnullFunction<EntityRendererProvider.Context, EntityRenderer<T>>> renderer = null;

    private boolean spawnPlacementConfigured = false;
    private boolean attributesConfigured = false;

    protected EntityBuilder(DeferredRegistrar<EntityType<?>> registrar, String name, EntityType.EntityFactory<T> factory, MobCategory category) {
        super(registrar, name);
        this.builder = () -> EntityType.Builder.of(factory, category);
    }

    public EntityBuilder<T> properties(NonnullConsumer<EntityType.Builder<T>> callback) {
        builderCallback = builderCallback.andThen(callback);
        return this;
    }

    public EntityBuilder<T> renderer(NonnullSupplier<NonnullFunction<EntityRendererProvider.Context, EntityRenderer<T>>> renderer) {
        if(this.renderer == null) {
            EnvironmentSide.CLIENT.runIfCurrent(() -> this::setupRenderer);
        }
        this.renderer = renderer;
        return this;
    }

    @SuppressWarnings("unchecked")
    public EntityBuilder<T> attributes(Supplier<AttributeSupplier.Builder> attributes) {
        if(attributesConfigured) {
            throw new IllegalStateException("Attributes were already configured");
        }
        attributesConfigured = true;
        EntityEvent.ATTRIBUTE_CREATION.subscribe(output -> output.accept((EntityType<LivingEntity>) getValue(), attributes.get().build()));

        return this;
    }

    @SuppressWarnings("unchecked")
    public EntityBuilder<T> spawnPlacement(SpawnPlacements.Type placementType, Heightmap.Types heightmapType, SpawnPlacements.SpawnPredicate<T> predicate) {
        if(spawnPlacementConfigured) {
            throw new IllegalStateException("Spawn placement was already configured");
        }
        spawnPlacementConfigured = true;
        onRegister(entry -> SpawnPlacements.register((EntityType<Mob>) entry, placementType, heightmapType, (SpawnPlacements.SpawnPredicate<Mob>) predicate));

        return this;
    }

    @Override
    protected EntityType<T> buildEntry() {
        EntityType.Builder<T> builder = this.builder.get();
        builderCallback.accept(builder);

        return builder.build(getName());
    }

    private void setupRenderer() {
        var renderer = this.renderer;

        if(renderer != null) {
            RenderingEvents.RENDERER_REGISTRATION.subscribe(output -> output.registerEntity(getValue(), renderer.get()::apply));
        }
    }
}
