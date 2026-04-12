package de.leoxian.moonlightcore.api.config.schema;

import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface ConfigSection {

    ConfigProperty<?> getProperty(String key);

    ConfigSection getSection(String key);

    Collection<ConfigProperty<?>> getProperties();

    Collection<ConfigSection> getSubSections();

    @Nullable
    Iterable<String> getComments();

    @Nullable
    String getTranslationKey();

    ConfigKey getKey();

}