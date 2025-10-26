package de.leoxian.moonlightcore.platform;

import java.util.function.Supplier;

public enum EnvironmentSide {
     CLIENT,
     SERVER
     ;

     private static final EnvironmentSide CURRENT = PlatformEnvironment.get().getEnvironmentSide();

     public static <T> T unsafeRunIf(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) {
          return switch (CURRENT) {
               case CLIENT -> clientTarget.get().get();
               case SERVER -> serverTarget.get().get();
          };
     }

     public void runIfCurrent(Supplier<Runnable> run) {
          if(this.isCurrent()) {
               run.get().run();
          }
     }

     public boolean isCurrent() {
          return this == CURRENT;
     }
}
