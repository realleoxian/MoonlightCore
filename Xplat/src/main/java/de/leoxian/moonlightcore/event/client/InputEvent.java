package de.leoxian.moonlightcore.event.client;


import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;

public interface InputEvent {
     /**
      * @see Key#onKeyInput(int, int, int, int)
      */
     Event<Key> KEY = EventFactory.create(Key.class);
     /**
      * @see Mouse#onMouseInput(int, int, int)
      */
     Event<Mouse> MOUSE = EventFactory.create(Mouse.class);

     interface Key {
          /**
           * Invoked after a keyboard's key is pressed
           * @param key The key code of the key pressed
           * @param scancode The scancode
           * @param action The action
           * @param modifiers The modifiers
           */
          void onKeyInput(int key, int scancode, int action, int modifiers);
     }

     interface Mouse {
          /**
           * Invoked after a mouse's key is pressed
           * @param button The button code of the button pressed
           * @param action The action
           * @param modifiers The modifiers
           */
          void onMouseInput(int button, int action, int modifiers);
     }
}
