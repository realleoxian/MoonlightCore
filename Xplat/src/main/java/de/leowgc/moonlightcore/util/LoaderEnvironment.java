package de.leowgc.moonlightcore.util;

import de.leowgc.moonlightcore.api.util.ExpectPlatform;

import java.nio.file.Path;

@ExpectPlatform
public final class LoaderEnvironment {

    public static boolean isDevEnvironment() {
        throw new UnsupportedOperationException();
    }

    public static Path getConfigPath() {
        throw new UnsupportedOperationException();
    }

    private LoaderEnvironment() {}
}
