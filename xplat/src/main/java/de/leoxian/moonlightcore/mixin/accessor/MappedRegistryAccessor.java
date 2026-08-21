package de.leoxian.moonlightcore.mixin.accessor;

import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(MappedRegistry.class)
public interface MappedRegistryAccessor<T> {
    @Accessor
    Map<TagKey<T>, HolderSet.Named<T>> getFrozenTags();

    @Accessor
    Map<T, Holder.Reference<T>> getUnregisteredIntrusiveHolders();

    @Accessor
    ObjectList<Holder.Reference<T>> getById();

    @Accessor
    Reference2IntMap<T> getToId();

    @Accessor
    Map<Identifier, Holder.Reference<T>> getByLocation();

    @Accessor
    Map<ResourceKey<T>, Holder.Reference<T>> getByKey();

    @Accessor
    Map<T, Holder.Reference<T>> getByValue();

    @Accessor
    Map<ResourceKey<T>, RegistrationInfo> getRegistrationInfos();

    @Accessor
    boolean isFrozen();

    @Accessor
    void setFrozen(boolean frozen);

    @Accessor
    void setRegistryLifecycle(Lifecycle lifecycle);
}
