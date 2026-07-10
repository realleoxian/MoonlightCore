package de.leoxian.moonlightcore.common.resource;

import org.jetbrains.annotations.ApiStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@ApiStatus.NonExtendable
public interface ModResource {
    InputStream open();

    default BufferedReader bufferedReader(Charset charset) throws IOException {
        return new BufferedReader(new InputStreamReader(open(), charset));
    }

    default BufferedReader bufferedReader() throws IOException {
        return bufferedReader(StandardCharsets.UTF_8);
    }
}
