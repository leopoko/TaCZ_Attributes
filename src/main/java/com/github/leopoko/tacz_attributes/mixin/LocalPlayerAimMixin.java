package com.github.leopoko.tacz_attributes.mixin;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.client.gameplay.LocalPlayerAim;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * LocalPlayerAim.getAlphaProgress() 内の Math.max(0, aimTime) を @Redirect し、
 * ADS移行速度属性に基づいた倍率を aimTime に適用するクライアント側Mixin。
 * <p>
 * サーバー側の LivingEntityAimMixin と同じ倍率を適用し、
 * ADSアニメーションの視覚的速度とゲームロジックを一致させる。
 */
@Mixin(LocalPlayerAim.class)
public class LocalPlayerAimMixin {

    @Shadow(remap = false)
    @Final
    private LocalPlayer player;

    /**
     * getAlphaProgress() 内の Math.max(float, float) 呼び出しをリダイレクトし、
     * ADS速度属性でaimTimeを調整する（クライアント側アニメーション用）。
     */
    @Redirect(
            method = "getAlphaProgress",
            at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F", ordinal = 0),
            remap = false
    )
    private float tacz_attributes$modifyClientAimTime(float a, float b) {
        float aimTime = Math.max(a, b);
        if (aimTime <= 0) return aimTime;

        double globalAdsSpeed = tacz_attributes$getAttributeValue(this.player, CustomAttributes.ADS_SPEED.get());
        GunType gunType = GunTypeResolver.resolveFromItem(this.player.getMainHandItem());
        double typeAdsSpeed = 1.0;
        if (gunType != null) {
            typeAdsSpeed = tacz_attributes$getAttributeValue(this.player, gunType.getAdsSpeedAttribute().get());
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
