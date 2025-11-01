package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.transfer.TransferResourceExtension;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Item.class)
public class ItemMixin implements TransferResourceExtension<ItemResource> {
    @Unique
    private ItemResource mlcore_cachedItemResource = null;

    @Override
    public ItemResource mlcore_getCachedResource() {
        if(this.mlcore_cachedItemResource == null) {
            this.mlcore_cachedItemResource = new ItemResource((Item) (Object) this, null);
        }
        return this.mlcore_cachedItemResource;
    }
}
