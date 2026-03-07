package com.github.leopoko.tacz_attributes.attribute;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityAttributeSetup {

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeModificationEvent event) {
        if (event.getTypes().contains(EntityType.PLAYER)) {
            // 全銃共通属性
            event.add(EntityType.PLAYER, CustomAttributes.GUN_DAMAGE.get());
            event.add(EntityType.PLAYER, CustomAttributes.RELOAD_SPEED.get());
            event.add(EntityType.PLAYER, CustomAttributes.BOLT_ACTION_SPEED.get());
            event.add(EntityType.PLAYER, CustomAttributes.MAGAZINE_CAPACITY.get());
            event.add(EntityType.PLAYER, CustomAttributes.AMMO_SAVE_CHANCE.get());
            event.add(EntityType.PLAYER, CustomAttributes.AMMO_RECOVERY_CHANCE.get());
            event.add(EntityType.PLAYER, CustomAttributes.AMMO_RECOVERY_AMOUNT.get());
            event.add(EntityType.PLAYER, CustomAttributes.AMMO_RECOVERY_PERCENT.get());
            event.add(EntityType.PLAYER, CustomAttributes.RELOAD_AMMO_SAVE_CHANCE.get());
            event.add(EntityType.PLAYER, CustomAttributes.BONUS_AMMO_CHANCE.get());
            event.add(EntityType.PLAYER, CustomAttributes.BONUS_AMMO_AMOUNT.get());
            event.add(EntityType.PLAYER, CustomAttributes.BONUS_AMMO_PERCENT.get());

            // 銃種別属性
            for (GunType gunType : GunType.values()) {
                event.add(EntityType.PLAYER, gunType.getDamageAttribute().get());
                event.add(EntityType.PLAYER, gunType.getReloadSpeedAttribute().get());
                event.add(EntityType.PLAYER, gunType.getBoltActionSpeedAttribute().get());
                event.add(EntityType.PLAYER, gunType.getMagazineCapacityAttribute().get());
                event.add(EntityType.PLAYER, gunType.getAmmoSaveChanceAttribute().get());
                event.add(EntityType.PLAYER, gunType.getAmmoRecoveryChanceAttribute().get());
                event.add(EntityType.PLAYER, gunType.getAmmoRecoveryAmountAttribute().get());
                event.add(EntityType.PLAYER, gunType.getAmmoRecoveryPercentAttribute().get());
                event.add(EntityType.PLAYER, gunType.getReloadAmmoSaveChanceAttribute().get());
                event.add(EntityType.PLAYER, gunType.getBonusAmmoChanceAttribute().get());
                event.add(EntityType.PLAYER, gunType.getBonusAmmoAmountAttribute().get());
                event.add(EntityType.PLAYER, gunType.getBonusAmmoPercentAttribute().get());
            }
        }
    }
}
