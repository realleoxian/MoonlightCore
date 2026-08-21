package de.leoxian.moonlightcore.mixin.accessor;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(HolderSet.Named.class)
public interface HolderSetNamedAccessor<T> {
    @Accessor
    void setContents(List<Holder<T>> contents);

    @Accessor
    List<Holder<T>> getContents();
}
