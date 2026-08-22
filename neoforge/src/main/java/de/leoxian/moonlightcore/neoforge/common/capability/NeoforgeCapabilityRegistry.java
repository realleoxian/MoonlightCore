package de.leoxian.moonlightcore.neoforge.common.capability;

import de.leoxian.moonlightcore.common.capability.block.BlockCapability;
import de.leoxian.moonlightcore.common.capability.entity.EntityCapability;
import de.leoxian.moonlightcore.common.capability.item.ItemCapability;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class NeoforgeCapabilityRegistry {
    private final Map<Identifier, NeoforgeBlockCapability<?, ?>> blockCapabilities = new ConcurrentHashMap<>();
    private final Map<Identifier, NeoforgeItemCapability<?, ?>> itemCapabilities = new ConcurrentHashMap<>();
    private final Map<Identifier, NeoforgeEntityCapability<?, ?>> entityCapabilities = new ConcurrentHashMap<>();

    @SubscribeEvent
    public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        this.blockCapabilities.values().forEach(c -> c.register(event));
        this.itemCapabilities.values().forEach(c -> c.register(event));
        this.entityCapabilities.values().forEach(c -> c.register(event));
    }

    @SuppressWarnings("unchecked")
    public <A, C> BlockCapability<A, C> getBlockCapability(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        BlockCapability<A, C> capability = (BlockCapability<A, C>) this.blockCapabilities.computeIfAbsent(id, k ->
                new NeoforgeBlockCapability<>(id, apiClass, contextClass));
        validateCapabilityTypes(capability.id(), capability.apiClass(), capability.contextClass(), apiClass, contextClass);
        return capability;
    }

    @SuppressWarnings("unchecked")
    public <A, C> ItemCapability<A, C> getItemCapability(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        ItemCapability<A, C> capability = (ItemCapability<A, C>) this.itemCapabilities.computeIfAbsent(id, k ->
                new NeoforgeItemCapability<>(id, apiClass, contextClass));
        validateCapabilityTypes(capability.id(), capability.apiClass(), capability.contextClass(), apiClass, contextClass);
        return capability;
    }

    @SuppressWarnings("unchecked")
    public <A, C> EntityCapability<A, C> getEntityCapability(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        EntityCapability<A, C> capability = (EntityCapability<A, C>) this.entityCapabilities.computeIfAbsent(id, k ->
                new NeoforgeEntityCapability<>(id, apiClass, contextClass));
        validateCapabilityTypes(capability.id(), capability.apiClass(), capability.contextClass(), apiClass, contextClass);
        return capability;
    }

    private void validateCapabilityTypes(Identifier id, Class<?> actualApi, Class<?> actualContext, Class<?> expectedApi, Class<?> expectedContext) {
        if (actualApi != expectedApi)
            throw new IllegalStateException("Attempted to register capability " + id + " with existing type class " + actualApi + " != " + expectedApi);
        if (actualContext != expectedContext)
            throw new IllegalStateException("Attempted to register capability " + id + " with existing context class " + actualContext + " != " + expectedContext);
    }
}
