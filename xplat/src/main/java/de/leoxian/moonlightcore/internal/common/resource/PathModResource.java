package de.leoxian.moonlightcore.internal.common.resource;

import de.leoxian.moonlightcore.common.resource.ModResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public record PathModResource(Path nioPath) implements ModResource {
    @Override
    public InputStream open() throws IOException {
        return Files.newInputStream(nioPath());
    }
}
