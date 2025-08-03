package de.leowgc.moonlightcore.api.util;

import java.lang.annotation.*;

/**
 * This is an annotation used to mark what methods and/or classes are only one-sided (it can be client side or server side), it doesn't do anything, just a helper marker
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface SidedEnvironment {

    Environment value();

    enum Environment {
        SERVER,
        CLIENT
    }
}
