package de.realleoxian.moonlightcore.api.config.schema;

import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
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