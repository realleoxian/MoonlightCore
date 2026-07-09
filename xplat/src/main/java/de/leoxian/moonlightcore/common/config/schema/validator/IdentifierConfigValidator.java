package de.leoxian.moonlightcore.common.config.schema.validator;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public record IdentifierConfigValidator(@Nullable String validNamespace) implements ConfigValueValidator<Identifier> {
    public static final ConfigValueValidator<Identifier> ANY = new IdentifierConfigValidator(null);

    @Override
    public boolean test(Identifier value) {
        if (validNamespace() != null) {
            return value.getNamespace().equals(validNamespace());
        }
        return true;
    }

    @Override
    public Optional<String> getValidValueDescription() {
        if (validNamespace == null) return Optional.empty();
        return Optional.of("Any identifier with " + validNamespace + " as namespace");
    }
}
