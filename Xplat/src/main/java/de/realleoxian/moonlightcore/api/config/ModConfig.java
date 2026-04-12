package de.realleoxian.moonlightcore.api.config;

import de.realleoxian.moonlightcore.api.config.schema.ConfigSchema;
import de.realleoxian.moonlightcore.impl.config.ModConfigImpl;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;
import java.util.Locale;
import java.util.function.Function;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface ModConfig {

    static <O> O configure(ModConfig.Type type, ResourceLocation id, Function<ConfigSchema.Builder, O> factory) {
        return ModConfigImpl.configure(type, id, factory);
    }

    void register();

    void loadIfNeeded();

    ConfigSchema getSchema();

    ResourceLocation getId();

    Type getType();

    Path getFilePath();

    enum Type {
        COMMON,
        CLIENT,
        SERVER
        ;

        public String getDiscriminator() {
            return name().toLowerCase(Locale.ROOT);
        }

        public boolean isSynced() {
            return this == SERVER;
        }
    }

}
