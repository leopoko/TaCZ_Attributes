package com.github.leopoko.tacz_attributes.mixin;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.client.event.CameraSetupEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * 反動属性をカメラリコイルに適用するMixin。
 * <p>
 * CameraSetupEvent.initialCameraRecoil() 内の genPitchSplineFunction / genYawSplineFunction
 * 呼び出しの引数を @ModifyArg で変更し、反動属性の倍率をmodifierに乗算する。
 * <p>
 * 適用される反動属性:
 * - 全般反動倍率（全体 × 銃種別）
 * - 縦/横反動倍率（全体 × 銃種別）
 * - ADS/腰撃ち反動倍率（全体 × 銃種別）
 * - ADS/腰撃ち縦/横反動倍率（全体 × 銃種別）
 * <p>
 * 計算式（Pitch）: modifier × 全般反動 × 縦反動 × ADS/腰撃ち反動 × ADS/腰撃ち縦反動
 *                  × 銃種全般反動 × 銃種縦反動 × 銃種ADS/腰撃ち反動 × 銃種ADS/腰撃ち縦反動
 * 計算式（Yaw）:   modifier × 全般反動 × 横反動 × ADS/腰撃ち反動 × ADS/腰撃ち横反動
 *                  × 銃種全般反動 × 銃種横反動 × 銃種ADS/腰撃ち反動 × 銃種ADS/腰撃ち横反動
 */
@Mixin(CameraSetupEvent.class)
public class RecoilMixin {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * initialCameraRecoil() 内の genPitchSplineFunction(float) の引数を変更し、
     * 縦反動属性に基づいた倍率をmodifierに適用する。
     */
    @ModifyArg(
            method = "initialCameraRecoil",
            at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunRecoil;genPitchSplineFunction(F)Lorg/apache/commons/math3/analysis/polynomials/PolynomialSplineFunction;"),
            remap = false
    )
    private static float tacz_attributes$modifyPitchRecoilArg(float modifier) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return modifier;

        boolean isAds = IGunOperator.fromLivingEntity(player).getSynIsAiming();
        GunType gunType = GunTypeResolver.resolveFromItem(player.getMainHandItem());

        // 全般反動 × 縦反動
        double globalRecoil = tacz_attributes$getAttributeValue(player, CustomAttributes.RECOIL.get());
        double verticalRecoil = tacz_attributes$getAttributeValue(player, CustomAttributes.VERTICAL_RECOIL.get());

        // ADS/腰撃ち反動 × ADS/腰撃ち縦反動
        double adsHipRecoil;
        double adsHipVerticalRecoil;
        if (isAds) {
            adsHipRecoil = tacz_attributes$getAttributeValue(player, CustomAttributes.ADS_RECOIL.get());
            adsHipVerticalRecoil = tacz_attributes$getAttributeValue(player, CustomAttributes.ADS_VERTICAL_RECOIL.get());
        } else {
            adsHipRecoil = tacz_attributes$getAttributeValue(player, CustomAttributes.HIP_FIRE_RECOIL.get());
            adsHipVerticalRecoil = tacz_attributes$getAttributeValue(player, CustomAttributes.HIP_FIRE_VERTICAL_RECOIL.get());
        }

        // 銃種別
        double typeRecoil = 1.0, typeVertical = 1.0, typeAdsHip = 1.0, typeAdsHipVertical = 1.0;
        if (gunType != null) {
            typeRecoil = tacz_attributes$getAttributeValue(player, gunType.getRecoilAttribute().get());
            typeVertical = tacz_attributes$getAttributeValue(player, gunType.getVerticalRecoilAttribute().get());
            if (isAds) {
                typeAdsHip = tacz_attributes$getAttributeValue(player, gunType.getAdsRecoilAttribute().get());
                typeAdsHipVertical = tacz_attributes$getAttributeValue(player, gunType.getAdsVerticalRecoilAttribute().get());
            } else {
                typeAdsHip = tacz_attributes$getAttributeValue(player, gunType.getHipFireRecoilAttribute().get());
                typeAdsHipVertical = tacz_attributes$getAttributeValue(player, gunType.getHipFireVerticalRecoilAttribute().get());
            }
        }

        double combinedModifier = globalRecoil * verticalRecoil * adsHipRecoil * adsHipVerticalRecoil
                * typeRecoil * typeVertical * typeAdsHip * typeAdsHipVertical;
        if (combinedModifier == 1.0) return modifier;

        if (!FMLEnvironment.production) {
            LOGGER.info("[TaCZ Attributes] 縦反動倍率適用: modifier {} -> {} (全般: {}, 縦: {}, {}: {}×{}, 銃種[{}]: {}×{}×{}×{})",
                    modifier, modifier * (float) combinedModifier, globalRecoil, verticalRecoil,
                    isAds ? "ADS" : "腰撃ち", adsHipRecoil, adsHipVerticalRecoil,
                    gunType != null ? gunType.getTypeId() : "unknown",
                    typeRecoil, typeVertical, typeAdsHip, typeAdsHipVertical);
        }

        return modifier * (float) combinedModifier;
    }

    /**
     * initialCameraRecoil() 内の genYawSplineFunction(float) の引数を変更し、
     * 横反動属性に基づいた倍率をmodifierに適用する。
     */
    @ModifyArg(
            method = "initialCameraRecoil",
            at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunRecoil;genYawSplineFunction(F)Lorg/apache/commons/math3/analysis/polynomials/PolynomialSplineFunction;"),
            remap = false
    )
    private static float tacz_attributes$modifyYawRecoilArg(float modifier) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return modifier;

        boolean isAds = IGunOperator.fromLivingEntity(player).getSynIsAiming();
        GunType gunType = GunTypeResolver.resolveFromItem(player.getMainHandItem());

        // 全般反動 × 横反動
        double globalRecoil = tacz_attributes$getAttributeValue(player, CustomAttributes.RECOIL.get());
        double horizontalRecoil = tacz_attributes$getAttributeValue(player, CustomAttributes.HORIZONTAL_RECOIL.get());

        // ADS/腰撃ち反動 × ADS/腰撃ち横反動
        double adsHipRecoil;
        double adsHipHorizontalRecoil;
        if (isAds) {
            adsHipRecoil = tacz_attributes$getAttributeValue(player, CustomAttributes.ADS_RECOIL.get());
            adsHipHorizontalRecoil = tacz_attributes$getAttributeValue(player, CustomAttributes.ADS_HORIZONTAL_RECOIL.get());
        } else {
            adsHipRecoil = tacz_attributes$getAttributeValue(player, CustomAttributes.HIP_FIRE_RECOIL.get());
            adsHipHorizontalRecoil = tacz_attributes$getAttributeValue(player, CustomAttributes.HIP_FIRE_HORIZONTAL_RECOIL.get());
        }

        // 銃種別
        double typeRecoil = 1.0, typeHorizontal = 1.0, typeAdsHip = 1.0, typeAdsHipHorizontal = 1.0;
        if (gunType != null) {
            typeRecoil = tacz_attributes$getAttributeValue(player, gunType.getRecoilAttribute().get());
            typeHorizontal = tacz_attributes$getAttributeValue(player, gunType.getHorizontalRecoilAttribute().get());
            if (isAds) {
                typeAdsHip = tacz_attributes$getAttributeValue(player, gunType.getAdsRecoilAttribute().get());
                typeAdsHipHorizontal = tacz_attributes$getAttributeValue(player, gunType.getAdsHorizontalRecoilAttribute().get());
            } else {
                typeAdsHip = tacz_attributes$getAttributeValue(player, gunType.getHipFireRecoilAttribute().get());
                typeAdsHipHorizontal = tacz_attributes$getAttributeValue(player, gunType.getHipFireHorizontalRecoilAttribute().get());
            }
        }

        double combinedModifier = globalRecoil * horizontalRecoil * adsHipRecoil * adsHipHorizontalRecoil
                * typeRecoil * typeHorizontal * typeAdsHip * typeAdsHipHorizontal;
        if (combinedModifier == 1.0) return modifier;

        if (!FMLEnvironment.production) {
            LOGGER.info("[TaCZ Attributes] 横反動倍率適用: modifier {} -> {} (全般: {}, 横: {}, {}: {}×{}, 銃種[{}]: {}×{}×{}×{})",
                    modifier, modifier * (float) combinedModifier, globalRecoil, horizontalRecoil,
                    isAds ? "ADS" : "腰撃ち", adsHipRecoil, adsHipHorizontalRecoil,
                    gunType != null ? gunType.getTypeId() : "unknown",
                    typeRecoil, typeHorizontal, typeAdsHip, typeAdsHipHorizontal);
        }

        return modifier * (float) combinedModifier;
    }

    @Unique
    private static double tacz_attributes$getAttributeValue(LocalPlayer player, Attribute attribute) {
        if (player.getAttributes().hasAttribute(attribute)) {
            return player.getAttributeValue(attribute);
        }
        return 1.0;
    }
}
