package de.leoxian.moonlightcore.common.registry;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public class Registrar<R> {
    public static <R> Registrar<R> create(final String modId, final Registry<R> registry) {
        return new Registrar<>(modId, registry);
    }

    private final String modId;
    private final Registry<R> registry;

    Registrar(String modId, Registry<R> registry) {
        this.modId = modId;
        this.registry = registry;
    }

    public <T extends R> DeferredHolder<R, T> register(final String id, final Supplier<T> sup) {
        return XplatAbstraction.INSTANCE.register(this.registry, Identifier.fromNamespaceAndPath(this.modId, id), sup);
    }
}
