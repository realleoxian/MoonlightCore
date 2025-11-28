/*
 * This source code file is subject to the terms of the Mozilla Public License, v. 2.0.
 * Based on code from Tterrag1098's Registrate (https://github.com/tterrag1098/Registrate).
 */
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
