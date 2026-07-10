package de.leoxian.moonlightcore.common;

import de.leoxian.moonlightcore.common.resource.ModResource;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@ApiStatus.NonExtendable
public interface ModContainer {
    ModMetadata metadata();

    ModResource resources();
}
