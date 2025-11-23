package de.leoxian.moonlightcore.transfer.context;


import de.leoxian.moonlightcore.lookup.item.ItemApiLookup;
import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.StoragePreconditions;
import de.leoxian.moonlightcore.transfer.item.CursorSlotWrapper;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public interface ContainerItemContext {

    static ContainerItemContext forPlayerInteraction(Player player, InteractionHand interactionHand) {
        if(player.getAbilities().instabuild) {
            return forCreativeInteraction(player, player.getItemInHand(interactionHand));
        }

        return ofPlayerHand(player, interactionHand);
    }

    static ContainerItemContext forCreativeInteraction(Player player, ItemStack interactingStack){
        return new CreativeInteractionContainerItemContext(ItemResource.of(interactingStack), interactingStack.getCount(), player);
    }

    static ContainerItemContext ofPlayerHand(Player player, InteractionHand interactionHand) {
        return new PlayerContainerItemContext(player, interactionHand);
    }

    static ContainerItemContext ofPLayerCursor(Player player, AbstractContainerMenu containerMenu) {
        return ofPlayerSlot(player, CursorSlotWrapper.get(containerMenu));
    }

    static ContainerItemContext ofPlayerSlot(Player player, SingleSlotStorage<ItemResource> slot) {
        return new PlayerContainerItemContext(player, slot);
    }

    static ContainerItemContext ofSingleSlot(SingleSlotStorage<ItemResource> slot) {
        return new SingleSlotContainerItemContext(slot);
    }

    static ContainerItemContext withConstant(ItemStack constantContent) {
        return withConstant(ItemResource.of(constantContent), constantContent.getCount());
    }

    static ContainerItemContext withConstant(ItemResource constantResource, int constantAmount) {
        StoragePreconditions.notNegative(constantAmount);
        return new ConstantContainerItemContext(constantResource, constantAmount);
    }

    int insertOverflow(TransactionContext context, ItemResource resource, int maxAmount);

    SingleSlotStorage<ItemResource> getMainSlot();

    @UnmodifiableView
    List<SingleSlotStorage<ItemResource>> getAdditionalSlots();

    default int insert(TransactionContext context, ItemResource insertedResource, int maxAmount) {
        int mainInserted = getMainSlot().insert(context, insertedResource, maxAmount);
        int overflowInserted = insertOverflow(context, insertedResource, maxAmount - mainInserted);

        return mainInserted + overflowInserted;
    }

    default int extract(TransactionContext context, ItemResource extractedResource, int maxAmount) {
        return getMainSlot().extract(context, extractedResource, maxAmount);
    }

    default int exchange(TransactionContext context, ItemResource newResource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(newResource, maxAmount);

        try(Transaction nested = context.openNested()) {
            int extracted = extract(context, getResource(), maxAmount);

            if(insert(context, newResource, maxAmount) == extracted) {
                nested.commit();
                return extracted;
            }
        }

        return 0;
    }

    default @Nullable <A> A find(ItemApiLookup<A, ContainerItemContext> lookup) {
        return getResource().isBlank() ? null : lookup.find(getResource().toStack(), this);
    }

    default ItemResource getResource() {
        return getMainSlot().getResource();
    }

    default int getAmount() {
        if(getResource().isBlank()) {
            throw new IllegalStateException("Amount may not be queried when the current item resource is blank");
        }

        return getMainSlot().getAmount();
    }

}
