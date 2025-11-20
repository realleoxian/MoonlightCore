package de.leoxian.moonlightcore.util.nullness;

import java.lang.annotation.*;

/**
 * An alternative to {@link javax.annotation.Nullable} which works on type parameters
 */
@Target({ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@javax.annotation.Nullable
public @interface Nullable {}
