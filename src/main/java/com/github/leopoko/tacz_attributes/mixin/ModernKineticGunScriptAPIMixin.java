package com.github.leopoko.tacz_attributes.mixin;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.github.leopoko.tacz_attributes.util.ReloadFinishingContext;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import com.tacz.guns.util.AttachmentDataUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * ModernKineticGunScriptAPI に対する属性適用Mixin。
 * <p>
 * 以下の3つの機能を提供する:
 * 1. 射撃時弾薬非消費（reduceAmmoOnce の HEAD）
 * 2. リロード時弾薬非消費（consumeAmmoFromPlayer の HEAD）
 * 3. リロード時追加弾薬（putAmmoInMagazine の RETURN）
 */
@Mixin(ModernKineticGunScriptAPI.class)
public class ModernKineticGunScriptAPIMixin {

    @Shadow(remap = false)
    private LivingEntity shooter;

    @Shadow(remap = false)
    private ItemStack itemStack;

    // === 1. 射撃時弾薬非消費 ===

    @Inject(method = "reduceAmmoOnce", at = @At("HEAD"), cancellable = true, remap = false)
    private void tacz_attributes$ammoSaveChance(CallbackInfoReturnable<Boolean> cir) {
        if (shooter == null) return;

        double saveChance = tacz_attributes$getAmmoSaveChance();
        if (saveChance <= 0) return;

        if (shooter.getRandom().nextDouble() < saveChance) {
            // true を返すことで「弾薬消費成功」と見せかけ、実際は消費しない
            cir.setReturnValue(true);
        }
    }

    // === 2. リロード時弾薬非消費 ===

    /**
     * リロード処理中に consumeAmmoFromPlayer() が呼ばれた場合、
     * 確率でインベントリ弾薬を消費せずに neededAmount をそのまま返す（無料リロード）。
     */
    @Inject(method = "consumeAmmoFromPlayer", at = @At("HEAD"), cancellable = true, remap = false)
    private void tacz_attributes$reloadAmmoSave(int neededAmount, CallbackInfoReturnable<Integer> cir) {
        if (!ReloadFinishingContext.isReloading()) return;
        if (shooter == null) return;

        double saveChance = tacz_attributes$getReloadAmmoSaveChance();
        if (saveChance <= 0) return;

        if (shooter.getRandom().nextDouble() < saveChance) {
            // neededAmount を返すことで「全弾消費成功」と見せかけ、実際は消費しない
            cir.setReturnValue(neededAmount);
        }
    }

    // === 3. リロード時追加弾薬 ===

    /**
     * リロード処理中に putAmmoInMagazine() が完了した後、
     * 確率で追加弾薬をマガジンに装填する（マガジン上限を超えてもよい）。
     */
    @Inject(method = "putAmmoInMagazine", at = @At("RETURN"), remap = false)
    private void tacz_attributes$bonusAmmo(int amount, CallbackInfoReturnable<Integer> cir) {
        if (!ReloadFinishingContext.isReloading()) return;
        if (shooter == null) return;

        double chance = tacz_attributes$getBonusAmmoChance();
        if (chance <= 0) return;

        if (shooter.getRandom().nextDouble() >= chance) return;

        int bonusAmount = tacz_attributes$calculateBonusAmmoAmount();
        if (bonusAmount <= 0) return;

        // マガジン上限を超えて弾薬を追加（setCurrentAmmoCount で直接設定）
        IGun iGun = IGun.getIGunOrNull(itemStack);
        if (iGun == null) return;

        int currentAmmo = iGun.getCurrentAmmoCount(itemStack);
        iGun.setCurrentAmmoCount(itemStack, currentAmmo + bonusAmount);
    }

    // === ヘルパーメソッド ===

    @Unique
    private double tacz_attributes$getAmmoSaveChance() {
        double globalChance = 0.0;
        if (shooter.getAttributes().hasAttribute(CustomAttributes.AMMO_SAVE_CHANCE.get())) {
            globalChance = shooter.getAttributeValue(CustomAttributes.AMMO_SAVE_CHANCE.get());
        }

        double typeChance = 0.0;
        GunType gunType = GunTypeResolver.resolveFromItem(itemStack);
        if (gunType != null) {
            Attribute typeAttr = gunType.getAmmoSaveChanceAttribute().get();
            if (shooter.getAttributes().hasAttribute(typeAttr)) {
                typeChance = shooter.getAttributeValue(typeAttr);
            }
        }

        return Math.min(1.0, globalChance + typeChance);
    }

    @Unique
    private double tacz_attributes$getReloadAmmoSaveChance() {
        double globalChance = 0.0;
        if (shooter.getAttributes().hasAttribute(CustomAttributes.RELOAD_AMMO_SAVE_CHANCE.get())) {
            globalChance = shooter.getAttributeValue(CustomAttributes.RELOAD_AMMO_SAVE_CHANCE.get());
        }

        double typeChance = 0.0;
        GunType gunType = GunTypeResolver.resolveFromItem(itemStack);
        if (gunType != null) {
            Attribute typeAttr = gunType.getReloadAmmoSaveChanceAttribute().get();
            if (shooter.getAttributes().hasAttribute(typeAttr)) {
                typeChance = shooter.getAttributeValue(typeAttr);
            }
        }

        return Math.min(1.0, globalChance + typeChance);
    }

    @Unique
    private double tacz_attributes$getBonusAmmoChance() {
        double globalChance = 0.0;
        if (shooter.getAttributes().hasAttribute(CustomAttributes.BONUS_AMMO_CHANCE.get())) {
            globalChance = shooter.getAttributeValue(CustomAttributes.BONUS_AMMO_CHANCE.get());
        }

        double typeChance = 0.0;
        GunType gunType = GunTypeResolver.resolveFromItem(itemStack);
        if (gunType != null) {
            Attribute typeAttr = gunType.getBonusAmmoChanceAttribute().get();
            if (shooter.getAttributes().hasAttribute(typeAttr)) {
                typeChance = shooter.getAttributeValue(typeAttr);
            }
        }

        return Math.min(1.0, globalChance + typeChance);
    }

    @Unique
    private int tacz_attributes$calculateBonusAmmoAmount() {
        GunType gunType = GunTypeResolver.resolveFromItem(itemStack);

        // 固定数（全体 + 銃種別）
        double fixedAmount = tacz_attributes$getAttributeSum(
                CustomAttributes.BONUS_AMMO_AMOUNT.get(),
                gunType != null ? gunType.getBonusAmmoAmountAttribute().get() : null);

        // 割合（全体 + 銃種別、上限 1.0）
        double percent = Math.min(1.0, tacz_attributes$getAttributeSum(
                CustomAttributes.BONUS_AMMO_PERCENT.get(),
                gunType != null ? gunType.getBonusAmmoPercentAttribute().get() : null));

        // マガジン最大容量
        int maxAmmo = tacz_attributes$getMaxAmmo();

        return (int) Math.round(fixedAmount) + (int) Math.ceil(maxAmmo * percent);
    }

    @Unique
    private double tacz_attributes$getAttributeSum(Attribute globalAttr, Attribute typeAttr) {
        double global = 0.0;
        if (shooter.getAttributes().hasAttribute(globalAttr)) {
            global = shooter.getAttributeValue(globalAttr);
        }

        double type = 0.0;
        if (typeAttr != null && shooter.getAttributes().hasAttribute(typeAttr)) {
            type = shooter.getAttributeValue(typeAttr);
        }

        return global + type;
    }

    @Unique
    private int tacz_attributes$getMaxAmmo() {
        IGun iGun = IGun.getIGunOrNull(itemStack);
        if (iGun == null) return 0;

        ResourceLocation gunId = iGun.getGunId(itemStack);
        return TimelessAPI.getCommonGunIndex(gunId)
                .map(index -> AttachmentDataUtils.getAmmoCountWithAttachment(itemStack, index.getGunData()))
                .orElse(0);
    }
}
