package com.github.leopoko.tacz_attributes;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.util.AttachmentDataUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

/**
 * キル時弾薬回復イベントハンドラ。
 * <p>
 * EntityKillByGunEvent を受け取り、攻撃者の属性に基づいて弾薬を回復する。
 * 回復量 = 固定数 + ceil(マガジン最大容量 × 割合)
 * マガジン最大容量を超えた回復も許可される。
 */
@Mod.EventBusSubscriber(modid = Tacz_attributes.MODID)
public class GunKillAmmoRecovery {

    @SubscribeEvent
    public static void onGunKill(EntityKillByGunEvent event) {
        if (event.getLogicalSide() != LogicalSide.SERVER) return;

        LivingEntity attacker = event.getAttacker();
        if (attacker == null) return;

        ResourceLocation gunId = event.getGunId();
        GunType gunType = GunTypeResolver.resolve(gunId);

        // 確率判定
        double chance = getAmmoRecoveryChance(attacker, gunType);
        if (chance <= 0) return;
        if (attacker.getRandom().nextDouble() >= chance) return;

        // 攻撃者のメインハンドから銃を取得
        ItemStack mainHand = attacker.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(mainHand);
        if (iGun == null) return;

        // 回復量を計算
        int recoveryAmount = calculateRecoveryAmount(attacker, mainHand, gunType);
        if (recoveryAmount <= 0) return;

        // 弾薬を回復（マガジン上限を超えてもよい）
        int currentAmmo = iGun.getCurrentAmmoCount(mainHand);
        iGun.setCurrentAmmoCount(mainHand, currentAmmo + recoveryAmount);
    }

    private static double getAmmoRecoveryChance(LivingEntity attacker, GunType gunType) {
        double globalChance = 0.0;
        if (attacker.getAttributes().hasAttribute(CustomAttributes.AMMO_RECOVERY_CHANCE.get())) {
            globalChance = attacker.getAttributeValue(CustomAttributes.AMMO_RECOVERY_CHANCE.get());
        }

        double typeChance = 0.0;
        if (gunType != null) {
            Attribute typeAttr = gunType.getAmmoRecoveryChanceAttribute().get();
            if (attacker.getAttributes().hasAttribute(typeAttr)) {
                typeChance = attacker.getAttributeValue(typeAttr);
            }
        }

        return Math.min(1.0, globalChance + typeChance);
    }

    private static int calculateRecoveryAmount(LivingEntity attacker, ItemStack mainHand, GunType gunType) {
        // 固定数（全体 + 銃種別）
        double fixedAmount = getAttributeSum(attacker,
                CustomAttributes.AMMO_RECOVERY_AMOUNT.get(),
                gunType != null ? gunType.getAmmoRecoveryAmountAttribute().get() : null);

        // 割合（全体 + 銃種別）
        double percent = Math.min(1.0, getAttributeSum(attacker,
                CustomAttributes.AMMO_RECOVERY_PERCENT.get(),
                gunType != null ? gunType.getAmmoRecoveryPercentAttribute().get() : null));

        // マガジン最大容量を取得
        int maxAmmo = getMaxAmmo(mainHand);

        return (int) Math.round(fixedAmount) + (int) Math.ceil(maxAmmo * percent);
    }

    private static double getAttributeSum(LivingEntity entity,
                                          Attribute globalAttr, Attribute typeAttr) {
        double global = 0.0;
        if (entity.getAttributes().hasAttribute(globalAttr)) {
            global = entity.getAttributeValue(globalAttr);
        }

        double type = 0.0;
        if (typeAttr != null && entity.getAttributes().hasAttribute(typeAttr)) {
            type = entity.getAttributeValue(typeAttr);
        }

        return global + type;
    }

    private static int getMaxAmmo(ItemStack gunStack) {
        IGun iGun = IGun.getIGunOrNull(gunStack);
        if (iGun == null) return 0;

        ResourceLocation gunId = iGun.getGunId(gunStack);
        return TimelessAPI.getCommonGunIndex(gunId)
                .map(index -> AttachmentDataUtils.getAmmoCountWithAttachment(gunStack, index.getGunData()))
                .orElse(0);
    }
}
