package com.github.leopoko.tacz_attributes;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.FireModeHelper;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.item.gun.FireMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = Tacz_attributes.MODID)
public class GunDamageModifier {
    @SubscribeEvent
    public static void onGunHurt(EntityHurtByGunEvent.Pre event) {
        if (event.getLogicalSide() != LogicalSide.SERVER) return;

        LivingEntity attacker = event.getAttacker();
        if (attacker == null) return;

        ResourceLocation gunId = event.getGunId();
        GunType gunType = GunTypeResolver.resolve(gunId);

        // 全体ダメージ倍率
        double globalModifier = getAttributeValue(attacker, CustomAttributes.GUN_DAMAGE);

        // 銃種別ダメージ倍率
        double typeModifier = 1.0;
        if (gunType != null) {
            typeModifier = getAttributeValue(attacker, gunType.getDamageAttribute());
        }

        // ADS / 腰撃ちダメージ倍率
        boolean isAds = IGunOperator.fromLivingEntity(attacker).getSynIsAiming();
        double adsHipGlobal;
        double adsHipType = 1.0;
        if (isAds) {
            adsHipGlobal = getAttributeValue(attacker, CustomAttributes.ADS_DAMAGE);
            if (gunType != null) {
                adsHipType = getAttributeValue(attacker, gunType.getAdsDamageAttribute());
            }
        } else {
            adsHipGlobal = getAttributeValue(attacker, CustomAttributes.HIP_FIRE_DAMAGE);
            if (gunType != null) {
                adsHipType = getAttributeValue(attacker, gunType.getHipFireDamageAttribute());
            }
        }

        // 射撃モード別ダメージ倍率
        FireMode fireMode = FireModeHelper.getFireMode(attacker.getMainHandItem());
        double fireModeGlobal = FireModeHelper.getAttributeValue(attacker, FireModeHelper.getGlobalDamageAttribute(fireMode));
        double fireModeType = FireModeHelper.getAttributeValue(attacker, FireModeHelper.getTypeDamageAttribute(gunType, fireMode));

        double combinedModifier = globalModifier * typeModifier * adsHipGlobal * adsHipType * fireModeGlobal * fireModeType;
        if (combinedModifier != 1.0) {
            float modifiedDamage = (float) (event.getBaseAmount() * combinedModifier);

            event.setBaseAmount(modifiedDamage);
        }

        // ヘッドショット倍率の適用
        if (event.isHeadShot()) {
            double hsGlobal = getAttributeValue(attacker, CustomAttributes.HEADSHOT_MULTIPLIER);
            double hsType = 1.0;
            if (gunType != null) {
                hsType = getAttributeValue(attacker, gunType.getHeadshotMultiplierAttribute());
            }
            double hsCombined = hsGlobal * hsType;
            if (hsCombined != 1.0) {
                float newHsMultiplier = (float) (event.getHeadshotMultiplier() * hsCombined);

                event.setHeadshotMultiplier(newHsMultiplier);
            }
        }
    }

    private static double getAttributeValue(LivingEntity entity, Holder<Attribute> attribute) {
        if (entity.getAttributes().hasAttribute(attribute)) {
            return entity.getAttributeValue(attribute);
        }
        return 1.0;
    }
}
