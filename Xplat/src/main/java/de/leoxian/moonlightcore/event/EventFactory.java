package de.leoxian.moonlightcore.event;

import com.google.common.reflect.AbstractInvocationHandler;
import de.leoxian.moonlightcore.util.nullness.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class EventFactory {

     public static <T> Event<T> of(Function<List<T>, T> factory) {
          return new Event<>(factory);
     }

     @SuppressWarnings("unchecked")
     public static <T> Event<T> create(Class<T> typeClass) {
          return of((listeners) -> (T) Proxy.newProxyInstance(EventFactory.class.getClassLoader(), new Class[]{typeClass}, new AbstractInvocationHandler() {
               @Override
               @Nullable
               protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
                    for(var listener : listeners) {
                         invokeMethod(listener, method, args);
                    }

                    return null;
               }
          }));
     }

     @SuppressWarnings("unchecked")
     public static <T> Event<T> createWithResult(Class<T> typeClass) {
          return of((listeners) -> (T) Proxy.newProxyInstance(EventFactory.class.getClassLoader(), new Class[]{typeClass}, new AbstractInvocationHandler() {
               @Override
               @Nullable
               protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
                    for(var listener : listeners) {
                         var result = (Event.Result) Objects.requireNonNull(invokeMethod(listener, method, args));
                         if(result.interruptsFurtherEvaluation) {
                              return result;
                         }
                    }

                    return Event.Result.PASS;
               }
          }));
     }

     @SuppressWarnings("unchecked")
     private static <T, R> R invokeMethod(T listener, Method method, Object[] args) throws Throwable {
          return (R) MethodHandles.lookup().unreflect(method).bindTo(listener).invokeWithArguments(args);
     }

     private EventFactory() {}

}
