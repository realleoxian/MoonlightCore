package de.leoxian.moonlightcore.event;

import de.leoxian.moonlightcore.util.SortedLinkedList;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class Event<T> {
     private final Function<List<T>, T> factory;
     private final SortedLinkedList<Listener<T>> listeners;
     private T invoker = null;

     Event(Function<List<T>, T> factory) {
          this.factory = factory;
          this.listeners = new SortedLinkedList<>((a, b) -> b.priority().ordinal() - a.priority().ordinal());
     }

     public T invoker() {
          if(this.invoker == null) {
               this.invoker = this.factory.apply(this.listeners.stream().map(Listener::listener).toList());
          }

          return this.invoker;
     }

     public void subscribe(Event.Priority priority, T listener) {
          if(!this.listeners.contains(listener)) {
              this.listeners.add(new Listener<>(priority, listener));
              this.invoker = null;
          }
     }

     public void subscribe(T listener) {
          this.subscribe(Priority.NORMAL, listener);
     }

     public void unsubscribe(T listener) {
          this.listeners.remove(listener);
          this.invoker = null;
     }

     public boolean hasListener(T listener) {
          return this.listeners.contains(listener);
     }

     public enum Priority {
          HIGHEST,
          HIGH,
          NORMAL,
          LOW,
          LOWEST
     }

     public enum Result {
          TRUE(true, true),
          FALSE(true, false),
          PASS(false, null),
          STOP(true, null)
          ;

          public final boolean interruptsFurtherEvaluation;
          private final Boolean value;

          Result(boolean interruptsFurtherEvaluation, Boolean value) {
               this.interruptsFurtherEvaluation = interruptsFurtherEvaluation;
               this.value = value;
          }

          public boolean isFalse() {
               return BooleanUtils.isFalse(this.value);
          }

          public boolean isTrue() {
               return BooleanUtils.isTrue(this.value);
          }
     }

     private record Listener<T>(Event.Priority priority, T listener) {
          @Override
          public boolean equals(Object obj) {
               if(obj == this) {
                    return true;
               }
               if(obj instanceof Listener<?> that) {
                    return Objects.equals(this.listener, that.listener);
               } else if (obj != null) {
                    return obj.equals(this.listener);
               }

               return false;
          }

          @Override
          public int hashCode() {
               return Objects.hash(this.listener);
          }
     }
}
