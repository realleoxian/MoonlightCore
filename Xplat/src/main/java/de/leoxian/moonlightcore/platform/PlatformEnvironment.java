package de.leoxian.moonlightcore.platform;

import java.nio.file.Path;

public final class PlatformEnvironment {
     private static PlatformEnvironment INSTANCE = null;

     public static PlatformEnvironment get() {
          if(INSTANCE == null) {
               INSTANCE = new PlatformEnvironment();
          }

          return INSTANCE;
     }

     private PlatformEnvironment() {}

     public boolean isModLoaded(String modId) {
          throw new AssertionError();
     }

     public String getPlatformName() {
          throw new AssertionError();
     }

     public EnvironmentSide getEnvironmentSide() {
          throw new AssertionError();
     }

     public Path getGameDirectory() {
          throw new AssertionError();
     }

     public Path getConfigDirectory() {
          throw new AssertionError();
     }

     public boolean isDevelopmentEnvironment() {
          throw new AssertionError();
     }

}
