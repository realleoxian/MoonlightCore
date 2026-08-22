package de.leoxian.moonlightcore.neoforge.common;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;

public final class ModDeferredRegisters {
    private static final Table<ResourceKey<?>, String, DeferredRegister<?>> DEFERRED_REGISTERS = Tables.synchronizedTable(HashBasedTable.create());

    @SuppressWarnings("unchecked")
    public static <T> DeferredRegister<T> get(ResourceKey<? extends Registry<T>> registry, String modId) {
        DeferredRegister<?> register = DEFERRED_REGISTERS.get(registry, modId);
        if (register == null) {
            register = DeferredRegister.create(registry, modId);
            DEFERRED_REGISTERS.put(registry, modId, register);
        }
        return (DeferredRegister<T>) register;
    }

    public static <T> DeferredRegister<T> get(Registry<T> registry, String modId) {
        return get(registry.key(), modId);
    }

    public static void register(String modId, IEventBus eventBus) {
        synchronized (DEFERRED_REGISTERS) {
            for (DeferredRegister<?> register : getByModId(modId)) {
                register.register(eventBus);
            }
        }
    }

    public static Collection<DeferredRegister<?>> getByModId(String modId) {
        return DEFERRED_REGISTERS.column(modId).values();
    }

    private ModDeferredRegisters() {}
}
