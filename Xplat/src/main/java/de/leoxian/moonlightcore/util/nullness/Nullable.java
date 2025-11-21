package de.leoxian.moonlightcore.util.nullness;

import java.lang.annotation.*;

/**
 * A type-use alternative of {@link javax.annotation.Nullable}
 */
@Target({ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@javax.annotation.Nullable
public @interface Nullable {}
