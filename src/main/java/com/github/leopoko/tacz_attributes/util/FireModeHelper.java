package com.github.leopoko.tacz_attributes.util;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.FireMode;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * FireMode（射撃モード）に基づく属性値の解決ユーティリティ。
 * 銃種別・確率系属性の取得ヘルパーも提供する。
 */
public final class FireModeHelper {

    private FireModeHelper() {}

    /**
     * ItemStack から現在の FireMode を取得する。
     */
    @Nullable
    public static FireMode getFireMode(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        IGun iGun = IGun.getIGunOrNull(stack);
        if (iGun == null) return null;
        FireMode mode = iGun.getFireMode(stack);
        return mode == FireMode.UNKNOWN ? null : mode;
    }

    /**
     * 射撃モードに対応する全体ダメージ属性を取得する。
     */
    @Nullable
    public static DeferredHolder<Attribute, Attribute> getGlobalDamageAttribute(@Nullable FireMode mode) {
        if (mode == null) return null;
        return switch (mode) {
            case AUTO -> CustomAttributes.AUTO_DAMAGE;
            case SEMI -> CustomAttributes.SEMI_DAMAGE;
            case BURST -> CustomAttributes.BURST_DAMAGE;
            default -> null;
        };
    }

    /**
     * 射撃モードに対応する全体精度属性を取得する。
     */
    @Nullable
    public static DeferredHolder<Attribute, Attribute> getGlobalAccuracyAttribute(@Nullable FireMode mode) {
        if (mode == null) return null;
        return switch (mode) {
            case AUTO -> CustomAttributes.AUTO_ACCURACY;
            case SEMI -> CustomAttributes.SEMI_ACCURACY;
            case BURST -> CustomAttributes.BURST_ACCURACY;
            default -> null;
        };
    }

    /**
     * 射撃モードに対応する銃種別ダメージ属性を取得する。
     */
    @Nullable
    public static DeferredHolder<Attribute, Attribute> getTypeDamageAttribute(@Nullable GunType gunType, @Nullable FireMode mode) {
        if (gunType == null || mode == null) return null;
        return switch (mode) {
            case AUTO -> gunType.getAutoDamageAttribute();
            case SEMI -> gunType.getSemiDamageAttribute();
            case BURST -> gunType.getBurstDamageAttribute();
            default -> null;
        };
    }

    /**
     * 射撃モードに対応する銃種別精度属性を取得する。
     */
    @Nullable
    public static DeferredHolder<Attribute, Attribute> getTypeAccuracyAttribute(@Nullable GunType gunType, @Nullable FireMode mode) {
        if (gunType == null || mode == null) return null;
        return switch (mode) {
            case AUTO -> gunType.getAutoAccuracyAttribute();
            case SEMI -> gunType.getSemiAccuracyAttribute();
            case BURST -> gunType.getBurstAccuracyAttribute();
            default -> null;
        };
    }

    /**
     * エンティティから属性値を取得する。属性が未登録の場合は 1.0 を返す。
     */
    public static double getAttributeValue(LivingEntity entity, @Nullable DeferredHolder<Attribute, Attribute> attribute) {
        if (attribute == null) return 1.0;
        if (entity.getAttributes().hasAttribute(attribute)) {
            return entity.getAttributeValue(attribute);
        }
        return 1.0;
    }

    /**
     * 銃種別属性の値を取得する。gunType==nullなら1.0を返す。
     */
    public static double getTypeAttributeValue(LivingEntity entity, @Nullable GunType gunType,
                                                Function<GunType, DeferredHolder<Attribute, Attribute>> getter) {
        if (gunType == null) return 1.0;
        return getAttributeValue(entity, getter.apply(gunType));
    }

    /**
     * 確率/加算系属性の値を取得する（デフォルト0.0）。
     */
    public static double getChanceAttributeValue(LivingEntity entity, DeferredHolder<Attribute, Attribute> attribute) {
        if (entity.getAttributes().hasAttribute(attribute)) {
            return entity.getAttributeValue(attribute);
        }
        return 0.0;
    }

    /**
     * 銃種別の確率/加算系属性の値を取得する（デフォルト0.0）。gunType==nullなら0.0を返す。
     */
    public static double getTypeChanceAttributeValue(LivingEntity entity, @Nullable GunType gunType,
                                                      Function<GunType, DeferredHolder<Attribute, Attribute>> getter) {
        if (gunType == null) return 0.0;
        DeferredHolder<Attribute, Attribute> attr = getter.apply(gunType);
        if (entity.getAttributes().hasAttribute(attr)) {
            return entity.getAttributeValue(attr);
        }
        return 0.0;
    }

    /**
     * FireModeに基づくダメージ倍率（全体×銃種別）を取得する。
     */
    public static double getFireModeDamageMultiplier(LivingEntity entity, @Nullable FireMode mode,
                                                      @Nullable GunType gunType) {
        if (mode == null) return 1.0;
        return getAttributeValue(entity, getGlobalDamageAttribute(mode))
                * getAttributeValue(entity, getTypeDamageAttribute(gunType, mode));
    }

    /**
     * FireModeに基づく精度倍率（全体×銃種別）を取得する。
     */
    public static double getFireModeAccuracyMultiplier(LivingEntity entity, @Nullable FireMode mode,
                                                        @Nullable GunType gunType) {
        if (mode == null) return 1.0;
        return getAttributeValue(entity, getGlobalAccuracyAttribute(mode))
                * getAttributeValue(entity, getTypeAccuracyAttribute(gunType, mode));
    }
}
