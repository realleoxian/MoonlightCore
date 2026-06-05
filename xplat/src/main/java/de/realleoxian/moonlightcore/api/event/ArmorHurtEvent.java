package de.realleoxian.moonlightcore.api.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;

public final class ArmorHurtEvent extends EventBase implements CancellableEvent {
    public static class ArmorEntry {
        public ItemStack armorItemStack;
        public final float originalDamage;
        public float damage;

        public ArmorEntry(ItemStack armorItemStack, float originalDamage) {
            this.armorItemStack = armorItemStack;
            this.originalDamage = originalDamage;
            this.damage = originalDamage;
        }
    }

    private final EnumMap<EquipmentSlot, ArmorEntry> entries;
    public final DamageSource source;
    public final LivingEntity entity;

    public ArmorHurtEvent(LivingEntity entity, EnumMap<EquipmentSlot, ArmorEntry> entries, DamageSource source) {
        this.entries = entries;
        this.source = source;
        this.entity = entity;
    }

    public ItemStack getArmorItemStack(EquipmentSlot slot) {
        return this.entries.containsKey(slot) ? this.entries.get(slot).armorItemStack : ItemStack.EMPTY;
    }

    public float getOriginalDamage(EquipmentSlot slot) {
        return this.entries.containsKey(slot) ? this.entries.get(slot).originalDamage : 0.0F;
    }

    public float getDamage(EquipmentSlot slot) {
        return this.entries.containsKey(slot) ? this.entries.get(slot).damage : 0.0F;
    }

    public void setDamage(EquipmentSlot slot, float damage) {
        if (this.entries.containsKey(slot)) {
            this.entries.get(slot).damage = damage;
        }
    }
}

