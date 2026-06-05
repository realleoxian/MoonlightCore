package de.realleoxian.moonlightcore.api.client.event;

import de.realleoxian.moonlightcore.api.event.Event;
import de.realleoxian.moonlightcore.api.event.EventBase;
import org.jetbrains.annotations.ApiStatus;

public final class InputEvents {
    public static final Event<Key> KEY = Event.create(Key.class);
    public static final Event<Mouse> MOUSE = Event.create(Mouse.class);

    private InputEvents() {}

    public static final class Key extends EventBase {
        public final int keyCode;
        public final int scanCode;
        public final int action;
        public final int modifiers;

        @ApiStatus.Internal
        public Key(int keyCode, int scanCode, int action, int modifiers) {
            this.keyCode = keyCode;
            this.scanCode = scanCode;
            this.action = action;
            this.modifiers = modifiers;
        }
    }

    public static final class Mouse extends EventBase {
        public final int button;
        public final int action;
        public final int modifiers;

        @ApiStatus.Internal
        public Mouse(int button, int action, int modifiers) {
            this.button = button;
            this.action = action;
            this.modifiers = modifiers;
        }
    }
}
