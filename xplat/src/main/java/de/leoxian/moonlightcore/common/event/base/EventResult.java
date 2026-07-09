package de.leoxian.moonlightcore.common.event.base;

import de.leoxian.moonlightcore.common.util.TriState;

public enum EventResult {
    TRUE(TriState.TRUE, true),
    FALSE(TriState.FALSE, true),
    PASS(TriState.DEFAULT, false),
    SUCCESS(TriState.DEFAULT, true),
    DENY(TriState.DEFAULT, false)
    ;

    private final TriState value;
    private final boolean cancelFurtherEventProcessing;

    EventResult(TriState value, boolean cancelFurtherEventProcessing) {
        this.value = value;
        this.cancelFurtherEventProcessing = cancelFurtherEventProcessing;
    }

    public boolean isTrue() {
        return this.value.isTrue();
    }

    public boolean isFalse() {
        return this.value.isFalse();
    }

    public boolean isSuccess() {
        return this == SUCCESS;
    }

    public boolean isDeny() {
        return this == DENY;
    }

    public boolean cancelFurtherEventProcessing() {
        return this.cancelFurtherEventProcessing;
    }
}
