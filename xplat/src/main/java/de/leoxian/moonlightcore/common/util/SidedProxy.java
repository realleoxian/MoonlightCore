package de.leoxian.moonlightcore.common.util;

import de.leoxian.moonlightcore.common.EnvironmentSide;

import java.util.function.Supplier;

public record SidedProxy<T>(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) implements Supplier<T> {
    @Override
    public T get() {
        return switch (EnvironmentSide.current()) {
            case CLIENT -> clientTarget.get().get();
            case SERVER -> serverTarget.get().get();
        };
    }
}
