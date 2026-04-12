package de.realleoxian.moonlightcore.mixin;

import de.realleoxian.moonlightcore.api.transfer.item.ItemResource;
import de.realleoxian.moonlightcore.impl.transfer.item.ItemResourceCache;
import de.realleoxian.moonlightcore.impl.transfer.item.ItemResourceImpl;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
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
