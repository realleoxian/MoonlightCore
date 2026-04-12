package de.leoxian.moonlightcore.api.event;

import org.apache.commons.lang3.BooleanUtils;
import org.jetbrains.annotations.Nullable;

public enum EventResult {
    TRUE(true, true),
    FALSE(true, false),
    CANCEL(true, null),
    PASS(false, null)
    ;

    public final boolean cancelFurtherProcessing;
    private final Boolean value;

    EventResult(boolean cancelFurtherProcessing, @Nullable Boolean value) {
        this.cancelFurtherProcessing = cancelFurtherProcessing;
        this.value = value;
    }

    public boolean isFalse() {
        return BooleanUtils.isFalse(value);
    }

    public boolean isTrue() {
        return BooleanUtils.isTrue(value);
    }
}
