package com.github.leopoko.tacz_attributes.mixin;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * GunData.getShootInterval() の戻り値を変更し、
 * RPM倍率属性に基づいた発射レートの変更を適用するMixin。
 * <p>
 * 計算式: 最終インターバル = 元インターバル / (全体RPM倍率 × 銃種別RPM倍率)
 * RPM倍率 2.0 → インターバル半分 → 2倍速射
 */
@Mixin(GunData.class)
public class ShootIntervalMixin {

    @Inject(
            method = "getShootInterval",
            at = @At("RETURN"),
            remap = false,
            cancellable = true
    )
    private void tacz_attributes$modifyShootInterval(LivingEntity shooter, FireMode fireMode,
                                                      ItemStack gunStack, CallbackInfoReturnable<Long> cir) {
        if (shooter == null) return;

        GunType gunType = GunTypeResolver.resolveFromItem(gunStack);
        double globalRpm = tacz_attributes$getAttributeValue(shooter, CustomAttributes.RPM_MULTIPLIER.get());
        double typeRpm = 1.0;
        if (gunType != null) {
            typeRpm = tacz_attributes$getAttributeValue(shooter, gunType.getRpmMultiplierAttribute().get());
        }
        double combined = globalRpm * typeRpm;
        if (combined == 1.0) return;

        long original = cir.getReturnValue();
        long modified = Math.max(1L, (long) (original / combined));

        cir.setReturnValue(modified);
    }

    @Unique
    private static double tacz_attributes$getAttributeValue(LivingEntity entity, Attribute attribute) {
        if (entity.getAttributes().hasAttribute(attribute)) {
            return entity.getAttributeValue(attribute);
        }
        return 1.0;
    }
}
