package de.leoxian.moonlightcore.impl.transfer.context;

import de.leoxian.moonlightcore.api.transfer.context.ItemAccessContext;
import de.leoxian.moonlightcore.api.transfer.item.ItemResource;
import de.leoxian.moonlightcore.api.transfer.item.VanillaContainerWrapper;
import de.leoxian.moonlightcore.api.transfer.storage.Storage;
import de.leoxian.moonlightcore.api.transfer.transaction.TransactionContext;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class StackItemAccessContext implements ItemAccessContext {
    private final Item item;
    private final Storage<ItemResource> wrapper;
    
    public StackItemAccessContext(ItemStack stack) {
        this.item = stack.getItem();
        this.wrapper = VanillaContainerWrapper.of(new SimpleContainer(stack));
    }

    @Override
    public int insert(TransactionContext tx, ItemResource resource, int maxAmount) {
        if (!resource.is(item)) {
            return 0;
        }
        
        return wrapper.insert(tx, resource, maxAmount);
    }

    @Override
    public int extract(TransactionContext tx, ItemResource resource, int maxAmount) {
        return wrapper.extract(tx, resource, maxAmount);
    }

    @Override
    public ItemResource getResource() {
        return wrapper.getResource(0);
    }

    @Override
    public int getAmount() {
        return wrapper.getAmount(0);
    }
}
