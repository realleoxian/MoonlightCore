package de.leoxian.moonlightcore.util;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.IntFunction;

public interface ByteBufCodecs {
    int SERVERBOUND_MAX_PAYLOAD_SIZE = 32767;

    StreamCodec<ByteBuf, Boolean> BOOLEAN = new StreamCodec<ByteBuf, Boolean>() {
        @Override
        public void encode(ByteBuf buf, Boolean value) {
            buf.writeBoolean(value);
        }

        @Override
        public Boolean decode(ByteBuf buf) {
            return buf.readBoolean();
        }
    };

    StreamCodec<ByteBuf, Byte> BYTE = new StreamCodec<ByteBuf, Byte>() {
        @Override
        public void encode(ByteBuf buf, Byte value) {
            buf.writeByte(value);
        }

        @Override
        public Byte decode(ByteBuf buf) {
            return buf.readByte();
        }
    };

    StreamCodec<ByteBuf, Float> DEGREES = BYTE.xmap((b) -> b * 360 / 256.0F, (f) -> (byte) Mth.floor(f  * 256.0F / 360.0F));

    StreamCodec<ByteBuf, Short> SHORT = new StreamCodec<ByteBuf, Short>() {
        @Override
        public void encode(ByteBuf buf, Short value) {
            buf.writeShort(value);
        }

        @Override
        public Short decode(ByteBuf buf) {
            return buf.readShort();
        }
    };

    StreamCodec<ByteBuf, Integer> UNSIGNED_SHORT = new StreamCodec<ByteBuf, Integer>() {
        @Override
        public void encode(ByteBuf buf, Integer value) {
            buf.writeShort(value);
        }

        @Override
        public Integer decode(ByteBuf buf) {
            return buf.readUnsignedShort();
        }
    };

    StreamCodec<ByteBuf, Integer> INTEGER = new StreamCodec<ByteBuf, Integer>() {
        @Override
        public void encode(ByteBuf buf, Integer value) {
            buf.writeInt(value);
        }

        @Override
        public Integer decode(ByteBuf buf) {
            return buf.readInt();
        }
    };

    StreamCodec<ByteBuf, Integer> VAR_INT = new StreamCodec<ByteBuf, Integer>() {
        @Override
        public void encode(ByteBuf buf, Integer value) {
            while((value & -128) != 0) {
                buf.writeByte(value & 127 | 128);
                value >>>= 7;
            }

            buf.writeByte(value);
        }

        @Override
        public Integer decode(ByteBuf buf) {
            int i = 0;
            int j = 0;

            byte b;
            do {
                b = buf.readByte();
                i |= (b & 127) << j++ * 7;

                if(j > 5) {
                    throw new RuntimeException("VarInt too big");
                }
            } while((b & 128) == 128);

            return i;
        }
    };

    StreamCodec<ByteBuf, Long> VAR_LONG = new StreamCodec<ByteBuf, Long>() {
        @Override
        public void encode(ByteBuf buf, Long value) {
            while((value & -128L) != 0L){
                buf.writeByte((int) (value & 127L) | 128);
                value >>>= 7;
            }

            buf.writeByte((int) (long) value);
        }

        @Override
        public Long decode(ByteBuf buf) {
            long i = 0L;
            int j = 0;

            byte b;
            do {
                b = buf.readByte();
                i |= (long) (b & 127) << j++ * 7;

                if(j > 10) {
                    throw new RuntimeException("VarLong too big");
                }
            } while ((b & 128) == 128);

            return i;
        }
    };

    StreamCodec<ByteBuf, Long> LONG = new StreamCodec<ByteBuf, Long>() {
        @Override
        public void encode(ByteBuf buf, Long value) {
            buf.writeLong(value);
        }

        @Override
        public Long decode(ByteBuf buf) {
            return buf.readLong();
        }
    };

    StreamCodec<ByteBuf, Float> FLOAT = new StreamCodec<ByteBuf, Float>() {
        @Override
        public void encode(ByteBuf buf, Float value) {
            buf.writeFloat(value);
        }

        @Override
        public Float decode(ByteBuf buf) {
            return buf.readFloat();
        }
    };

    StreamCodec<ByteBuf, Double> DOUBLE = new StreamCodec<ByteBuf, Double>() {
        @Override
        public void encode(ByteBuf buf, Double value) {
            buf.writeDouble(value);
        }

        @Override
        public Double decode(ByteBuf buf) {
            return buf.readDouble();
        }
    };

    StreamCodec<ByteBuf, byte[]> BYTE_ARRAY = new StreamCodec<ByteBuf, byte[]>() {
        @Override
        public void encode(ByteBuf buf, byte[] value) {
            VAR_INT.encode(buf, value.length);
            buf.writeBytes(value);
        }

        @Override
        public byte[] decode(ByteBuf buf) {
            int length = VAR_INT.decode(buf);
            byte[] ba = new byte[length];
            buf.readBytes(ba);

            return ba;
        }
    };

    StreamCodec<ByteBuf, long[]> LONG_ARRAY = new StreamCodec<ByteBuf, long[]>() {
        @Override
        public void encode(ByteBuf buf, long[] value) {
            VAR_INT.encode(buf, value.length);

            for(long l : value) {
                buf.readLong();
            }
        }

        @Override
        public long[] decode(ByteBuf buf) {
            int length = VAR_INT.decode(buf);
            long[] la = new long[length];

            for(int i = 0; i < length; i++) {
                la[i] = buf.readLong();
            }

            return la;
        }
    };

    StreamCodec<ByteBuf, String> STRING_UTF8 = stringUTF8(32767);

    StreamCodec<ByteBuf, Vector3fc> VECTOR_3FC = new StreamCodec<ByteBuf, Vector3fc>() {
        @Override
        public void encode(ByteBuf buf, Vector3fc value) {
            buf.writeFloat(value.x());
            buf.writeFloat(value.y());
            buf.writeFloat(value.z());
        }

        @Override
        public Vector3fc decode(ByteBuf buf) {
            float x = buf.readFloat();
            float y = buf.readFloat();
            float z = buf.readFloat();

            return new Vector3f(x, y, z);
        }
    };

    StreamCodec<ByteBuf, Quaternionfc> QUATERNION_FC = new StreamCodec<ByteBuf, Quaternionfc>() {
        @Override
        public void encode(ByteBuf buf, Quaternionfc value) {
            buf.writeFloat(value.x());
            buf.writeFloat(value.y());
            buf.writeFloat(value.z());
            buf.writeFloat(value.w());
        }

        @Override
        public Quaternionfc decode(ByteBuf buf) {
            float x = buf.readFloat();
            float y = buf.readFloat();
            float z = buf.readFloat();
            float w = buf.readFloat();

            return new Quaternionf(x, y, z, w);
        }
    };

    StreamCodec<ByteBuf, ResourceLocation> RESOURCE_LOCATION = STRING_UTF8.xmap(ResourceLocation::new, ResourceLocation::toString);

    StreamCodec<ByteBuf, BlockPos> BLOCK_POS = LONG.xmap(BlockPos::of, BlockPos::asLong);

    static <T> StreamCodec<ByteBuf, ResourceKey<T>> resourceKey(ResourceKey<? extends Registry<T>> registry) {
        return RESOURCE_LOCATION.xmap(id -> ResourceKey.create(registry, id), ResourceKey::location);
    }

    static StreamCodec<ByteBuf, String> stringUTF8(int maxLength) {
        return new StreamCodec<ByteBuf, String>() {
            @Override
            public void encode(ByteBuf buf, String value) {
                if(value.length() > maxLength) {
                    throw new EncoderException("String length was bigger than max length, can't encode.\n  - Max length: %s\n  - String length: %s".formatted(maxLength, value.length()));
                }

                byte[] array = value.getBytes(StandardCharsets.UTF_8);
                int maxEncodedUTFLength = maxLength * 3;
                if(array.length > maxEncodedUTFLength) {
                    throw new EncoderException("String too big (was " + array.length + " bytes encoded, max " + maxEncodedUTFLength + ")");
                }

                VAR_INT.encode(buf, array.length);
                buf.writeBytes(array);
            }

            @Override
            public String decode(ByteBuf buf) {
                int maxEncodedUTFLength = maxLength * 3;
                int length = VAR_INT.decode(buf);

                if(length > maxEncodedUTFLength) {
                    throw new DecoderException("The received encoded string buffer length is longer than maximum allows (" + length + " > " + maxEncodedUTFLength + ")");
                } else if (length < 0) {
                    throw new DecoderException("The received encoded string buffer length is less than zero");
                }

                String s = buf.toString(buf.readerIndex(), length, StandardCharsets.UTF_8);
                buf.readerIndex(buf.readerIndex() + length);
                if(s.length() > maxLength) {
                    throw new DecoderException("The received string is longer than maximum allows (" + s.length() + " > " + maxLength + ")");
                }
                return s;
            }
        };
    }

    static <B extends ByteBuf, V> StreamCodec<B, Optional<V>> optional(StreamCodec<? super B, V> valueCodec) {
        return new StreamCodec<>() {
            @Override
            public void encode(B buf, Optional<V> value) {
                if (value.isPresent()) {
                    buf.writeBoolean(true);
                    valueCodec.encode(buf, value.get());
                } else {
                    buf.writeBoolean(false);
                }
            }

            @Override
            public Optional<V> decode(B buf) {
                boolean present = buf.readBoolean();
                return present ? Optional.of(valueCodec.decode(buf)) : Optional.empty();
            }
        };
    }

    static <B extends ByteBuf, L, R> StreamCodec<B, Either<L, R>> either(StreamCodec<? super B, L> leftValueCodec, StreamCodec<? super B, R> rightValueCodec) {
        return new StreamCodec<>() {
            @Override
            public void encode(B buf, Either<L, R> value) {
                value.ifLeft(l -> {
                    buf.writeBoolean(true);
                    leftValueCodec.encode(buf, l);
                }).ifRight(r -> {
                    buf.writeBoolean(false);
                    rightValueCodec.encode(buf, r);
                });
            }

            @Override
            public Either<L, R> decode(B buf) {
                boolean left = buf.readBoolean();
                return left ? Either.left(leftValueCodec.decode(buf)) : Either.right(rightValueCodec.decode(buf));
            }
        };
    }

    static <B extends ByteBuf, F, S> StreamCodec<B, Pair<F, S>> pair(StreamCodec<? super B, F> firstValueCodec, StreamCodec<? super B, S> secondValueCodec) {
        return new StreamCodec<B, Pair<F, S>>() {
            @Override
            public void encode(B buf, Pair<F, S> value) {
                firstValueCodec.encode(buf, value.getFirst());
                secondValueCodec.encode(buf, value.getSecond());
            }

            @Override
            public Pair<F, S> decode(B buf) {
                return Pair.of(firstValueCodec.decode(buf), secondValueCodec.decode(buf));
            }
        };
    }

    static <B extends ByteBuf, V, C extends Collection<V>> StreamCodec<B, C> collection(IntFunction<C> factory, StreamCodec<? super B, V> elementCodec) {
        return collection(factory, elementCodec, Integer.MAX_VALUE);
    }

    static <B extends ByteBuf, V, C extends Collection<V>> StreamCodec<B, C> collection(IntFunction<C> factory, StreamCodec<? super B, V> elementCodec, int maxSize) {
        return new StreamCodec<B, C>() {
            @Override
            public void encode(B buf, C value) {
                if(value.size() > maxSize) {
                    throw new EncoderException(value.size() + " elements, exceeded the maximum elements (" + maxSize + ")");
                }

                VAR_INT.encode(buf, value.size());
                for(V obj : value) {
                    elementCodec.encode(buf, obj);
                }
            }

            @Override
            public C decode(B buf) {
                int size = VAR_INT.decode(buf);

                if(size > maxSize){
                    throw new DecoderException(size + " encoded elements received, exceeded the maximum elements of: " + maxSize);
                }

                C collection = factory.apply(Math.min(65535, size));

                for(int i = 0; i < size; i++) {
                    collection.add(elementCodec.decode(buf));
                }

                return collection;
            }
        };
    }

    static <B extends ByteBuf, V, C extends Collection<V>> StreamCodec.ResultFunction<B, V, C> toCollection(IntFunction<C> collectionFactory) {
        return codec -> collection(collectionFactory, codec);
    }

    static <B extends ByteBuf, V> StreamCodec.ResultFunction<B, V, List<V>> toList() {
        return codec -> collection(ArrayList::new, codec);
    }

    static <B extends ByteBuf, V> StreamCodec.ResultFunction<B, V, List<V>> toList(int maxLength) {
        return codec -> collection(ArrayList::new, codec, maxLength);
    }

    static <B extends ByteBuf, V> StreamCodec.ResultFunction<B, V, Set<V>> toSet() {
        return codec -> collection(HashSet::new, codec);
    }

    static <B extends ByteBuf, V> StreamCodec.ResultFunction<B, V, Set<V>> toSet(int maxLength) {
        return codec -> collection(HashSet::new, codec, maxLength);
    }

    static <B extends ByteBuf, K, V, M extends Map<K, V>> StreamCodec<B, M> map(IntFunction<? extends M> factory, StreamCodec<? super B, K> keyCodec, StreamCodec<? super B, V> valueCodec) {
        return map(factory, keyCodec, valueCodec, Integer.MAX_VALUE);
    }

    static <B extends ByteBuf, K, V, M extends Map<K, V>> StreamCodec<B, M> map(IntFunction<? extends M> factory, StreamCodec<? super B, K> keyCodec, StreamCodec<? super B, V> valueCodec, int maxSize) {
        return new StreamCodec<B, M>() {
            @Override
            public void encode(B buf, M value) {
                if(value.size() > maxSize) {
                    throw new EncoderException(value.size() + " elements, exceeded the maximum elements of: " + maxSize);
                }

                VAR_INT.encode(buf, value.size());
                value.forEach((k, v) -> {
                    keyCodec.encode(buf, k);
                    valueCodec.encode(buf, v);
                });
            }

            @Override
            public M decode(B buf) {
                int size = VAR_INT.decode(buf);
                if(size > maxSize){
                    throw new DecoderException(size + " encoded elements received, exceeded the maximum elements of: " + maxSize);
                }

                M map = factory.apply(Math.min(65535, size));

                for(int i = 0; i < size; i++) {
                    K key = keyCodec.decode(buf);
                    V val = valueCodec.decode(buf);

                    map.put(key, val);
                }

                return map;
            }
        };
    }
}
