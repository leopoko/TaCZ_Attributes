package com.github.leopoko.tacz_attributes.mixin;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.client.gameplay.LocalPlayerShoot;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * LocalPlayerShoot.doShoot() に対するクライアント側Mixin。
 * <p>
 * 1. バースト弾数倍率のクライアント同期（反動・音声の修正）:
 *    cycles (int ordinal 1) を変更して、追加バーストショットの反動・音声を再生させる。
 * <p>
 * 2. バースト速度倍率:
 *    burstShootInterval (long ordinal 0) を変更して、バースト内の射撃間隔を調整する。
 */
@Mixin(LocalPlayerShoot.class)
public class LocalPlayerShootMixin {

    @Shadow(remap = false)
    private LocalPlayer player;

    /**
     * doShoot() 内の cycles (int ordinal 1) を変更し、
     * バースト弾数倍率をクライアント側に反映する。
     * <p>
     * これにより追加されたバーストショットの反動アニメーションと音声が再生される。
     */
    @ModifyVariable(
            method = "doShoot",
            at = @At("STORE"),
            ordinal = 1,
            remap = false
    )
    private int tacz_attributes$modifyClientCycles(int cycles) {
        if (this.player == null || cycles <= 1) return cycles;

        ItemStack stack = this.player.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(stack);
        if (iGun == null) return cycles;
        FireMode mode = iGun.getFireMode(stack);
        if (mode != FireMode.BURST) return cycles;

        GunType gunType = GunTypeResolver.resolveFromItem(stack);
        double globalMult = tacz_attributes$getAttrValue(CustomAttributes.BURST_BULLET_AMOUNT);
        double typeMult = 1.0;
        if (gunType != null) {
            typeMult = tacz_attributes$getAttrValue(gunType.getBurstBulletAmountAttribute());
        }
        double combined = globalMult * typeMult;
        if (combined == 1.0) return cycles;

        int modified = Math.max(1, (int) (cycles * combined));

        return modified;
    }

    /**
     * doShoot() 内の period (long ordinal 1) を変更し、
     * バースト間隔速度倍率をクライアント側に反映する。
     * <p>
     * LVT上の long 変数: ordinal 0 = delay（メソッドパラメータ）、ordinal 1 = period（ローカル変数）
     * burst_speed 2.0 → 間隔半分 → より速いバースト（音声・反動のタイミングに反映）
     */
    @ModifyVariable(
            method = "doShoot",
            at = @At("STORE"),
            ordinal = 1,
            remap = false
    )
    private long tacz_attributes$modifyClientBurstInterval(long interval) {
        if (this.player == null || interval <= 1) return interval;

        ItemStack stack = this.player.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(stack);
        if (iGun == null) return interval;
        FireMode mode = iGun.getFireMode(stack);
        if (mode != FireMode.BURST) return interval;

        GunType gunType = GunTypeResolver.resolveFromItem(stack);
        double globalMult = tacz_attributes$getAttrValue(CustomAttributes.BURST_SPEED);
        double typeMult = 1.0;
        if (gunType != null) {
            typeMult = tacz_attributes$getAttrValue(gunType.getBurstSpeedAttribute());
        }
        double combined = globalMult * typeMult;
        if (combined == 1.0) return interval;

        long modified = Math.max(1L, (long) (interval / combined));

        return modified;
    }

    @Unique
    private double tacz_attributes$getAttrValue(Holder<Attribute> attribute) {
        if (this.player != null && this.player.getAttributes().hasAttribute(attribute)) {
            return this.player.getAttributeValue(attribute);
        }
        return 1.0;
    }
}
