package com.github.leopoko.tacz_attributes.mixin;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.FireModeHelper;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 精度属性を射撃の散布度に適用するMixin。
 * <p>
 * ModernKineticGunScriptAPI.shootOnce() 内の Math.max(FF)F 呼び出しを @Redirect し、
 * 計算された inaccuracy 値に精度属性の倍率を適用する。
 * <p>
 * 適用される精度属性:
 * - 腰撃ち/ADS精度（全体 × 銃種別）
 * - 射撃モード別精度（全体 × 銃種別）
 * <p>
 * 計算式: final_inaccuracy = base_inaccuracy / (全体精度 × 銃種別精度 × モード全体精度 × モード銃種別精度)
 */
@Mixin(ModernKineticGunScriptAPI.class)
public class ShootInaccuracyMixin {

    private static final Logger LOGGER = LogManager.getLogger();

    @Shadow(remap = false)
    private LivingEntity shooter;

    @Shadow(remap = false)
    private ItemStack itemStack;

    /**
     * shootOnce() 内の Math.max(0, inaccuracy) を差し替え、
     * 精度属性に基づいた倍率を適用する。
     */
    @Redirect(
            method = "shootOnce",
            at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"),
            remap = false
    )
    private float tacz_attributes$modifyInaccuracy(float a, float b) {
        float originalInaccuracy = Math.max(a, b);
        if (shooter == null) return originalInaccuracy;

        // ADS / 腰撃ち精度
        boolean isAds = IGunOperator.fromLivingEntity(shooter).getSynIsAiming();
        GunType gunType = GunTypeResolver.resolveFromItem(itemStack);

        double adsHipGlobal;
        double adsHipType = 1.0;
        if (isAds) {
            adsHipGlobal = tacz_attributes$getAttributeValue(CustomAttributes.ADS_ACCURACY.get());
            if (gunType != null) {
                adsHipType = tacz_attributes$getAttributeValue(gunType.getAdsAccuracyAttribute().get());
            }
        } else {
            adsHipGlobal = tacz_attributes$getAttributeValue(CustomAttributes.HIP_FIRE_ACCURACY.get());
            if (gunType != null) {
                adsHipType = tacz_attributes$getAttributeValue(gunType.getHipFireAccuracyAttribute().get());
            }
        }

        // 射撃モード別精度
        FireMode fireMode = FireModeHelper.getFireMode(itemStack);
        double fireModeGlobal = FireModeHelper.getAttributeValue(shooter, FireModeHelper.getGlobalAccuracyAttribute(fireMode));
        double fireModeType = FireModeHelper.getAttributeValue(shooter, FireModeHelper.getTypeAccuracyAttribute(gunType, fireMode));

        double combinedAccuracy = adsHipGlobal * adsHipType * fireModeGlobal * fireModeType;
        if (combinedAccuracy == 1.0) return originalInaccuracy;

        float modifiedInaccuracy = (float) (originalInaccuracy / combinedAccuracy);

        if (!FMLEnvironment.production) {
            LOGGER.info("[TaCZ Attributes] 精度属性適用: inaccuracy {} -> {} (ADS: {}, 腰撃ち/ADS精度: {}×{}, モード[{}]精度: {}×{})",
                    originalInaccuracy, modifiedInaccuracy, isAds,
                    adsHipGlobal, adsHipType,
                    fireMode != null ? fireMode.name() : "unknown",
                    fireModeGlobal, fireModeType);
        }

        return modifiedInaccuracy;
    }

    @Unique
    private double tacz_attributes$getAttributeValue(Attribute attribute) {
        if (shooter.getAttributes().hasAttribute(attribute)) {
            return shooter.getAttributeValue(attribute);
        }
        return 1.0;
    }
}
