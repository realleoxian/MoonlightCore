package de.realleoxian.moonlightcore.api;

import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public enum EnvSide {
    CLIENT,
    SERVER
    ;

    private static final EnvSide CURRENT = MoonlightCore.RUNTIME.getEnvironmentSide();

    public static <T> T runForSide(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) {
        return switch (CURRENT) {
            case SERVER -> serverTarget.get().get();
            case CLIENT -> clientTarget.get().get();
        };
    }


    @Nullable
    public static <T> T unsafeRunWhenOn(EnvSide side, Supplier<Callable<T>> target) {
        if(side.isCurrent()) {
            try {
                return target.get().call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    public void runIfCurrent(Supplier<Runnable> task) {
        if(isCurrent()) {
            try {
                task.get().run();
            } catch (Exception e) {
                throw new RuntimeException("Failed to execute task on '" + this.name() + "' side", e);
            }
        }
    }

    public boolean isCurrent() {
        return this == CURRENT;
    }

}
