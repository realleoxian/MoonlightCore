package de.leoxian.moonlightcore.util.nullness;

import javax.annotation.Nonnull;
import java.lang.annotation.*;

/**
 * An alternative to {@link javax.annotation.Nonnull} which works with type parameters
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Nonnull
public @interface NonnullType {
}
