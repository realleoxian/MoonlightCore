package de.leoxian.moonlightcore.api.client.event;

import de.leoxian.moonlightcore.api.event.EventBus;

public final class InputEvents {
    public static final EventBus<KeyInput> KEY_INPUT = EventBus.create((listeners) -> (keyCode, action, modifier, scancode) -> {
        for (KeyInput listener : listeners) {
            listener.onKeyInput(keyCode, action, modifier, scancode);
        }
    });
    public static final EventBus<MouseInput> MOUSE_INPUT = EventBus.create((listeners) -> (button, action, modifiers) -> {
       for (MouseInput listener : listeners) {
           listener.onMouseInput(button, action, modifiers);
       }
    });

    private InputEvents() {}

    public interface KeyInput {
        void onKeyInput(int keyCode, int action, int modifier, int scancode);
    }

    public interface MouseInput {
        void onMouseInput(int button, int action, int modifiers);
    }
}
