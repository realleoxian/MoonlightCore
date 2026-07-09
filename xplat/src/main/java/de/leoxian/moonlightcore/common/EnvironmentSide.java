package de.leoxian.moonlightcore.common;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;

public enum EnvironmentSide {
    CLIENT,
    SERVER
    ;

    static EnvironmentSide current() {
        return XplatAbstraction.INSTANCE.getEnvironmentSide();
    }
}
