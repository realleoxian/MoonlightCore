package de.realleoxian.moonlightcore.xplat.config;

import de.realleoxian.moonlightcore.api.config.metadata.CommentsMetadata;
import de.realleoxian.moonlightcore.api.config.schema.ConfigSchema;
import de.realleoxian.moonlightcore.api.config.schema.ConfigValue;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public final class ConfigSerializer {
    public static void writeToFile(ModConfigImpl config) throws IOException {
        final var content = new ArrayList<String>();
        final var schema = config.getSchema();

        config.lock.lock();
        try {
            schema.getValues().forEach(val -> writeValue(content, val));
            schema.getSchemas().forEach(sch -> writeSchema(content, sch));
            Files.write(config.getPath(), content);
        } finally {
            config.lock.unlock();
        }
    }

    private static void writeSchema(List<String> content, ConfigSchema schema) {
        if (schema.hasMetadata(CommentsMetadata.TYPE)) {
            final var comments = schema.getMetadata(CommentsMetadata.TYPE);
            if (comments != null) {
                comments.forEach(comment -> {
                    content.add("# " + comment);
                    content.add("");
                });
            }
        }

        schema.getValues().forEach(val -> writeValue(content, val));
        schema.getSchemas().forEach(sch -> writeSchema(content, sch));
        content.add("");
    }

    private static <T> void writeValue(List<String> content, ConfigValue<T> configValue) {
        if (configValue.hasMetadata(CommentsMetadata.TYPE)) {
            final var comments = configValue.getMetadata(CommentsMetadata.TYPE);
            if (comments != null) {
                comments.forEach(comment -> {
                    content.add("# " + comment);
                    content.add("");
                });
            }
        }

        final var serializer = configValue.getSerializer();
        content.add("%s = %s".formatted(configValue.getKey(), serializer.writeToString(configValue.getValue())));
        content.add("");
    }

    private ConfigSerializer() {}
}
