package de.leoxian.moonlightcore.common.util;

public enum TriState {
    TRUE,
    FALSE,
    DEFAULT
    ;

    public static TriState from(boolean value) {
        return value ? TRUE : FALSE;
    }

    public boolean toBoolean(boolean defaultValue) {
        return switch (this) {
            case TRUE -> true;
            case FALSE -> false;
            case DEFAULT -> defaultValue;
        };
    }

    public boolean isTrue() {
        return this == TRUE;
    }

    public boolean isFalse() {
        return this == FALSE;
    }

    public boolean isDefault() {
        return this == DEFAULT;
    }
}
