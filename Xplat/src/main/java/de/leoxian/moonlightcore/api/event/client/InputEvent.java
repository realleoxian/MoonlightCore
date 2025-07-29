package de.leoxian.moonlightcore.api.event.client;

import de.leoxian.moonlightcore.api.event.Event;
import de.leoxian.moonlightcore.api.util.SidedEnvironment;

@SidedEnvironment(SidedEnvironment.Environment.CLIENT)
public interface InputEvent {
    /**
     * An event fired when a button from the keyboard is pressed
     */
    Event<KeyInput> KEY_INPUT = Event.create();
    /**
     * An event fired when a button from the mouse is pressed
     */
    Event<MouseInput> MOUSE_INPUT = Event.create();

    @FunctionalInterface
    interface KeyInput {
        void bootstrap(int key, int scanCode, int action, int modifiers);
    }

    @FunctionalInterface
    interface MouseInput {
        void bootstrap(int button, int action, int modifiers);
    }
}
