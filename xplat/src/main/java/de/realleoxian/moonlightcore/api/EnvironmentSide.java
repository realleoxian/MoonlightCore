package de.realleoxian.moonlightcore.api;

public enum EnvironmentSide {
    CLIENT,
    DEDICATED_SERVER
    ;

    public boolean isClient() {
        return this == CLIENT;
    }
}
