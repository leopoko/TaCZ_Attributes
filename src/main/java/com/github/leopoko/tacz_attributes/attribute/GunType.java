package com.github.leopoko.tacz_attributes.attribute;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * TaCZ の銃種分類に対応する enum。
 * 各銃種ごとにダメージ倍率、リロード速度倍率、コッキング速度倍率、マガジン容量倍率、
 * 弾薬非消費確率、キル時弾薬回復、精度倍率、反動倍率、射撃モード別倍率等の属性を保持する。
 */
public enum GunType {
    PISTOL("pistol"),
    SNIPER("sniper"),
    RIFLE("rifle"),
    SHOTGUN("shotgun"),
    SMG("smg"),
    RPG("rpg"),
    MG("mg");

    private final String typeId;
    private DeferredHolder<Attribute, Attribute> damageAttribute;
    private DeferredHolder<Attribute, Attribute> reloadSpeedAttribute;
    private DeferredHolder<Attribute, Attribute> boltActionSpeedAttribute;
    private DeferredHolder<Attribute, Attribute> magazineCapacityAttribute;
    private DeferredHolder<Attribute, Attribute> ammoSaveChanceAttribute;
    private DeferredHolder<Attribute, Attribute> ammoRecoveryChanceAttribute;
    private DeferredHolder<Attribute, Attribute> ammoRecoveryAmountAttribute;
    private DeferredHolder<Attribute, Attribute> ammoRecoveryPercentAttribute;
    private DeferredHolder<Attribute, Attribute> reloadAmmoSaveChanceAttribute;
    private DeferredHolder<Attribute, Attribute> bonusAmmoChanceAttribute;
    private DeferredHolder<Attribute, Attribute> bonusAmmoAmountAttribute;
    private DeferredHolder<Attribute, Attribute> bonusAmmoPercentAttribute;
    private DeferredHolder<Attribute, Attribute> hipFireAccuracyAttribute;
    private DeferredHolder<Attribute, Attribute> adsAccuracyAttribute;
    private DeferredHolder<Attribute, Attribute> hipFireDamageAttribute;
    private DeferredHolder<Attribute, Attribute> adsDamageAttribute;
    private DeferredHolder<Attribute, Attribute> autoDamageAttribute;
    private DeferredHolder<Attribute, Attribute> semiDamageAttribute;
    private DeferredHolder<Attribute, Attribute> burstDamageAttribute;
    private DeferredHolder<Attribute, Attribute> autoAccuracyAttribute;
    private DeferredHolder<Attribute, Attribute> semiAccuracyAttribute;
    private DeferredHolder<Attribute, Attribute> burstAccuracyAttribute;
    private DeferredHolder<Attribute, Attribute> recoilAttribute;
    private DeferredHolder<Attribute, Attribute> verticalRecoilAttribute;
    private DeferredHolder<Attribute, Attribute> horizontalRecoilAttribute;
    private DeferredHolder<Attribute, Attribute> adsRecoilAttribute;
    private DeferredHolder<Attribute, Attribute> adsVerticalRecoilAttribute;
    private DeferredHolder<Attribute, Attribute> adsHorizontalRecoilAttribute;
    private DeferredHolder<Attribute, Attribute> hipFireRecoilAttribute;
    private DeferredHolder<Attribute, Attribute> hipFireVerticalRecoilAttribute;
    private DeferredHolder<Attribute, Attribute> hipFireHorizontalRecoilAttribute;
    private DeferredHolder<Attribute, Attribute> gunMovementSpeedAttribute;
    private DeferredHolder<Attribute, Attribute> headshotMultiplierAttribute;
    private DeferredHolder<Attribute, Attribute> knockbackMultiplierAttribute;
    private DeferredHolder<Attribute, Attribute> knockbackBaseAttribute;
    private DeferredHolder<Attribute, Attribute> pierceMultiplierAttribute;
    private DeferredHolder<Attribute, Attribute> rpmMultiplierAttribute;
    private DeferredHolder<Attribute, Attribute> adsSpeedAttribute;
    private DeferredHolder<Attribute, Attribute> semiBulletAmountAttribute;
    private DeferredHolder<Attribute, Attribute> autoBulletAmountAttribute;
    private DeferredHolder<Attribute, Attribute> burstBulletAmountAttribute;
    private DeferredHolder<Attribute, Attribute> drawSpeedAttribute;
    private DeferredHolder<Attribute, Attribute> burstSpeedAttribute;

    private static final Map<String, GunType> BY_TYPE_ID = new HashMap<>();

    static {
        for (GunType type : values()) {
            BY_TYPE_ID.put(type.typeId, type);
        }
    }

    GunType(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeId() {
        return typeId;
    }

    public DeferredHolder<Attribute, Attribute> getDamageAttribute() {
        return damageAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getReloadSpeedAttribute() {
        return reloadSpeedAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getBoltActionSpeedAttribute() {
        return boltActionSpeedAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getMagazineCapacityAttribute() {
        return magazineCapacityAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getAmmoSaveChanceAttribute() {
        return ammoSaveChanceAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getAmmoRecoveryChanceAttribute() {
        return ammoRecoveryChanceAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getAmmoRecoveryAmountAttribute() {
        return ammoRecoveryAmountAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getAmmoRecoveryPercentAttribute() {
        return ammoRecoveryPercentAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getReloadAmmoSaveChanceAttribute() {
        return reloadAmmoSaveChanceAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getBonusAmmoChanceAttribute() {
        return bonusAmmoChanceAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getBonusAmmoAmountAttribute() {
        return bonusAmmoAmountAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getBonusAmmoPercentAttribute() {
        return bonusAmmoPercentAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getHipFireAccuracyAttribute() {
        return hipFireAccuracyAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getAdsAccuracyAttribute() {
        return adsAccuracyAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getHipFireDamageAttribute() {
        return hipFireDamageAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getAdsDamageAttribute() {
        return adsDamageAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getAutoDamageAttribute() {
        return autoDamageAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getSemiDamageAttribute() {
        return semiDamageAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getBurstDamageAttribute() {
        return burstDamageAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getAutoAccuracyAttribute() {
        return autoAccuracyAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getSemiAccuracyAttribute() {
        return semiAccuracyAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getBurstAccuracyAttribute() {
        return burstAccuracyAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getRecoilAttribute() {
        return recoilAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getVerticalRecoilAttribute() {
        return verticalRecoilAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getHorizontalRecoilAttribute() {
        return horizontalRecoilAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getAdsRecoilAttribute() {
        return adsRecoilAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getAdsVerticalRecoilAttribute() {
        return adsVerticalRecoilAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getAdsHorizontalRecoilAttribute() {
        return adsHorizontalRecoilAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getHipFireRecoilAttribute() {
        return hipFireRecoilAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getHipFireVerticalRecoilAttribute() {
        return hipFireVerticalRecoilAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getHipFireHorizontalRecoilAttribute() {
        return hipFireHorizontalRecoilAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getGunMovementSpeedAttribute() {
        return gunMovementSpeedAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getHeadshotMultiplierAttribute() {
        return headshotMultiplierAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getKnockbackMultiplierAttribute() {
        return knockbackMultiplierAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getKnockbackBaseAttribute() {
        return knockbackBaseAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getPierceMultiplierAttribute() {
        return pierceMultiplierAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getRpmMultiplierAttribute() {
        return rpmMultiplierAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getAdsSpeedAttribute() {
        return adsSpeedAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getSemiBulletAmountAttribute() {
        return semiBulletAmountAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getAutoBulletAmountAttribute() {
        return autoBulletAmountAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getBurstBulletAmountAttribute() {
        return burstBulletAmountAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getDrawSpeedAttribute() {
        return drawSpeedAttribute;
    }

    public DeferredHolder<Attribute, Attribute> getBurstSpeedAttribute() {
        return burstSpeedAttribute;
    }

    /**
     * TaCZ の銃種文字列（CommonGunIndex.getType() の戻り値）から GunType を取得する。
     * 不明な銃種の場合は null を返す。
     */
    @Nullable
    public static GunType fromTypeId(String typeId) {
        return BY_TYPE_ID.get(typeId);
    }

    /**
     * 全銃種の属性を DeferredRegister に一括登録する。
     * CustomAttributes の static 初期化で呼び出す。
     */
    public static void registerAll(DeferredRegister<Attribute> registry) {
        for (GunType type : values()) {
            String id = type.typeId;

            type.damageAttribute = registerDamage(registry, id + "_damage");
            type.reloadSpeedAttribute = registerReloadSpeed(registry, id + "_reload_speed");
            type.boltActionSpeedAttribute = registerReloadSpeed(registry, id + "_bolt_action_speed");
            type.magazineCapacityAttribute = registerBulletAmount(registry, id + "_magazine_capacity");
            type.ammoSaveChanceAttribute = registerChance(registry, id + "_ammo_save_chance");
            type.ammoRecoveryChanceAttribute = registerChance(registry, id + "_ammo_recovery_chance");
            type.ammoRecoveryAmountAttribute = registerKnockbackBase(registry, id + "_ammo_recovery_amount");
            type.ammoRecoveryPercentAttribute = registerChance(registry, id + "_ammo_recovery_percent");
            type.reloadAmmoSaveChanceAttribute = registerChance(registry, id + "_reload_ammo_save_chance");
            type.bonusAmmoChanceAttribute = registerChance(registry, id + "_bonus_ammo_chance");
            type.bonusAmmoAmountAttribute = registerKnockbackBase(registry, id + "_bonus_ammo_amount");
            type.bonusAmmoPercentAttribute = registerChance(registry, id + "_bonus_ammo_percent");
            type.hipFireAccuracyAttribute = registerAccuracy(registry, id + "_hip_fire_accuracy");
            type.adsAccuracyAttribute = registerAccuracy(registry, id + "_ads_accuracy");
            type.hipFireDamageAttribute = registerDamage(registry, id + "_hip_fire_damage");
            type.adsDamageAttribute = registerDamage(registry, id + "_ads_damage");
            type.autoDamageAttribute = registerDamage(registry, id + "_auto_damage");
            type.semiDamageAttribute = registerDamage(registry, id + "_semi_damage");
            type.burstDamageAttribute = registerDamage(registry, id + "_burst_damage");
            type.autoAccuracyAttribute = registerAccuracy(registry, id + "_auto_accuracy");
            type.semiAccuracyAttribute = registerAccuracy(registry, id + "_semi_accuracy");
            type.burstAccuracyAttribute = registerAccuracy(registry, id + "_burst_accuracy");
            type.recoilAttribute = registerRecoil(registry, id + "_recoil");
            type.verticalRecoilAttribute = registerRecoil(registry, id + "_vertical_recoil");
            type.horizontalRecoilAttribute = registerRecoil(registry, id + "_horizontal_recoil");
            type.adsRecoilAttribute = registerRecoil(registry, id + "_ads_recoil");
            type.adsVerticalRecoilAttribute = registerRecoil(registry, id + "_ads_vertical_recoil");
            type.adsHorizontalRecoilAttribute = registerRecoil(registry, id + "_ads_horizontal_recoil");
            type.hipFireRecoilAttribute = registerRecoil(registry, id + "_hip_fire_recoil");
            type.hipFireVerticalRecoilAttribute = registerRecoil(registry, id + "_hip_fire_vertical_recoil");
            type.hipFireHorizontalRecoilAttribute = registerRecoil(registry, id + "_hip_fire_horizontal_recoil");
            type.gunMovementSpeedAttribute = registerSpeedMultiplier(registry, id + "_gun_movement_speed");
            type.headshotMultiplierAttribute = registerGenericMultiplier(registry, id + "_headshot_multiplier");
            type.knockbackMultiplierAttribute = registerGenericMultiplier(registry, id + "_knockback_multiplier");
            type.knockbackBaseAttribute = registerKnockbackBase(registry, id + "_knockback_base");
            type.pierceMultiplierAttribute = registerPierceMultiplier(registry, id + "_pierce_multiplier");
            type.rpmMultiplierAttribute = registerSpeedMultiplier(registry, id + "_rpm_multiplier");
            type.adsSpeedAttribute = registerSpeedMultiplier(registry, id + "_ads_speed");
            type.semiBulletAmountAttribute = registerBulletAmount(registry, id + "_semi_bullet_amount");
            type.autoBulletAmountAttribute = registerBulletAmount(registry, id + "_auto_bullet_amount");
            type.burstBulletAmountAttribute = registerBulletAmount(registry, id + "_burst_bullet_amount");
            type.drawSpeedAttribute = registerSpeedMultiplier(registry, id + "_draw_speed");
            type.burstSpeedAttribute = registerSpeedMultiplier(registry, id + "_burst_speed");
        }
    }

    private static DeferredHolder<Attribute, Attribute> registerDamage(DeferredRegister<Attribute> registry, String name) {
        return registry.register(name,
                () -> new RangedAttribute("attribute.tacz_attributes." + name, 1.0, 0.0, 1024.0));
    }

    private static DeferredHolder<Attribute, Attribute> registerAccuracy(DeferredRegister<Attribute> registry, String name) {
        return registry.register(name,
                () -> new RangedAttribute("attribute.tacz_attributes." + name, 1.0, 0.01, 100.0));
    }

    private static DeferredHolder<Attribute, Attribute> registerReloadSpeed(DeferredRegister<Attribute> registry, String name) {
        return registry.register(name,
                () -> new RangedAttribute("attribute.tacz_attributes." + name, 1.0, 0.1, 20.0));
    }

    private static DeferredHolder<Attribute, Attribute> registerRecoil(DeferredRegister<Attribute> registry, String name) {
        return registry.register(name,
                () -> new RangedAttribute("attribute.tacz_attributes." + name, 1.0, 0.0, 100.0));
    }

    private static DeferredHolder<Attribute, Attribute> registerGenericMultiplier(DeferredRegister<Attribute> registry, String name) {
        return registry.register(name,
                () -> new RangedAttribute("attribute.tacz_attributes." + name, 1.0, 0.0, 100.0));
    }

    private static DeferredHolder<Attribute, Attribute> registerPierceMultiplier(DeferredRegister<Attribute> registry, String name) {
        return registry.register(name,
                () -> new RangedAttribute("attribute.tacz_attributes." + name, 1.0, 0.01, 100.0));
    }

    private static DeferredHolder<Attribute, Attribute> registerSpeedMultiplier(DeferredRegister<Attribute> registry, String name) {
        return registry.register(name,
                () -> new RangedAttribute("attribute.tacz_attributes." + name, 1.0, 0.01, 10.0));
    }

    private static DeferredHolder<Attribute, Attribute> registerBulletAmount(DeferredRegister<Attribute> registry, String name) {
        return registry.register(name,
                () -> new RangedAttribute("attribute.tacz_attributes." + name, 1.0, 0.01, 100.0));
    }

    private static DeferredHolder<Attribute, Attribute> registerKnockbackBase(DeferredRegister<Attribute> registry, String name) {
        return registry.register(name,
                () -> new RangedAttribute("attribute.tacz_attributes." + name, 0.0, 0.0, 100.0));
    }

    private static DeferredHolder<Attribute, Attribute> registerChance(DeferredRegister<Attribute> registry, String name) {
        return registry.register(name,
                () -> new RangedAttribute("attribute.tacz_attributes." + name, 0.0, 0.0, 1.0));
    }
}
