package de.leowgc.moonlightcore.api.util;

import java.lang.annotation.*;

/**
 * This is an annotation used to mark what methods and/or classes expects a loader/platform implementation, it doesn't do anything by itself, just as a help marker
 * @since 4.0.0
 * @author Leoxian
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ExpectPlatform {

    Platform value() default Platform.ALL;

    enum Platform {
        ALL,
        FORGE,
        FABRIC,
        NEOFORGE
    }

}
