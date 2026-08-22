package de.leoxian.moonlightcore.neoforge.common.capability;

import de.leoxian.moonlightcore.common.capability.entity.EntityCapability;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NeoforgeEntityCapability<A, C> implements EntityCapability<A, C> {
    private final Identifier id;
    private final Class<A> apiClass;
    private final Class<C> contextClass;
    private final net.neoforged.neoforge.capabilities.EntityCapability<A, C> neoCapability;
    private final List<Consumer<RegisterCapabilitiesEvent>> pendingRegistrations = new ArrayList<>();

    NeoforgeEntityCapability(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        this.id = id;
        this.apiClass = apiClass;
        this.contextClass = contextClass;
        this.neoCapability = net.neoforged.neoforge.capabilities.EntityCapability.create(
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
    public @Nullable A find(Entity entity, C context) {
        return entity.getCapability(this.neoCapability, context);
    }

    @Override
    public <E extends Entity> void registerForEntity(Supplier<EntityType<E>> entityType, BiFunction<E, C, A> provider) {
        this.pendingRegistrations.add(event ->
                event.registerEntity(this.neoCapability, entityType.get(), provider::apply)
        );
    }

    @Override
    public void registerSelf(Supplier<EntityType<?>> entityType) {
        this.registerForEntity(() -> (EntityType<Entity>) entityType.get(), (e, c) -> {
            if (this.apiClass.isInstance(e)) {
                this.apiClass.cast(e);
            }
            return null;
        });
    }

    @Override
    public void registerFallback(Provider<A, C> provider) {
        this.pendingRegistrations.add(event -> {
            for (final EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
                event.registerEntity(this.neoCapability, (EntityType<Entity>) entityType, provider::find);
            }
        });
    }

    @Override
    public @Nullable Provider<A, C> getProvider(Supplier<EntityType<?>> entityType) {
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
