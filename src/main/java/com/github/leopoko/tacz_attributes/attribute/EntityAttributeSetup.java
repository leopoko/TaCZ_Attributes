package com.github.leopoko.tacz_attributes.attribute;

import com.github.leopoko.tacz_attributes.Tacz_attributes;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

@EventBusSubscriber(modid = Tacz_attributes.MODID, bus = EventBusSubscriber.Bus.MOD)
public class EntityAttributeSetup {

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeModificationEvent event) {
        if (event.getTypes().contains(EntityType.PLAYER)) {
            // 全銃共通属性
            event.add(EntityType.PLAYER, CustomAttributes.GUN_DAMAGE);
            event.add(EntityType.PLAYER, CustomAttributes.RELOAD_SPEED);
            event.add(EntityType.PLAYER, CustomAttributes.BOLT_ACTION_SPEED);
            event.add(EntityType.PLAYER, CustomAttributes.MAGAZINE_CAPACITY);
            event.add(EntityType.PLAYER, CustomAttributes.AMMO_SAVE_CHANCE);
            event.add(EntityType.PLAYER, CustomAttributes.AMMO_RECOVERY_CHANCE);
            event.add(EntityType.PLAYER, CustomAttributes.AMMO_RECOVERY_AMOUNT);
            event.add(EntityType.PLAYER, CustomAttributes.AMMO_RECOVERY_PERCENT);
            event.add(EntityType.PLAYER, CustomAttributes.RELOAD_AMMO_SAVE_CHANCE);
            event.add(EntityType.PLAYER, CustomAttributes.BONUS_AMMO_CHANCE);
            event.add(EntityType.PLAYER, CustomAttributes.BONUS_AMMO_AMOUNT);
            event.add(EntityType.PLAYER, CustomAttributes.BONUS_AMMO_PERCENT);
            event.add(EntityType.PLAYER, CustomAttributes.HIP_FIRE_ACCURACY);
            event.add(EntityType.PLAYER, CustomAttributes.ADS_ACCURACY);
            event.add(EntityType.PLAYER, CustomAttributes.HIP_FIRE_DAMAGE);
            event.add(EntityType.PLAYER, CustomAttributes.ADS_DAMAGE);
            event.add(EntityType.PLAYER, CustomAttributes.AUTO_DAMAGE);
            event.add(EntityType.PLAYER, CustomAttributes.SEMI_DAMAGE);
            event.add(EntityType.PLAYER, CustomAttributes.BURST_DAMAGE);
            event.add(EntityType.PLAYER, CustomAttributes.AUTO_ACCURACY);
            event.add(EntityType.PLAYER, CustomAttributes.SEMI_ACCURACY);
            event.add(EntityType.PLAYER, CustomAttributes.BURST_ACCURACY);
            event.add(EntityType.PLAYER, CustomAttributes.RECOIL);
            event.add(EntityType.PLAYER, CustomAttributes.VERTICAL_RECOIL);
            event.add(EntityType.PLAYER, CustomAttributes.HORIZONTAL_RECOIL);
            event.add(EntityType.PLAYER, CustomAttributes.ADS_RECOIL);
            event.add(EntityType.PLAYER, CustomAttributes.ADS_VERTICAL_RECOIL);
            event.add(EntityType.PLAYER, CustomAttributes.ADS_HORIZONTAL_RECOIL);
            event.add(EntityType.PLAYER, CustomAttributes.HIP_FIRE_RECOIL);
            event.add(EntityType.PLAYER, CustomAttributes.HIP_FIRE_VERTICAL_RECOIL);
            event.add(EntityType.PLAYER, CustomAttributes.HIP_FIRE_HORIZONTAL_RECOIL);
            event.add(EntityType.PLAYER, CustomAttributes.GUN_MOVEMENT_SPEED);
            event.add(EntityType.PLAYER, CustomAttributes.HEADSHOT_MULTIPLIER);
            event.add(EntityType.PLAYER, CustomAttributes.KNOCKBACK_MULTIPLIER);
            event.add(EntityType.PLAYER, CustomAttributes.KNOCKBACK_BASE);
            event.add(EntityType.PLAYER, CustomAttributes.PIERCE_MULTIPLIER);
            event.add(EntityType.PLAYER, CustomAttributes.RPM_MULTIPLIER);
            event.add(EntityType.PLAYER, CustomAttributes.ADS_SPEED);
            event.add(EntityType.PLAYER, CustomAttributes.SEMI_BULLET_AMOUNT);
            event.add(EntityType.PLAYER, CustomAttributes.AUTO_BULLET_AMOUNT);
            event.add(EntityType.PLAYER, CustomAttributes.BURST_BULLET_AMOUNT);
            event.add(EntityType.PLAYER, CustomAttributes.DRAW_SPEED);
            event.add(EntityType.PLAYER, CustomAttributes.BURST_SPEED);

            // 銃種別属性
            for (GunType gunType : GunType.values()) {
                event.add(EntityType.PLAYER, gunType.getDamageAttribute());
                event.add(EntityType.PLAYER, gunType.getReloadSpeedAttribute());
                event.add(EntityType.PLAYER, gunType.getBoltActionSpeedAttribute());
                event.add(EntityType.PLAYER, gunType.getMagazineCapacityAttribute());
                event.add(EntityType.PLAYER, gunType.getAmmoSaveChanceAttribute());
                event.add(EntityType.PLAYER, gunType.getAmmoRecoveryChanceAttribute());
                event.add(EntityType.PLAYER, gunType.getAmmoRecoveryAmountAttribute());
                event.add(EntityType.PLAYER, gunType.getAmmoRecoveryPercentAttribute());
                event.add(EntityType.PLAYER, gunType.getReloadAmmoSaveChanceAttribute());
                event.add(EntityType.PLAYER, gunType.getBonusAmmoChanceAttribute());
                event.add(EntityType.PLAYER, gunType.getBonusAmmoAmountAttribute());
                event.add(EntityType.PLAYER, gunType.getBonusAmmoPercentAttribute());
                event.add(EntityType.PLAYER, gunType.getHipFireAccuracyAttribute());
                event.add(EntityType.PLAYER, gunType.getAdsAccuracyAttribute());
                event.add(EntityType.PLAYER, gunType.getHipFireDamageAttribute());
                event.add(EntityType.PLAYER, gunType.getAdsDamageAttribute());
                event.add(EntityType.PLAYER, gunType.getAutoDamageAttribute());
                event.add(EntityType.PLAYER, gunType.getSemiDamageAttribute());
                event.add(EntityType.PLAYER, gunType.getBurstDamageAttribute());
                event.add(EntityType.PLAYER, gunType.getAutoAccuracyAttribute());
                event.add(EntityType.PLAYER, gunType.getSemiAccuracyAttribute());
                event.add(EntityType.PLAYER, gunType.getBurstAccuracyAttribute());
                event.add(EntityType.PLAYER, gunType.getRecoilAttribute());
                event.add(EntityType.PLAYER, gunType.getVerticalRecoilAttribute());
                event.add(EntityType.PLAYER, gunType.getHorizontalRecoilAttribute());
                event.add(EntityType.PLAYER, gunType.getAdsRecoilAttribute());
                event.add(EntityType.PLAYER, gunType.getAdsVerticalRecoilAttribute());
                event.add(EntityType.PLAYER, gunType.getAdsHorizontalRecoilAttribute());
                event.add(EntityType.PLAYER, gunType.getHipFireRecoilAttribute());
                event.add(EntityType.PLAYER, gunType.getHipFireVerticalRecoilAttribute());
                event.add(EntityType.PLAYER, gunType.getHipFireHorizontalRecoilAttribute());
                event.add(EntityType.PLAYER, gunType.getGunMovementSpeedAttribute());
                event.add(EntityType.PLAYER, gunType.getHeadshotMultiplierAttribute());
                event.add(EntityType.PLAYER, gunType.getKnockbackMultiplierAttribute());
                event.add(EntityType.PLAYER, gunType.getKnockbackBaseAttribute());
                event.add(EntityType.PLAYER, gunType.getPierceMultiplierAttribute());
                event.add(EntityType.PLAYER, gunType.getRpmMultiplierAttribute());
                event.add(EntityType.PLAYER, gunType.getAdsSpeedAttribute());
                event.add(EntityType.PLAYER, gunType.getSemiBulletAmountAttribute());
                event.add(EntityType.PLAYER, gunType.getAutoBulletAmountAttribute());
                event.add(EntityType.PLAYER, gunType.getBurstBulletAmountAttribute());
                event.add(EntityType.PLAYER, gunType.getDrawSpeedAttribute());
                event.add(EntityType.PLAYER, gunType.getBurstSpeedAttribute());
            }
        }
    }
}
