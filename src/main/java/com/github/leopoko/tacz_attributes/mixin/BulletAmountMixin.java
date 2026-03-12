package com.github.leopoko.tacz_attributes.mixin;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * ModernKineticGunScriptAPI.shootOnce() に対するMixin。
 * <p>
 * - セミオート/フルオート弾数倍率: bulletAmount (int ordinal 1) を変更
 * - バースト弾数倍率: cycles (int ordinal 2) を変更
 * <p>
 * 計算式:
 * セミ/フルオート: 最終bulletAmount = bulletAmount × (全体倍率 × 銃種別倍率)
 * バースト: 最終cycles = cycles × (全体倍率 × 銃種別倍率)
 */
@Mixin(ModernKineticGunScriptAPI.class)
public class BulletAmountMixin {

    @Shadow(remap = false)
    private LivingEntity shooter;

    @Shadow(remap = false)
    private ItemStack itemStack;

    /**
     * shootOnce() 内の bulletAmount (int ordinal 1) を変更し、
     * セミオート/フルオート時の弾数倍率を適用する。
     */
    @ModifyVariable(
            method = "shootOnce",
            at = @At("STORE"),
            ordinal = 1,
            remap = false
    )
    private int tacz_attributes$modifyBulletAmount(int bulletAmount) {
        if (this.shooter == null) return bulletAmount;

        IGun iGun = IGun.getIGunOrNull(this.itemStack);
        if (iGun == null) return bulletAmount;
        FireMode mode = iGun.getFireMode(this.itemStack);

        Holder<Attribute> globalAttr;
        if (mode == FireMode.SEMI) {
            globalAttr = CustomAttributes.SEMI_BULLET_AMOUNT;
        } else if (mode == FireMode.AUTO) {
            globalAttr = CustomAttributes.AUTO_BULLET_AMOUNT;
        } else {
            return bulletAmount; // BURST時はcyclesで変更するためここでは変更しない
        }

        GunType gunType = GunTypeResolver.resolveFromItem(this.itemStack);
        double globalMult = tacz_attributes$getAttributeValue(this.shooter, globalAttr);
        double typeMult = 1.0;
        if (gunType != null) {
            var typeAttr = (mode == FireMode.SEMI)
                    ? gunType.getSemiBulletAmountAttribute()
                    : gunType.getAutoBulletAmountAttribute();
            typeMult = tacz_attributes$getAttributeValue(this.shooter, typeAttr);
        }
        double combined = globalMult * typeMult;
        if (combined == 1.0) return bulletAmount;

        int modified = Math.max(1, (int) (bulletAmount * combined));

        return modified;
    }

    /**
     * shootOnce() 内の cycles (int ordinal 2) を変更し、
     * バースト時の弾数（バーストカウント）倍率を適用する。
     */
    @ModifyVariable(
            method = "shootOnce",
            at = @At("STORE"),
            ordinal = 2,
            remap = false
    )
    private int tacz_attributes$modifyCycles(int cycles) {
        if (this.shooter == null || cycles <= 1) return cycles; // 非バースト(cycles=1)は変更しない

        GunType gunType = GunTypeResolver.resolveFromItem(this.itemStack);
        double globalMult = tacz_attributes$getAttributeValue(this.shooter, CustomAttributes.BURST_BULLET_AMOUNT);
        double typeMult = 1.0;
        if (gunType != null) {
            typeMult = tacz_attributes$getAttributeValue(this.shooter, gunType.getBurstBulletAmountAttribute());
        }
        double combined = globalMult * typeMult;
        if (combined == 1.0) return cycles;

        int modified = Math.max(1, (int) (cycles * combined));

        return modified;
    }

    /**
     * shootOnce() 内の burstShootInterval (long ordinal 0) を変更し、
     * バースト間隔速度倍率を適用する。
     * <p>
     * 計算式: 最終interval = interval / (全体倍率 × 銃種別倍率)
     * burst_speed 2.0 → 間隔半分 → より速いバースト
     */
    @ModifyVariable(
            method = "shootOnce",
            at = @At("STORE"),
            ordinal = 0,
            remap = false
    )
    private long tacz_attributes$modifyBurstInterval(long interval) {
        if (this.shooter == null || interval <= 1) return interval;

        IGun iGun = IGun.getIGunOrNull(this.itemStack);
        if (iGun == null) return interval;
        FireMode mode = iGun.getFireMode(this.itemStack);
        if (mode != FireMode.BURST) return interval;

        GunType gunType = GunTypeResolver.resolveFromItem(this.itemStack);
        double globalMult = tacz_attributes$getAttributeValue(this.shooter, CustomAttributes.BURST_SPEED);
        double typeMult = 1.0;
        if (gunType != null) {
            typeMult = tacz_attributes$getAttributeValue(this.shooter, gunType.getBurstSpeedAttribute());
        }
        double combined = globalMult * typeMult;
        if (combined == 1.0) return interval;

        long modified = Math.max(1L, (long) (interval / combined));

        return modified;
    }

    @Unique
    private static double tacz_attributes$getAttributeValue(LivingEntity entity, Holder<Attribute> attribute) {
        if (entity.getAttributes().hasAttribute(attribute)) {
            return entity.getAttributeValue(attribute);
        }
        return 1.0;
    }
}
