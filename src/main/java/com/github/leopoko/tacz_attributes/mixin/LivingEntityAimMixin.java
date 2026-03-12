package com.github.leopoko.tacz_attributes.mixin;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.entity.shooter.LivingEntityAim;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * LivingEntityAim.tickAimingProgress() 内の Math.max(0, aimTime) を @Redirect し、
 * ADS移行速度属性に基づいた倍率を aimTime に適用するMixin。
 * <p>
 * 計算式: 最終aimTime = Math.max(0, aimTime) / (全体ADS速度 × 銃種別ADS速度)
 * ADS速度 2.0 → aimTime 半分 → 2倍速でADS移行
 */
@Mixin(LivingEntityAim.class)
public class LivingEntityAimMixin {

    @Shadow(remap = false)
    @Final
    private LivingEntity shooter;

    /**
     * tickAimingProgress() 内の最初の Math.max(float, float) 呼び出しをリダイレクトし、
     * ADS速度属性でaimTimeを調整する。
     */
    @Redirect(
            method = "tickAimingProgress",
            at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F", ordinal = 0),
            remap = false
    )
    private float tacz_attributes$modifyAimTime(float a, float b) {
        float aimTime = Math.max(a, b);
        if (aimTime <= 0) return aimTime;

        double globalAdsSpeed = tacz_attributes$getAttributeValue(this.shooter, CustomAttributes.ADS_SPEED.get());
        GunType gunType = GunTypeResolver.resolveFromItem(this.shooter.getMainHandItem());
        double typeAdsSpeed = 1.0;
        if (gunType != null) {
            typeAdsSpeed = tacz_attributes$getAttributeValue(this.shooter, gunType.getAdsSpeedAttribute().get());
        }
        double combined = globalAdsSpeed * typeAdsSpeed;
        if (combined == 1.0) return aimTime;

        float modified = (float) (aimTime / combined);

        return Math.max(0, modified);
    }

    @Unique
    private static double tacz_attributes$getAttributeValue(LivingEntity entity, Attribute attribute) {
        if (entity.getAttributes().hasAttribute(attribute)) {
            return entity.getAttributeValue(attribute);
        }
        return 1.0;
    }
}
