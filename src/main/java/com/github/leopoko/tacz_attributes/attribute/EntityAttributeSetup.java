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
            event.add(EntityType.PLAYER, CustomAttributes.HIP_FIRE_ACCURACY.get());
            event.add(EntityType.PLAYER, CustomAttributes.ADS_ACCURACY.get());
            event.add(EntityType.PLAYER, CustomAttributes.HIP_FIRE_DAMAGE.get());
            event.add(EntityType.PLAYER, CustomAttributes.ADS_DAMAGE.get());
            event.add(EntityType.PLAYER, CustomAttributes.AUTO_DAMAGE.get());
            event.add(EntityType.PLAYER, CustomAttributes.SEMI_DAMAGE.get());
            event.add(EntityType.PLAYER, CustomAttributes.BURST_DAMAGE.get());
            event.add(EntityType.PLAYER, CustomAttributes.AUTO_ACCURACY.get());
            event.add(EntityType.PLAYER, CustomAttributes.SEMI_ACCURACY.get());
            event.add(EntityType.PLAYER, CustomAttributes.BURST_ACCURACY.get());
            event.add(EntityType.PLAYER, CustomAttributes.RECOIL.get());
            event.add(EntityType.PLAYER, CustomAttributes.VERTICAL_RECOIL.get());
            event.add(EntityType.PLAYER, CustomAttributes.HORIZONTAL_RECOIL.get());
            event.add(EntityType.PLAYER, CustomAttributes.ADS_RECOIL.get());
            event.add(EntityType.PLAYER, CustomAttributes.ADS_VERTICAL_RECOIL.get());
            event.add(EntityType.PLAYER, CustomAttributes.ADS_HORIZONTAL_RECOIL.get());
            event.add(EntityType.PLAYER, CustomAttributes.HIP_FIRE_RECOIL.get());
            event.add(EntityType.PLAYER, CustomAttributes.HIP_FIRE_VERTICAL_RECOIL.get());
            event.add(EntityType.PLAYER, CustomAttributes.HIP_FIRE_HORIZONTAL_RECOIL.get());
            event.add(EntityType.PLAYER, CustomAttributes.GUN_MOVEMENT_SPEED.get());
            event.add(EntityType.PLAYER, CustomAttributes.HEADSHOT_MULTIPLIER.get());
            event.add(EntityType.PLAYER, CustomAttributes.KNOCKBACK_MULTIPLIER.get());
            event.add(EntityType.PLAYER, CustomAttributes.KNOCKBACK_BASE.get());
            event.add(EntityType.PLAYER, CustomAttributes.PIERCE_MULTIPLIER.get());
            event.add(EntityType.PLAYER, CustomAttributes.RPM_MULTIPLIER.get());
            event.add(EntityType.PLAYER, CustomAttributes.ADS_SPEED.get());
            event.add(EntityType.PLAYER, CustomAttributes.SEMI_BULLET_AMOUNT.get());
            event.add(EntityType.PLAYER, CustomAttributes.AUTO_BULLET_AMOUNT.get());
            event.add(EntityType.PLAYER, CustomAttributes.BURST_BULLET_AMOUNT.get());
            event.add(EntityType.PLAYER, CustomAttributes.DRAW_SPEED.get());
            event.add(EntityType.PLAYER, CustomAttributes.HOLSTER_SPEED.get());
            event.add(EntityType.PLAYER, CustomAttributes.BURST_SPEED.get());
            event.add(EntityType.PLAYER, CustomAttributes.BULLET_VELOCITY.get());
            event.add(EntityType.PLAYER, CustomAttributes.BULLET_LIFE.get());

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
                event.add(EntityType.PLAYER, gunType.getHipFireAccuracyAttribute().get());
                event.add(EntityType.PLAYER, gunType.getAdsAccuracyAttribute().get());
                event.add(EntityType.PLAYER, gunType.getHipFireDamageAttribute().get());
                event.add(EntityType.PLAYER, gunType.getAdsDamageAttribute().get());
                event.add(EntityType.PLAYER, gunType.getAutoDamageAttribute().get());
                event.add(EntityType.PLAYER, gunType.getSemiDamageAttribute().get());
                event.add(EntityType.PLAYER, gunType.getBurstDamageAttribute().get());
                event.add(EntityType.PLAYER, gunType.getAutoAccuracyAttribute().get());
                event.add(EntityType.PLAYER, gunType.getSemiAccuracyAttribute().get());
                event.add(EntityType.PLAYER, gunType.getBurstAccuracyAttribute().get());
                event.add(EntityType.PLAYER, gunType.getRecoilAttribute().get());
                event.add(EntityType.PLAYER, gunType.getVerticalRecoilAttribute().get());
                event.add(EntityType.PLAYER, gunType.getHorizontalRecoilAttribute().get());
                event.add(EntityType.PLAYER, gunType.getAdsRecoilAttribute().get());
                event.add(EntityType.PLAYER, gunType.getAdsVerticalRecoilAttribute().get());
                event.add(EntityType.PLAYER, gunType.getAdsHorizontalRecoilAttribute().get());
                event.add(EntityType.PLAYER, gunType.getHipFireRecoilAttribute().get());
                event.add(EntityType.PLAYER, gunType.getHipFireVerticalRecoilAttribute().get());
                event.add(EntityType.PLAYER, gunType.getHipFireHorizontalRecoilAttribute().get());
                event.add(EntityType.PLAYER, gunType.getGunMovementSpeedAttribute().get());
                event.add(EntityType.PLAYER, gunType.getHeadshotMultiplierAttribute().get());
                event.add(EntityType.PLAYER, gunType.getKnockbackMultiplierAttribute().get());
                event.add(EntityType.PLAYER, gunType.getKnockbackBaseAttribute().get());
                event.add(EntityType.PLAYER, gunType.getPierceMultiplierAttribute().get());
                event.add(EntityType.PLAYER, gunType.getRpmMultiplierAttribute().get());
                event.add(EntityType.PLAYER, gunType.getAdsSpeedAttribute().get());
                event.add(EntityType.PLAYER, gunType.getSemiBulletAmountAttribute().get());
                event.add(EntityType.PLAYER, gunType.getAutoBulletAmountAttribute().get());
                event.add(EntityType.PLAYER, gunType.getBurstBulletAmountAttribute().get());
                event.add(EntityType.PLAYER, gunType.getDrawSpeedAttribute().get());
                event.add(EntityType.PLAYER, gunType.getHolsterSpeedAttribute().get());
                event.add(EntityType.PLAYER, gunType.getBurstSpeedAttribute().get());
                event.add(EntityType.PLAYER, gunType.getBulletVelocityAttribute().get());
                event.add(EntityType.PLAYER, gunType.getBulletLifeAttribute().get());
            }
        }
    }
}
