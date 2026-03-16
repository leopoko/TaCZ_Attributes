package com.github.leopoko.tacz_attributes.util;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.gun.FireMode;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;

import javax.annotation.Nullable;

/**
 * 銃ダメージの属性倍率を計算する共通ユーティリティ。
 * EntityKineticBulletMixin と TargetBlockMixin の両方から使用される。
 */
public final class DamageModifierHelper {

    private DamageModifierHelper() {}

    /**
     * 射撃者の属性に基づくダメージ倍率を計算する。
     *
     * @param shooter 射撃者
     * @param gunId   銃のID
     * @return ダメージ倍率（1.0 = 変更なし）
     */
    public static double calculateDamageModifier(LivingEntity shooter, @Nullable ResourceLocation gunId) {
        GunType gunType = GunTypeResolver.resolve(gunId);

        // 全体ダメージ倍率
        double globalModifier = getAttributeValue(shooter, CustomAttributes.GUN_DAMAGE);

        // 銃種別ダメージ倍率
        double typeModifier = 1.0;
        if (gunType != null) {
            typeModifier = getAttributeValue(shooter, gunType.getDamageAttribute());
        }

        // ADS / 腰撃ちダメージ倍率
        boolean isAds = IGunOperator.fromLivingEntity(shooter).getSynIsAiming();
        double adsHipGlobal;
        double adsHipType = 1.0;
        if (isAds) {
            adsHipGlobal = getAttributeValue(shooter, CustomAttributes.ADS_DAMAGE);
            if (gunType != null) {
                adsHipType = getAttributeValue(shooter, gunType.getAdsDamageAttribute());
            }
        } else {
            adsHipGlobal = getAttributeValue(shooter, CustomAttributes.HIP_FIRE_DAMAGE);
            if (gunType != null) {
                adsHipType = getAttributeValue(shooter, gunType.getHipFireDamageAttribute());
            }
        }

        // 射撃モード別ダメージ倍率
        FireMode fireMode = FireModeHelper.getFireMode(shooter.getMainHandItem());
        double fireModeGlobal = FireModeHelper.getAttributeValue(shooter, FireModeHelper.getGlobalDamageAttribute(fireMode));
        double fireModeType = FireModeHelper.getAttributeValue(shooter, FireModeHelper.getTypeDamageAttribute(gunType, fireMode));

        return globalModifier * typeModifier * adsHipGlobal * adsHipType * fireModeGlobal * fireModeType;
    }

    private static double getAttributeValue(LivingEntity entity, Holder<Attribute> attribute) {
        if (entity.getAttributes().hasAttribute(attribute)) {
            return entity.getAttributeValue(attribute);
        }
        return 1.0;
    }
}
