package de.leoxian.moonlightcore.mixin.transfer;

import de.leoxian.moonlightcore.common.transfer.item.ItemResource;
import de.leoxian.moonlightcore.internal.common.transfer.item.ItemResourceCache;
import de.leoxian.moonlightcore.internal.common.transfer.item.ItemResourceImpl;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Item.class)
public abstract class ItemMixin implements ItemResourceCache {
    @Unique
    private ItemResource moonlighcore$cachedItemResource = null;

    @Override
    public ItemResource moonlightcore$getCachedItemResource() {
        ItemResource ret = this.moonlighcore$cachedItemResource;
        if (ret == null) {
            ret = this.moonlighcore$cachedItemResource = new ItemResourceImpl(
                    (Item) (Object) this,
                    DataComponentPatch.EMPTY
            );
        }
        return ret;
    }
}
