package de.leoxian.moonlightcore.common.util;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import de.leoxian.moonlightcore.common.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.mixin.accessor.HolderSetNamedAccessor;
import de.leoxian.moonlightcore.mixin.accessor.MappedRegistryAccessor;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.core.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.function.Supplier;

/// # Be careful with what are you doing with these methods-
public final class DynamicRegistryUtils {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static <T> void unregister(Registry<T> registry, Identifier id) {
        Objects.requireNonNull(registry, "Registry may not be 'null'");
        Objects.requireNonNull(id, "Id may not be 'null'");

        if (registry.containsKey(id)) {
            if (registry instanceof MappedRegistry<T>) {
                if (registry instanceof DefaultedMappedRegistry<T> defaultedMappedRegistry && defaultedMappedRegistry.getDefaultKey().equals(id)) {
                    throw new IllegalArgumentException("May not remove default value from registry");
                }

                @SuppressWarnings("unchecked")
                MappedRegistryAccessor<T> accessor = (MappedRegistryAccessor<T>) registry;
                ResourceKey<T> key = ResourceKey.create(registry.key(), id);
                T value = accessor.getByLocation().get(id).value();


                ObjectList<Holder.Reference<T>> byId = accessor.getById();
                int rawId = accessor.getToId().removeInt(value);
                if (byId.get(rawId).value() != value) {
                    LOGGER.error("ID mismatch in registry '{}'", registry.key());
                }

                Holder.Reference<T> removed = byId.remove(rawId);
                assert removed.value() == value;
                accessor.getToId().replaceAll((t, i) -> i > rawId ? i - 1 : i);
                accessor.getByLocation().remove(id);
                accessor.getByKey().remove(key);
                accessor.getByValue().remove(value);
                accessor.getRegistrationInfos().remove(key);
                Lifecycle lifecycle = Lifecycle.stable();
                for (RegistrationInfo info : accessor.getRegistrationInfos().values()) {
                    lifecycle.add(info.lifecycle());
                }
                accessor.setRegistryLifecycle(lifecycle);

                for (HolderSet.Named<T> holderSet : accessor.getFrozenTags().values()) {
                    @SuppressWarnings("unchecked")
                    HolderSetNamedAccessor<T> holderSetNamedAccessor = (HolderSetNamedAccessor<T>) holderSet;
                    ImmutableList.Builder<Holder<T>> list = ImmutableList.builder();
                    for (Holder<T> content : holderSetNamedAccessor.getContents()) {
                        if (!content.is(id)) {
                            list.add(content);
                        }
                    }
                    holderSetNamedAccessor.setContents(list.build());
                }
                if (accessor.getUnregisteredIntrusiveHolders() != null) {
                    accessor.getUnregisteredIntrusiveHolders().remove(value);
                }
            }
        } else {
            LOGGER.warn("Tried to remove non-existing key {}", id);
        }
    }

    public static <T> Holder.Reference<T> register(Registry<T> registry, Identifier id, Supplier<T> value) {
        if (!registry.containsKey(id)) {
            if (!(registry instanceof MappedRegistry<T> mappedRegistry)) {
                throw new IllegalStateException("Non-vanilla registry '" + registry.key() + "'");
            }
            @SuppressWarnings("unchecked")
            MappedRegistryAccessor<T> accessor = (MappedRegistryAccessor<T>) mappedRegistry;
            boolean frozen = accessor.isFrozen();
            if (frozen) accessor.setFrozen(false);
            Holder.Reference<T> ret = mappedRegistry.register(ResourceKey.create(registry.key(), id), value.get(), RegistrationInfo.BUILT_IN);
            if (frozen) registry.freeze();
            assert accessor.getById().get(accessor.getToId().getInt(value)) != null;
            return ret;
        } else {
            LOGGER.warn("Tried to add pre-existing key {}", id);
            return registry.getOrThrow(ResourceKey.create(registry.key(), id));
        }
    }

    private DynamicRegistryUtils() {}
}
