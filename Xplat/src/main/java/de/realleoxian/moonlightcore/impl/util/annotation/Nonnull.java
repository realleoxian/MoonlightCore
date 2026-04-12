package de.realleoxian.moonlightcore.impl.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
@javax.annotation.Nonnull
public @interface Nonnull {
}
