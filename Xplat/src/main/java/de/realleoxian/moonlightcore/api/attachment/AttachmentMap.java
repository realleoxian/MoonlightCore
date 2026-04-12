package de.realleoxian.moonlightcore.api.attachment;

import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;
import java.util.function.Supplier;

public interface AttachmentMap {

    static AttachmentMap get(Object obj) {
        if (!(obj instanceof AttachmentHolder holder)) {
            throw new IllegalStateException("Can't get attachment map from null holder");
        }

        return holder.getAttachmentsMap();
    }

    <A> void syncAttachment(Level level, AttachmentType<A> type);

    void writeToNBT(CompoundTag tag);

    void readFromNBT(CompoundTag tag);

    <A> @Nullable A set(AttachmentType<A> type, @Nullable A newValue);

    <A> @Nullable A remove(AttachmentType<A> type);

    <A> @Nullable A get(AttachmentType<A> type);

    <A> A getOrSet(AttachmentType<A> type, Supplier<A> initializer);

    <A> A getOrSet(AttachmentType<A> type);

    <A> A getOrThrow(AttachmentType<A> type);

    boolean contains(AttachmentType<?> type);

    @UnmodifiableView
    Map<AttachmentType<?>, Object> getAttachments();

}
