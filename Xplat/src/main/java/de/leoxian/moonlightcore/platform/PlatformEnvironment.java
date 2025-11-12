package de.leoxian.moonlightcore.platform;

import java.nio.file.Path;
import java.util.ServiceLoader;

public interface PlatformEnvironment {

    PlatformEnvironment INSTANCE = ServiceLoader.load(PlatformEnvironment.class).findFirst().orElseThrow(() -> new IllegalStateException("No platform implementation provided"));

    boolean isModLoaded(String modId);

    String getPlatformName();

    EnvironmentSide getEnvironmentSide();

    Path getGameDirectory();

    Path getConfigDirectory();

    boolean isDevelopmentEnvironment();

}
