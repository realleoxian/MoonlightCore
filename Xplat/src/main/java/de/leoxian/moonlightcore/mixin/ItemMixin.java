package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.api.transfer.item.ItemResource;
import de.leoxian.moonlightcore.impl.transfer.item.ItemResourceCache;
import de.leoxian.moonlightcore.impl.transfer.item.ItemResourceImpl;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Item.class)
public class ItemMixin implements ItemResourceCache {
    @Unique
    private @Nullable ItemResource moonlightCore$cachedItemResource = null;

    @Override
    public ItemResource moonlightcore$getCachedItemResource() {
        if(moonlightCore$cachedItemResource == null) {
            moonlightCore$cachedItemResource = new ItemResourceImpl((Item) (Object) this, null);
        }

        return moonlightCore$cachedItemResource;
    }
}
