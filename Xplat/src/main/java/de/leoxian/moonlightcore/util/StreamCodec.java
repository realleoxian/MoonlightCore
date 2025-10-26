package de.leoxian.moonlightcore.util;

import com.mojang.datafixers.util.*;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface StreamCodec<B, V> {

     static <B, V> StreamCodec<B, V> unit(V expectedValue) {
          return new StreamCodec<B, V>() {
               @Override
               public void encode(B buf, V value) {
                    if(!value.equals(expectedValue)) {
                         throw new IllegalStateException("Can't encode '" + value + "', expected '" + expectedValue + "'");
                    }
               }

               @Override
               public V decode(B buf) {
                    return expectedValue;
               }
          };
     }

     static <B, V> StreamCodec<B, V> of(BiConsumer<B, V> encoder, Function<B, V> decoder) {
          return new StreamCodec<B, V>() {
               @Override
               public void encode(B buf, V value) {
                    encoder.accept(buf, value);
               }

               @Override
               public V decode(B buf) {
                    return decoder.apply(buf);
               }
          };
     }

     static <B, V, T1> StreamCodec<B, V> composite(StreamCodec<? super B, T1> codec, Function<V, T1> getter, Function<T1, V> decoder) {
          return new StreamCodec<B, V>() {
               @Override
               public void encode(B buf, V value) {
                    codec.encode(buf, getter.apply(value));
               }

               @Override
               public V decode(B buf) {
                    T1 t1 = codec.decode(buf);
                    return decoder.apply(t1);
               }
          };
     }

     static <B, V, T1, T2> StreamCodec<B, V> composite(StreamCodec<? super B, T1> codec1, Function<V, T1> getter1, StreamCodec<? super B, T2> codec2, Function<V, T2> getter2, BiFunction<T1, T2, V> decoder) {
          return new StreamCodec<B, V>() {
               @Override
               public void encode(B buf, V value) {
                    codec1.encode(buf, getter1.apply(value));
                    codec2.encode(buf, getter2.apply(value));
               }

               @Override
               public V decode(B buf) {
                    T1 t1 = codec1.decode(buf);
                    T2 t2 = codec2.decode(buf);

                    return decoder.apply(t1, t2);
               }
          };
     }

     static <B, V, T1, T2, T3> StreamCodec<B, V> composite(StreamCodec<? super B, T1> codec1, Function<V, T1> getter1, StreamCodec<? super B, T2> codec2, Function<V, T2> getter2, StreamCodec<? super B, T3> codec3, Function<V, T3> getter3, Function3<T1, T2, T3, V> decoder) {
          return new StreamCodec<B, V>() {
               @Override
               public void encode(B buf, V value) {
                    codec1.encode(buf, getter1.apply(value));
                    codec2.encode(buf, getter2.apply(value));
                    codec3.encode(buf, getter3.apply(value));
               }

               @Override
               public V decode(B buf) {
                    T1 t1 = codec1.decode(buf);
                    T2 t2 = codec2.decode(buf);
                    T3 t3 = codec3.decode(buf);

                    return decoder.apply(t1, t2, t3);
               }
          };
     }

     static <B, V, T1, T2, T3, T4> StreamCodec<B, V> composite(StreamCodec<? super B, T1> codec1, Function<V, T1> getter1, StreamCodec<? super B, T2> codec2, Function<V, T2> getter2, StreamCodec<? super B, T3> codec3, Function<V, T3> getter3, StreamCodec<? super B, T4> codec4, Function<V, T4> getter4, Function4<T1, T2, T3, T4, V> decoder) {
          return new StreamCodec<B, V>() {
               @Override
               public void encode(B buf, V value) {
                    codec1.encode(buf, getter1.apply(value));
                    codec2.encode(buf, getter2.apply(value));
                    codec3.encode(buf, getter3.apply(value));
                    codec4.encode(buf, getter4.apply(value));
               }

               @Override
               public V decode(B buf) {
                    T1 t1 = codec1.decode(buf);
                    T2 t2 = codec2.decode(buf);
                    T3 t3 = codec3.decode(buf);
                    T4 t4 = codec4.decode(buf);

                    return decoder.apply(t1, t2, t3, t4);
               }
          };
     }

     static <B, V, T1, T2, T3, T4, T5> StreamCodec<B, V> composite(StreamCodec<? super B, T1> codec1, Function<V, T1> getter1, StreamCodec<? super B, T2> codec2, Function<V, T2> getter2, StreamCodec<? super B, T3> codec3, Function<V, T3> getter3, StreamCodec<? super B, T4> codec4, Function<V, T4> getter4, StreamCodec<? super B, T5> codec5, Function<V, T5> getter5, Function5<T1, T2, T3, T4, T5, V> decoder) {
          return new StreamCodec<B, V>() {
               @Override
               public void encode(B buf, V value) {
                    codec1.encode(buf, getter1.apply(value));
                    codec2.encode(buf, getter2.apply(value));
                    codec3.encode(buf, getter3.apply(value));
                    codec4.encode(buf, getter4.apply(value));
                    codec5.encode(buf, getter5.apply(value));
               }

               @Override
               public V decode(B buf) {
                    T1 t1 = codec1.decode(buf);
                    T2 t2 = codec2.decode(buf);
                    T3 t3 = codec3.decode(buf);
                    T4 t4 = codec4.decode(buf);
                    T5 t5 = codec5.decode(buf);

                    return decoder.apply(t1, t2, t3, t4, t5);
               }
          };
     }

     static <B, V, T1, T2, T3, T4, T5, T6> StreamCodec<B, V> composite(StreamCodec<? super B, T1> codec1, Function<V, T1> getter1, StreamCodec<? super B, T2> codec2, Function<V, T2> getter2, StreamCodec<? super B, T3> codec3, Function<V, T3> getter3, StreamCodec<? super B, T4> codec4, Function<V, T4> getter4, StreamCodec<? super B, T5> codec5, Function<V, T5> getter5, StreamCodec<? super B, T6> codec6, Function<V, T6> getter6, Function6<T1, T2, T3, T4, T5, T6, V> decoder) {
          return new StreamCodec<B, V>() {
               @Override
               public void encode(B buf, V value) {
                    codec1.encode(buf, getter1.apply(value));
                    codec2.encode(buf, getter2.apply(value));
                    codec3.encode(buf, getter3.apply(value));
                    codec4.encode(buf, getter4.apply(value));
                    codec5.encode(buf, getter5.apply(value));
                    codec6.encode(buf, getter6.apply(value));
               }

               @Override
               public V decode(B buf) {
                    T1 t1 = codec1.decode(buf);
                    T2 t2 = codec2.decode(buf);
                    T3 t3 = codec3.decode(buf);
                    T4 t4 = codec4.decode(buf);
                    T5 t5 = codec5.decode(buf);
                    T6 t6 = codec6.decode(buf);

                    return decoder.apply(t1, t2, t3, t4, t5, t6);
               }
          };
     }

     static <B, V, T1, T2, T3, T4, T5, T6, T7> StreamCodec<B, V> composite(StreamCodec<? super B, T1> codec1, Function<V, T1> getter1, StreamCodec<? super B, T2> codec2, Function<V, T2> getter2, StreamCodec<? super B, T3> codec3, Function<V, T3> getter3, StreamCodec<? super B, T4> codec4, Function<V, T4> getter4, StreamCodec<? super B, T5> codec5, Function<V, T5> getter5, StreamCodec<? super B, T6> codec6, Function<V, T6> getter6, StreamCodec<? super B, T7> codec7, Function<V, T7> getter7, Function7<T1, T2, T3, T4, T5, T6, T7, V> decoder) {
          return new StreamCodec<B, V>() {
               @Override
               public void encode(B buf, V value) {
                    codec1.encode(buf, getter1.apply(value));
                    codec2.encode(buf, getter2.apply(value));
                    codec3.encode(buf, getter3.apply(value));
                    codec4.encode(buf, getter4.apply(value));
                    codec5.encode(buf, getter5.apply(value));
                    codec6.encode(buf, getter6.apply(value));
                    codec7.encode(buf, getter7.apply(value));
               }

               @Override
               public V decode(B buf) {
                    T1 t1 = codec1.decode(buf);
                    T2 t2 = codec2.decode(buf);
                    T3 t3 = codec3.decode(buf);
                    T4 t4 = codec4.decode(buf);
                    T5 t5 = codec5.decode(buf);
                    T6 t6 = codec6.decode(buf);
                    T7 t7 = codec7.decode(buf);

                    return decoder.apply(t1, t2, t3, t4, t5, t6, t7);
               }
          };
     }

     static <B, V, T1, T2, T3, T4, T5, T6, T7, T8> StreamCodec<B, V> composite(StreamCodec<? super B, T1> codec1, Function<V, T1> getter1, StreamCodec<? super B, T2> codec2, Function<V, T2> getter2, StreamCodec<? super B, T3> codec3, Function<V, T3> getter3, StreamCodec<? super B, T4> codec4, Function<V, T4> getter4, StreamCodec<? super B, T5> codec5, Function<V, T5> getter5, StreamCodec<? super B, T6> codec6, Function<V, T6> getter6, StreamCodec<? super B, T7> codec7, Function<V, T7> getter7, StreamCodec<? super B, T8> codec8, Function<V, T8> getter8, Function8<T1, T2, T3, T4, T5, T6, T7, T8, V> decoder) {
          return new StreamCodec<B, V>() {
               @Override
               public void encode(B buf, V value) {
                    codec1.encode(buf, getter1.apply(value));
                    codec2.encode(buf, getter2.apply(value));
                    codec3.encode(buf, getter3.apply(value));
                    codec4.encode(buf, getter4.apply(value));
                    codec5.encode(buf, getter5.apply(value));
                    codec6.encode(buf, getter6.apply(value));
                    codec7.encode(buf, getter7.apply(value));
                    codec8.encode(buf, getter8.apply(value));
               }

               @Override
               public V decode(B buf) {
                    T1 t1 = codec1.decode(buf);
                    T2 t2 = codec2.decode(buf);
                    T3 t3 = codec3.decode(buf);
                    T4 t4 = codec4.decode(buf);
                    T5 t5 = codec5.decode(buf);
                    T6 t6 = codec6.decode(buf);
                    T7 t7 = codec7.decode(buf);
                    T8 t8 = codec8.decode(buf);

                    return decoder.apply(t1, t2, t3, t4, t5, t6, t7, t8);
               }
          };
     }

     static <B, V, T1, T2, T3, T4, T5, T6, T7, T8, T9> StreamCodec<B, V> composite(StreamCodec<? super B, T1> codec1, Function<V, T1> getter1, StreamCodec<? super B, T2> codec2, Function<V, T2> getter2, StreamCodec<? super B, T3> codec3, Function<V, T3> getter3, StreamCodec<? super B, T4> codec4, Function<V, T4> getter4, StreamCodec<? super B, T5> codec5, Function<V, T5> getter5, StreamCodec<? super B, T6> codec6, Function<V, T6> getter6, StreamCodec<? super B, T7> codec7, Function<V, T7> getter7, StreamCodec<? super B, T8> codec8, Function<V, T8> getter8, StreamCodec<? super B, T9> codec9, Function<V, T9> getter9, Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, V> decoder) {
          return new StreamCodec<B, V>() {
               @Override
               public void encode(B buf, V value) {
                    codec1.encode(buf, getter1.apply(value));
                    codec2.encode(buf, getter2.apply(value));
                    codec3.encode(buf, getter3.apply(value));
                    codec4.encode(buf, getter4.apply(value));
                    codec5.encode(buf, getter5.apply(value));
                    codec6.encode(buf, getter6.apply(value));
                    codec7.encode(buf, getter7.apply(value));
                    codec8.encode(buf, getter8.apply(value));
                    codec9.encode(buf, getter9.apply(value));
               }

               @Override
               public V decode(B buf) {
                    T1 t1 = codec1.decode(buf);
                    T2 t2 = codec2.decode(buf);
                    T3 t3 = codec3.decode(buf);
                    T4 t4 = codec4.decode(buf);
                    T5 t5 = codec5.decode(buf);
                    T6 t6 = codec6.decode(buf);
                    T7 t7 = codec7.decode(buf);
                    T8 t8 = codec8.decode(buf);
                    T9 t9 = codec9.decode(buf);

                    return decoder.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9);
               }
          };
     }

     static <B, V, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> StreamCodec<B, V> composite(StreamCodec<? super B, T1> codec1, Function<V, T1> getter1, StreamCodec<? super B, T2> codec2, Function<V, T2> getter2, StreamCodec<? super B, T3> codec3, Function<V, T3> getter3, StreamCodec<? super B, T4> codec4, Function<V, T4> getter4, StreamCodec<? super B, T5> codec5, Function<V, T5> getter5, StreamCodec<? super B, T6> codec6, Function<V, T6> getter6, StreamCodec<? super B, T7> codec7, Function<V, T7> getter7, StreamCodec<? super B, T8> codec8, Function<V, T8> getter8, StreamCodec<? super B, T9> codec9, Function<V, T9> getter9, StreamCodec<? super B, T10> codec10, Function<V, T10> getter10, Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, V> decoder) {
          return new StreamCodec<B, V>() {
               @Override
               public void encode(B buf, V value) {
                    codec1.encode(buf, getter1.apply(value));
                    codec2.encode(buf, getter2.apply(value));
                    codec3.encode(buf, getter3.apply(value));
                    codec4.encode(buf, getter4.apply(value));
                    codec5.encode(buf, getter5.apply(value));
                    codec6.encode(buf, getter6.apply(value));
                    codec7.encode(buf, getter7.apply(value));
                    codec8.encode(buf, getter8.apply(value));
                    codec9.encode(buf, getter9.apply(value));
                    codec10.encode(buf, getter10.apply(value));
               }

               @Override
               public V decode(B buf) {
                    T1 t1 = codec1.decode(buf);
                    T2 t2 = codec2.decode(buf);
                    T3 t3 = codec3.decode(buf);
                    T4 t4 = codec4.decode(buf);
                    T5 t5 = codec5.decode(buf);
                    T6 t6 = codec6.decode(buf);
                    T7 t7 = codec7.decode(buf);
                    T8 t8 = codec8.decode(buf);
                    T9 t9 = codec9.decode(buf);
                    T10 t10 = codec10.decode(buf);

                    return decoder.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10);
               }
          };
     }

     static <B, V, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> StreamCodec<B, V> composite(StreamCodec<? super B, T1> codec1, Function<V, T1> getter1, StreamCodec<? super B, T2> codec2, Function<V, T2> getter2, StreamCodec<? super B, T3> codec3, Function<V, T3> getter3, StreamCodec<? super B, T4> codec4, Function<V, T4> getter4, StreamCodec<? super B, T5> codec5, Function<V, T5> getter5, StreamCodec<? super B, T6> codec6, Function<V, T6> getter6, StreamCodec<? super B, T7> codec7, Function<V, T7> getter7, StreamCodec<? super B, T8> codec8, Function<V, T8> getter8, StreamCodec<? super B, T9> codec9, Function<V, T9> getter9, StreamCodec<? super B, T10> codec10, Function<V, T10> getter10, StreamCodec<? super B, T11> codec11, Function<V, T11> getter11, Function11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, V> decoder) {
          return new StreamCodec<B, V>() {
               @Override
               public void encode(B buf, V value) {
                    codec1.encode(buf, getter1.apply(value));
                    codec2.encode(buf, getter2.apply(value));
                    codec3.encode(buf, getter3.apply(value));
                    codec4.encode(buf, getter4.apply(value));
                    codec5.encode(buf, getter5.apply(value));
                    codec6.encode(buf, getter6.apply(value));
                    codec7.encode(buf, getter7.apply(value));
                    codec8.encode(buf, getter8.apply(value));
                    codec9.encode(buf, getter9.apply(value));
                    codec10.encode(buf, getter10.apply(value));
                    codec11.encode(buf, getter11.apply(value));
               }

               @Override
               public V decode(B buf) {
                    T1 t1 = codec1.decode(buf);
                    T2 t2 = codec2.decode(buf);
                    T3 t3 = codec3.decode(buf);
                    T4 t4 = codec4.decode(buf);
                    T5 t5 = codec5.decode(buf);
                    T6 t6 = codec6.decode(buf);
                    T7 t7 = codec7.decode(buf);
                    T8 t8 = codec8.decode(buf);
                    T9 t9 = codec9.decode(buf);
                    T10 t10 = codec10.decode(buf);
                    T11 t11 = codec11.decode(buf);

                    return decoder.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
               }
          };
     }

     static <B, V, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> StreamCodec<B, V> composite(StreamCodec<? super B, T1> codec1, Function<V, T1> getter1, StreamCodec<? super B, T2> codec2, Function<V, T2> getter2, StreamCodec<? super B, T3> codec3, Function<V, T3> getter3, StreamCodec<? super B, T4> codec4, Function<V, T4> getter4, StreamCodec<? super B, T5> codec5, Function<V, T5> getter5, StreamCodec<? super B, T6> codec6, Function<V, T6> getter6, StreamCodec<? super B, T7> codec7, Function<V, T7> getter7, StreamCodec<? super B, T8> codec8, Function<V, T8> getter8, StreamCodec<? super B, T9> codec9, Function<V, T9> getter9, StreamCodec<? super B, T10> codec10, Function<V, T10> getter10, StreamCodec<? super B, T11> codec11, Function<V, T11> getter11, StreamCodec<? super B, T12> codec12, Function<V, T12> getter12, Function12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, V> decoder) {
          return new StreamCodec<B, V>() {
               @Override
               public void encode(B buf, V value) {
                    codec1.encode(buf, getter1.apply(value));
                    codec2.encode(buf, getter2.apply(value));
                    codec3.encode(buf, getter3.apply(value));
                    codec4.encode(buf, getter4.apply(value));
                    codec5.encode(buf, getter5.apply(value));
                    codec6.encode(buf, getter6.apply(value));
                    codec7.encode(buf, getter7.apply(value));
                    codec8.encode(buf, getter8.apply(value));
                    codec9.encode(buf, getter9.apply(value));
                    codec10.encode(buf, getter10.apply(value));
                    codec11.encode(buf, getter11.apply(value));
                    codec12.encode(buf, getter12.apply(value));
               }

               @Override
               public V decode(B buf) {
                    T1 t1 = codec1.decode(buf);
                    T2 t2 = codec2.decode(buf);
                    T3 t3 = codec3.decode(buf);
                    T4 t4 = codec4.decode(buf);
                    T5 t5 = codec5.decode(buf);
                    T6 t6 = codec6.decode(buf);
                    T7 t7 = codec7.decode(buf);
                    T8 t8 = codec8.decode(buf);
                    T9 t9 = codec9.decode(buf);
                    T10 t10 = codec10.decode(buf);
                    T11 t11 = codec11.decode(buf);
                    T12 t12 = codec12.decode(buf);

                    return decoder.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12);
               }
          };
     }

     static <B, V, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> StreamCodec<B, V> composite(StreamCodec<? super B, T1> codec1, Function<V, T1> getter1, StreamCodec<? super B, T2> codec2, Function<V, T2> getter2, StreamCodec<? super B, T3> codec3, Function<V, T3> getter3, StreamCodec<? super B, T4> codec4, Function<V, T4> getter4, StreamCodec<? super B, T5> codec5, Function<V, T5> getter5, StreamCodec<? super B, T6> codec6, Function<V, T6> getter6, StreamCodec<? super B, T7> codec7, Function<V, T7> getter7, StreamCodec<? super B, T8> codec8, Function<V, T8> getter8, StreamCodec<? super B, T9> codec9, Function<V, T9> getter9, StreamCodec<? super B, T10> codec10, Function<V, T10> getter10, StreamCodec<? super B, T11> codec11, Function<V, T11> getter11, StreamCodec<? super B, T12> codec12, Function<V, T12> getter12, StreamCodec<? super B, T13> codec13, Function<V, T13> getter13, Function13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, V> decoder) {
          return new StreamCodec<B, V>() {
               @Override
               public void encode(B buf, V value) {
                    codec1.encode(buf, getter1.apply(value));
                    codec2.encode(buf, getter2.apply(value));
                    codec3.encode(buf, getter3.apply(value));
                    codec4.encode(buf, getter4.apply(value));
                    codec5.encode(buf, getter5.apply(value));
                    codec6.encode(buf, getter6.apply(value));
                    codec7.encode(buf, getter7.apply(value));
                    codec8.encode(buf, getter8.apply(value));
                    codec9.encode(buf, getter9.apply(value));
                    codec10.encode(buf, getter10.apply(value));
                    codec11.encode(buf, getter11.apply(value));
                    codec12.encode(buf, getter12.apply(value));
                    codec13.encode(buf, getter13.apply(value));
               }

               @Override
               public V decode(B buf) {
                    T1 t1 = codec1.decode(buf);
                    T2 t2 = codec2.decode(buf);
                    T3 t3 = codec3.decode(buf);
                    T4 t4 = codec4.decode(buf);
                    T5 t5 = codec5.decode(buf);
                    T6 t6 = codec6.decode(buf);
                    T7 t7 = codec7.decode(buf);
                    T8 t8 = codec8.decode(buf);
                    T9 t9 = codec9.decode(buf);
                    T10 t10 = codec10.decode(buf);
                    T11 t11 = codec11.decode(buf);
                    T12 t12 = codec12.decode(buf);
                    T13 t13 = codec13.decode(buf);

                    return decoder.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13);
               }
          };
     }

     static <B, V, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> StreamCodec<B, V> composite(StreamCodec<? super B, T1> codec1, Function<V, T1> getter1, StreamCodec<? super B, T2> codec2, Function<V, T2> getter2, StreamCodec<? super B, T3> codec3, Function<V, T3> getter3, StreamCodec<? super B, T4> codec4, Function<V, T4> getter4, StreamCodec<? super B, T5> codec5, Function<V, T5> getter5, StreamCodec<? super B, T6> codec6, Function<V, T6> getter6, StreamCodec<? super B, T7> codec7, Function<V, T7> getter7, StreamCodec<? super B, T8> codec8, Function<V, T8> getter8, StreamCodec<? super B, T9> codec9, Function<V, T9> getter9, StreamCodec<? super B, T10> codec10, Function<V, T10> getter10, StreamCodec<? super B, T11> codec11, Function<V, T11> getter11, StreamCodec<? super B, T12> codec12, Function<V, T12> getter12, StreamCodec<? super B, T13> codec13, Function<V, T13> getter13, StreamCodec<? super B, T14> codec14, Function<V, T14> getter14, Function14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, V> decoder) {
          return new StreamCodec<B, V>() {
               @Override
               public void encode(B buf, V value) {
                    codec1.encode(buf, getter1.apply(value));
                    codec2.encode(buf, getter2.apply(value));
                    codec3.encode(buf, getter3.apply(value));
                    codec4.encode(buf, getter4.apply(value));
                    codec5.encode(buf, getter5.apply(value));
                    codec6.encode(buf, getter6.apply(value));
                    codec7.encode(buf, getter7.apply(value));
                    codec8.encode(buf, getter8.apply(value));
                    codec9.encode(buf, getter9.apply(value));
                    codec10.encode(buf, getter10.apply(value));
                    codec11.encode(buf, getter11.apply(value));
                    codec12.encode(buf, getter12.apply(value));
                    codec13.encode(buf, getter13.apply(value));
                    codec14.encode(buf, getter14.apply(value));
               }

               @Override
               public V decode(B buf) {
                    T1 t1 = codec1.decode(buf);
                    T2 t2 = codec2.decode(buf);
                    T3 t3 = codec3.decode(buf);
                    T4 t4 = codec4.decode(buf);
                    T5 t5 = codec5.decode(buf);
                    T6 t6 = codec6.decode(buf);
                    T7 t7 = codec7.decode(buf);
                    T8 t8 = codec8.decode(buf);
                    T9 t9 = codec9.decode(buf);
                    T10 t10 = codec10.decode(buf);
                    T11 t11 = codec11.decode(buf);
                    T12 t12 = codec12.decode(buf);
                    T13 t13 = codec13.decode(buf);
                    T14 t14 = codec14.decode(buf);

                    return decoder.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14);
               }
          };
     }

     static <B, V, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> StreamCodec<B, V> composite(StreamCodec<? super B, T1> codec1, Function<V, T1> getter1, StreamCodec<? super B, T2> codec2, Function<V, T2> getter2, StreamCodec<? super B, T3> codec3, Function<V, T3> getter3, StreamCodec<? super B, T4> codec4, Function<V, T4> getter4, StreamCodec<? super B, T5> codec5, Function<V, T5> getter5, StreamCodec<? super B, T6> codec6, Function<V, T6> getter6, StreamCodec<? super B, T7> codec7, Function<V, T7> getter7, StreamCodec<? super B, T8> codec8, Function<V, T8> getter8, StreamCodec<? super B, T9> codec9, Function<V, T9> getter9, StreamCodec<? super B, T10> codec10, Function<V, T10> getter10, StreamCodec<? super B, T11> codec11, Function<V, T11> getter11, StreamCodec<? super B, T12> codec12, Function<V, T12> getter12, StreamCodec<? super B, T13> codec13, Function<V, T13> getter13, StreamCodec<? super B, T14> codec14, Function<V, T14> getter14, StreamCodec<? super B, T15> codec15, Function<V, T15> getter15, Function15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, V> decoder) {
          return new StreamCodec<B, V>() {
               @Override
               public void encode(B buf, V value) {
                    codec1.encode(buf, getter1.apply(value));
                    codec2.encode(buf, getter2.apply(value));
                    codec3.encode(buf, getter3.apply(value));
                    codec4.encode(buf, getter4.apply(value));
                    codec5.encode(buf, getter5.apply(value));
                    codec6.encode(buf, getter6.apply(value));
                    codec7.encode(buf, getter7.apply(value));
                    codec8.encode(buf, getter8.apply(value));
                    codec9.encode(buf, getter9.apply(value));
                    codec10.encode(buf, getter10.apply(value));
                    codec11.encode(buf, getter11.apply(value));
                    codec12.encode(buf, getter12.apply(value));
                    codec13.encode(buf, getter13.apply(value));
                    codec14.encode(buf, getter14.apply(value));
                    codec15.encode(buf, getter15.apply(value));
               }

               @Override
               public V decode(B buf) {
                    T1 t1 = codec1.decode(buf);
                    T2 t2 = codec2.decode(buf);
                    T3 t3 = codec3.decode(buf);
                    T4 t4 = codec4.decode(buf);
                    T5 t5 = codec5.decode(buf);
                    T6 t6 = codec6.decode(buf);
                    T7 t7 = codec7.decode(buf);
                    T8 t8 = codec8.decode(buf);
                    T9 t9 = codec9.decode(buf);
                    T10 t10 = codec10.decode(buf);
                    T11 t11 = codec11.decode(buf);
                    T12 t12 = codec12.decode(buf);
                    T13 t13 = codec13.decode(buf);
                    T14 t14 = codec14.decode(buf);
                    T15 t15 = codec15.decode(buf);

                    return decoder.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15);
               }
          };
     }

     static <B, V, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> StreamCodec<B, V> composite(StreamCodec<? super B, T1> codec1, Function<V, T1> getter1, StreamCodec<? super B, T2> codec2, Function<V, T2> getter2, StreamCodec<? super B, T3> codec3, Function<V, T3> getter3, StreamCodec<? super B, T4> codec4, Function<V, T4> getter4, StreamCodec<? super B, T5> codec5, Function<V, T5> getter5, StreamCodec<? super B, T6> codec6, Function<V, T6> getter6, StreamCodec<? super B, T7> codec7, Function<V, T7> getter7, StreamCodec<? super B, T8> codec8, Function<V, T8> getter8, StreamCodec<? super B, T9> codec9, Function<V, T9> getter9, StreamCodec<? super B, T10> codec10, Function<V, T10> getter10, StreamCodec<? super B, T11> codec11, Function<V, T11> getter11, StreamCodec<? super B, T12> codec12, Function<V, T12> getter12, StreamCodec<? super B, T13> codec13, Function<V, T13> getter13, StreamCodec<? super B, T14> codec14, Function<V, T14> getter14, StreamCodec<? super B, T15> codec15, Function<V, T15> getter15, StreamCodec<? super B, T16> codec16, Function<V, T16> getter16, Function16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, V> decoder) {
          return new StreamCodec<B, V>() {
               @Override
               public void encode(B buf, V value) {
                    codec1.encode(buf, getter1.apply(value));
                    codec2.encode(buf, getter2.apply(value));
                    codec3.encode(buf, getter3.apply(value));
                    codec4.encode(buf, getter4.apply(value));
                    codec5.encode(buf, getter5.apply(value));
                    codec6.encode(buf, getter6.apply(value));
                    codec7.encode(buf, getter7.apply(value));
                    codec8.encode(buf, getter8.apply(value));
                    codec9.encode(buf, getter9.apply(value));
                    codec10.encode(buf, getter10.apply(value));
                    codec11.encode(buf, getter11.apply(value));
                    codec12.encode(buf, getter12.apply(value));
                    codec13.encode(buf, getter13.apply(value));
                    codec14.encode(buf, getter14.apply(value));
                    codec15.encode(buf, getter15.apply(value));
                    codec16.encode(buf, getter16.apply(value));
               }

               @Override
               public V decode(B buf) {
                    T1 t1 = codec1.decode(buf);
                    T2 t2 = codec2.decode(buf);
                    T3 t3 = codec3.decode(buf);
                    T4 t4 = codec4.decode(buf);
                    T5 t5 = codec5.decode(buf);
                    T6 t6 = codec6.decode(buf);
                    T7 t7 = codec7.decode(buf);
                    T8 t8 = codec8.decode(buf);
                    T9 t9 = codec9.decode(buf);
                    T10 t10 = codec10.decode(buf);
                    T11 t11 = codec11.decode(buf);
                    T12 t12 = codec12.decode(buf);
                    T13 t13 = codec13.decode(buf);
                    T14 t14 = codec14.decode(buf);
                    T15 t15 = codec15.decode(buf);
                    T16 t16 = codec16.decode(buf);

                    return decoder.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16);
               }
          };
     }

     void encode(B buf, V value);

     V decode(B buf);

     default <O> StreamCodec<B, O> map(StreamCodec.ResultFunction<B, V, O> function){
         return function.apply(this);
     }

     default <O> StreamCodec<B, O> xmap(Function<? super V, ? extends O> factory, final Function<? super O, ? extends V> getter) {
          return new StreamCodec<B, O>() {
               @Override
               public void encode(B buf, O value) {
                    StreamCodec.this.encode(buf, getter.apply(value));
               }

               @Override
               public O decode(B buf) {
                    return factory.apply(StreamCodec.this.decode(buf));
               }
          };
     }

     @SuppressWarnings("unchecked")
     default <U> StreamCodec<B, U> dispatch(Function<? super U, ? extends  V> type, Function<? super V, ? extends StreamCodec<? super B, ? extends U>> codec) {
         return new StreamCodec<B, U>() {
             @Override
             public void encode(B buf, U value) {
                 V i = type.apply(value);
                StreamCodec<B, U> j = (StreamCodec<B, U>) codec.apply(i);

                StreamCodec.this.encode(buf, i);
                j.encode(buf, value);
             }

             @Override
             public U decode(B buf) {
                 V i = StreamCodec.this.decode(buf);
                 return codec.apply(i).decode(buf);
             }
         };
     }

     @FunctionalInterface
     interface ResultFunction<B, S, T> {
         StreamCodec<B, T> apply(StreamCodec<B, S> codec);
     }
}
