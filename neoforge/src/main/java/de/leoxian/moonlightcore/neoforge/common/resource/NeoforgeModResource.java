package de.leoxian.moonlightcore.neoforge.common.resource;

import de.leoxian.moonlightcore.common.resource.ModResource;
import net.neoforged.fml.jarcontents.JarContents;
import net.neoforged.fml.jarcontents.JarResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public record NeoforgeModResource(JarResource resource, String path) implements ModResource {
    @Override
    public InputStream open() throws IOException {
        return resource().open();
    }

    @Override
    public BufferedReader bufferedReader(Charset charset) throws IOException {
        return resource().bufferedReader(charset);
    }
}
