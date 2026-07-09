package de.leoxian.moonlightcore.client.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import de.leoxian.moonlightcore.common.event.base.EventResult;

public final class InputEvents {
    public static final Event<Key.Pre> PRE_KEY_PRESS = Event.create(Key.Pre.class, listeners -> (key, scancode, modifiers, action) -> {
       var result = EventResult.TRUE;
       for (final var listener : listeners) {
           result = listener.onPreMouseInput(key, scancode, modifiers, action);
           if (result.cancelFurtherEventProcessing()) {
               break;
           }
       }
       return result;
    });
    public static final Event<Key.Post> POST_KEY_PRESS = Event.create(Key.Post.class, listeners -> (key, scancode, modifiers, action) -> {
       for (final var listener : listeners) {
           listener.onPostKeyInput(key, scancode, modifiers, action);
       }
    });

    public static final Event<Mouse.Pre> PRE_MOUSE_INPUT = Event.create(Mouse.Pre.class, listeners -> (button, modifiers, action) -> {
       var result = EventResult.TRUE;
       for (final var listener : listeners) {
           result = listener.onPreMouseInput(button, modifiers, action);
           if (result.cancelFurtherEventProcessing()) {
               break;
           }
       }
       return result;
    });
    public static final Event<Mouse.Post> POST_MOUSE_INPUT = Event.create(Mouse.Post.class, listeners -> (button, modifiers, action) -> {
       for (final var listener : listeners) {
           listener.onPostMouseInput(button, modifiers, action);
       }
    });
    public static final Event<Mouse.Scroll> MOUSE_SCROLL = Event.create(Mouse.Scroll.class, listeners -> (scrollDeltaX, scrollDeltaY, leftDown, middleDown, rightDown, mouseX, mouseY) -> {
       var result = EventResult.TRUE;
       for (final var listener : listeners) {
           result = listener.onMouseScroll(scrollDeltaX, scrollDeltaY, leftDown, middleDown, rightDown, mouseX, mouseY);
           if (result.cancelFurtherEventProcessing()) {
               break;
           }
       }
       return result;
    });

    private InputEvents() {}


    public static final class Key {
        @FunctionalInterface
        public interface Pre {
            EventResult onPreMouseInput(int key, int scancode, int modifiers, int action);
        }

        @FunctionalInterface
        public interface Post {
            void onPostKeyInput(int key, int scancode, int modifiers, int action);
        }

        private Key() {}
    }

    public static final class Mouse {
        @FunctionalInterface
        public interface Pre {
            EventResult onPreMouseInput(int button, int modifiers, int action);
        }

        @FunctionalInterface
        public interface Post {
            void onPostMouseInput(int button, int modifiers, int action);
        }

        @FunctionalInterface
        public interface Scroll {
            EventResult onMouseScroll(double scrollDeltaX, double scrollDeltaY, boolean leftDown, boolean middleDown, boolean rightDown, double mouseX, double mouseY);
        }

        private Mouse() {}
    }
}
