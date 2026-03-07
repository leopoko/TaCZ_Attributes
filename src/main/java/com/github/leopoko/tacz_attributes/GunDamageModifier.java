package com.github.leopoko.tacz_attributes;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.FireModeHelper;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.item.gun.FireMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = Tacz_attributes.MODID)
public class GunDamageModifier {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onGunHurt(EntityHurtByGunEvent.Pre event) {
        if (event.getLogicalSide() != LogicalSide.SERVER) return;

        LivingEntity attacker = event.getAttacker();
        if (attacker == null) return;

        ResourceLocation gunId = event.getGunId();
        GunType gunType = GunTypeResolver.resolve(gunId);

        // 全体ダメージ倍率
        double globalModifier = getAttributeValue(attacker, CustomAttributes.GUN_DAMAGE.get());

        // 銃種別ダメージ倍率
        double typeModifier = 1.0;
        if (gunType != null) {
            typeModifier = getAttributeValue(attacker, gunType.getDamageAttribute().get());
        }

        // ADS / 腰撃ちダメージ倍率
        boolean isAds = IGunOperator.fromLivingEntity(attacker).getSynIsAiming();
        double adsHipGlobal;
        double adsHipType = 1.0;
        if (isAds) {
            adsHipGlobal = getAttributeValue(attacker, CustomAttributes.ADS_DAMAGE.get());
            if (gunType != null) {
                adsHipType = getAttributeValue(attacker, gunType.getAdsDamageAttribute().get());
            }
        } else {
            adsHipGlobal = getAttributeValue(attacker, CustomAttributes.HIP_FIRE_DAMAGE.get());
            if (gunType != null) {
                adsHipType = getAttributeValue(attacker, gunType.getHipFireDamageAttribute().get());
            }
        }

        // 射撃モード別ダメージ倍率
        FireMode fireMode = FireModeHelper.getFireMode(attacker.getMainHandItem());
        double fireModeGlobal = FireModeHelper.getAttributeValue(attacker, FireModeHelper.getGlobalDamageAttribute(fireMode));
        double fireModeType = FireModeHelper.getAttributeValue(attacker, FireModeHelper.getTypeDamageAttribute(gunType, fireMode));

        double combinedModifier = globalModifier * typeModifier * adsHipGlobal * adsHipType * fireModeGlobal * fireModeType;
        if (combinedModifier != 1.0) {
            float modifiedDamage = (float) (event.getBaseAmount() * combinedModifier);

            if (!FMLEnvironment.production) {
                LOGGER.info("[TaCZ Attributes] 銃ダメージ倍率適用: {} -> {} (全体: {}, 銃種[{}]: {}, {}: {}×{}, モード[{}]: {}×{})",
                        event.getBaseAmount(), modifiedDamage, globalModifier,
                        gunType != null ? gunType.getTypeId() : "unknown", typeModifier,
                        isAds ? "ADS" : "腰撃ち", adsHipGlobal, adsHipType,
                        fireMode != null ? fireMode.name() : "unknown", fireModeGlobal, fireModeType);
            }

            event.setBaseAmount(modifiedDamage);
        }

        // ヘッドショット倍率の適用
        if (event.isHeadShot()) {
            double hsGlobal = getAttributeValue(attacker, CustomAttributes.HEADSHOT_MULTIPLIER.get());
            double hsType = 1.0;
            if (gunType != null) {
                hsType = getAttributeValue(attacker, gunType.getHeadshotMultiplierAttribute().get());
            }
            double hsCombined = hsGlobal * hsType;
            if (hsCombined != 1.0) {
                float newHsMultiplier = (float) (event.getHeadshotMultiplier() * hsCombined);

                if (!FMLEnvironment.production) {
                    LOGGER.info("[TaCZ Attributes] ヘッドショット倍率適用: {} -> {} (全体: {}, 銃種[{}]: {})",
                            event.getHeadshotMultiplier(), newHsMultiplier, hsGlobal,
                            gunType != null ? gunType.getTypeId() : "unknown", hsType);
                }

                event.setHeadshotMultiplier(newHsMultiplier);
            }
        }
    }

    private static double getAttributeValue(LivingEntity entity, Attribute attribute) {
        if (entity.getAttributes().hasAttribute(attribute)) {
            return entity.getAttributeValue(attribute);
        }
        return 1.0;
    }
}
