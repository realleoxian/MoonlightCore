package de.leoxian.moonlightcore.util.nullness;

import java.lang.annotation.*;

/**
 * A type-use alternative of {@link javax.annotation.Nonnull}
 */
@Target({ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@javax.annotation.Nonnull
public @interface Nonnull {
}
