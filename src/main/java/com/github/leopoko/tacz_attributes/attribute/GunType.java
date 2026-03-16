package com.github.leopoko.tacz_attributes.attribute;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

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
    private RegistryObject<Attribute> damageAttribute;
    private RegistryObject<Attribute> reloadSpeedAttribute;
    private RegistryObject<Attribute> boltActionSpeedAttribute;
    private RegistryObject<Attribute> magazineCapacityAttribute;
    private RegistryObject<Attribute> ammoSaveChanceAttribute;
    private RegistryObject<Attribute> ammoRecoveryChanceAttribute;
    private RegistryObject<Attribute> ammoRecoveryAmountAttribute;
    private RegistryObject<Attribute> ammoRecoveryPercentAttribute;
    private RegistryObject<Attribute> reloadAmmoSaveChanceAttribute;
    private RegistryObject<Attribute> bonusAmmoChanceAttribute;
    private RegistryObject<Attribute> bonusAmmoAmountAttribute;
    private RegistryObject<Attribute> bonusAmmoPercentAttribute;
    private RegistryObject<Attribute> hipFireAccuracyAttribute;
    private RegistryObject<Attribute> adsAccuracyAttribute;
    private RegistryObject<Attribute> hipFireDamageAttribute;
    private RegistryObject<Attribute> adsDamageAttribute;
    private RegistryObject<Attribute> autoDamageAttribute;
    private RegistryObject<Attribute> semiDamageAttribute;
    private RegistryObject<Attribute> burstDamageAttribute;
    private RegistryObject<Attribute> autoAccuracyAttribute;
    private RegistryObject<Attribute> semiAccuracyAttribute;
    private RegistryObject<Attribute> burstAccuracyAttribute;
    private RegistryObject<Attribute> recoilAttribute;
    private RegistryObject<Attribute> verticalRecoilAttribute;
    private RegistryObject<Attribute> horizontalRecoilAttribute;
    private RegistryObject<Attribute> adsRecoilAttribute;
    private RegistryObject<Attribute> adsVerticalRecoilAttribute;
    private RegistryObject<Attribute> adsHorizontalRecoilAttribute;
    private RegistryObject<Attribute> hipFireRecoilAttribute;
    private RegistryObject<Attribute> hipFireVerticalRecoilAttribute;
    private RegistryObject<Attribute> hipFireHorizontalRecoilAttribute;
    private RegistryObject<Attribute> gunMovementSpeedAttribute;
    private RegistryObject<Attribute> headshotMultiplierAttribute;
    private RegistryObject<Attribute> knockbackMultiplierAttribute;
    private RegistryObject<Attribute> knockbackBaseAttribute;
    private RegistryObject<Attribute> pierceMultiplierAttribute;
    private RegistryObject<Attribute> rpmMultiplierAttribute;
    private RegistryObject<Attribute> adsSpeedAttribute;
    private RegistryObject<Attribute> semiBulletAmountAttribute;
    private RegistryObject<Attribute> autoBulletAmountAttribute;
    private RegistryObject<Attribute> burstBulletAmountAttribute;
    private RegistryObject<Attribute> drawSpeedAttribute;
    private RegistryObject<Attribute> holsterSpeedAttribute;
    private RegistryObject<Attribute> burstSpeedAttribute;
    private RegistryObject<Attribute> bulletVelocityAttribute;
    private RegistryObject<Attribute> bulletLifeAttribute;

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

    public RegistryObject<Attribute> getDamageAttribute() {
        return damageAttribute;
    }

    public RegistryObject<Attribute> getReloadSpeedAttribute() {
        return reloadSpeedAttribute;
    }

    public RegistryObject<Attribute> getBoltActionSpeedAttribute() {
        return boltActionSpeedAttribute;
    }

    public RegistryObject<Attribute> getMagazineCapacityAttribute() {
        return magazineCapacityAttribute;
    }

    public RegistryObject<Attribute> getAmmoSaveChanceAttribute() {
        return ammoSaveChanceAttribute;
    }

    public RegistryObject<Attribute> getAmmoRecoveryChanceAttribute() {
        return ammoRecoveryChanceAttribute;
    }

    public RegistryObject<Attribute> getAmmoRecoveryAmountAttribute() {
        return ammoRecoveryAmountAttribute;
    }

    public RegistryObject<Attribute> getAmmoRecoveryPercentAttribute() {
        return ammoRecoveryPercentAttribute;
    }

    public RegistryObject<Attribute> getReloadAmmoSaveChanceAttribute() {
        return reloadAmmoSaveChanceAttribute;
    }

    public RegistryObject<Attribute> getBonusAmmoChanceAttribute() {
        return bonusAmmoChanceAttribute;
    }

    public RegistryObject<Attribute> getBonusAmmoAmountAttribute() {
        return bonusAmmoAmountAttribute;
    }

    public RegistryObject<Attribute> getBonusAmmoPercentAttribute() {
        return bonusAmmoPercentAttribute;
    }

    public RegistryObject<Attribute> getHipFireAccuracyAttribute() {
        return hipFireAccuracyAttribute;
    }

    public RegistryObject<Attribute> getAdsAccuracyAttribute() {
        return adsAccuracyAttribute;
    }

    public RegistryObject<Attribute> getHipFireDamageAttribute() {
        return hipFireDamageAttribute;
    }

    public RegistryObject<Attribute> getAdsDamageAttribute() {
        return adsDamageAttribute;
    }

    public RegistryObject<Attribute> getAutoDamageAttribute() {
        return autoDamageAttribute;
    }

    public RegistryObject<Attribute> getSemiDamageAttribute() {
        return semiDamageAttribute;
    }

    public RegistryObject<Attribute> getBurstDamageAttribute() {
        return burstDamageAttribute;
    }

    public RegistryObject<Attribute> getAutoAccuracyAttribute() {
        return autoAccuracyAttribute;
    }

    public RegistryObject<Attribute> getSemiAccuracyAttribute() {
        return semiAccuracyAttribute;
    }

    public RegistryObject<Attribute> getBurstAccuracyAttribute() {
        return burstAccuracyAttribute;
    }

    public RegistryObject<Attribute> getRecoilAttribute() {
        return recoilAttribute;
    }

    public RegistryObject<Attribute> getVerticalRecoilAttribute() {
        return verticalRecoilAttribute;
    }

    public RegistryObject<Attribute> getHorizontalRecoilAttribute() {
        return horizontalRecoilAttribute;
    }

    public RegistryObject<Attribute> getAdsRecoilAttribute() {
        return adsRecoilAttribute;
    }

    public RegistryObject<Attribute> getAdsVerticalRecoilAttribute() {
        return adsVerticalRecoilAttribute;
    }

    public RegistryObject<Attribute> getAdsHorizontalRecoilAttribute() {
        return adsHorizontalRecoilAttribute;
    }

    public RegistryObject<Attribute> getHipFireRecoilAttribute() {
        return hipFireRecoilAttribute;
    }

    public RegistryObject<Attribute> getHipFireVerticalRecoilAttribute() {
        return hipFireVerticalRecoilAttribute;
    }

    public RegistryObject<Attribute> getHipFireHorizontalRecoilAttribute() {
        return hipFireHorizontalRecoilAttribute;
    }

    public RegistryObject<Attribute> getGunMovementSpeedAttribute() {
        return gunMovementSpeedAttribute;
    }

    public RegistryObject<Attribute> getHeadshotMultiplierAttribute() {
        return headshotMultiplierAttribute;
    }

    public RegistryObject<Attribute> getKnockbackMultiplierAttribute() {
        return knockbackMultiplierAttribute;
    }

    public RegistryObject<Attribute> getKnockbackBaseAttribute() {
        return knockbackBaseAttribute;
    }

    public RegistryObject<Attribute> getPierceMultiplierAttribute() {
        return pierceMultiplierAttribute;
    }

    public RegistryObject<Attribute> getRpmMultiplierAttribute() {
        return rpmMultiplierAttribute;
    }

    public RegistryObject<Attribute> getAdsSpeedAttribute() {
        return adsSpeedAttribute;
    }

    public RegistryObject<Attribute> getSemiBulletAmountAttribute() {
        return semiBulletAmountAttribute;
    }

    public RegistryObject<Attribute> getAutoBulletAmountAttribute() {
        return autoBulletAmountAttribute;
    }

    public RegistryObject<Attribute> getBurstBulletAmountAttribute() {
        return burstBulletAmountAttribute;
    }

    public RegistryObject<Attribute> getDrawSpeedAttribute() {
        return drawSpeedAttribute;
    }

    public RegistryObject<Attribute> getHolsterSpeedAttribute() {
        return holsterSpeedAttribute;
    }

    public RegistryObject<Attribute> getBurstSpeedAttribute() {
        return burstSpeedAttribute;
    }

    public RegistryObject<Attribute> getBulletVelocityAttribute() {
        return bulletVelocityAttribute;
    }

    public RegistryObject<Attribute> getBulletLifeAttribute() {
        return bulletLifeAttribute;
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
            type.holsterSpeedAttribute = registerSpeedMultiplier(registry, id + "_holster_speed");
            type.burstSpeedAttribute = registerSpeedMultiplier(registry, id + "_burst_speed");
            type.bulletVelocityAttribute = registerSpeedMultiplier(registry, id + "_bullet_velocity");
            type.bulletLifeAttribute = registerSpeedMultiplier(registry, id + "_bullet_life");
        }
    }

    private static RegistryObject<Attribute> registerDamage(DeferredRegister<Attribute> registry, String name) {
        return registry.register(name,
                () -> new RangedAttribute("attribute.tacz_attributes." + name, 1.0, 0.0, 1024.0).setSyncable(true));
    }

    private static RegistryObject<Attribute> registerAccuracy(DeferredRegister<Attribute> registry, String name) {
        return registry.register(name,
                () -> new RangedAttribute("attribute.tacz_attributes." + name, 1.0, 0.01, 100.0).setSyncable(true));
    }

    private static RegistryObject<Attribute> registerReloadSpeed(DeferredRegister<Attribute> registry, String name) {
        return registry.register(name,
                () -> new RangedAttribute("attribute.tacz_attributes." + name, 1.0, 0.1, 20.0).setSyncable(true));
    }

    private static RegistryObject<Attribute> registerRecoil(DeferredRegister<Attribute> registry, String name) {
        return registry.register(name,
                () -> new RangedAttribute("attribute.tacz_attributes." + name, 1.0, 0.0, 100.0).setSyncable(true));
    }

    private static RegistryObject<Attribute> registerGenericMultiplier(DeferredRegister<Attribute> registry, String name) {
        return registry.register(name,
                () -> new RangedAttribute("attribute.tacz_attributes." + name, 1.0, 0.0, 100.0).setSyncable(true));
    }

    private static RegistryObject<Attribute> registerPierceMultiplier(DeferredRegister<Attribute> registry, String name) {
        return registry.register(name,
                () -> new RangedAttribute("attribute.tacz_attributes." + name, 1.0, 0.01, 100.0).setSyncable(true));
    }

    private static RegistryObject<Attribute> registerSpeedMultiplier(DeferredRegister<Attribute> registry, String name) {
        return registry.register(name,
                () -> new RangedAttribute("attribute.tacz_attributes." + name, 1.0, 0.01, 10.0).setSyncable(true));
    }

    private static RegistryObject<Attribute> registerBulletAmount(DeferredRegister<Attribute> registry, String name) {
        return registry.register(name,
                () -> new RangedAttribute("attribute.tacz_attributes." + name, 1.0, 0.01, 100.0).setSyncable(true));
    }

    private static RegistryObject<Attribute> registerKnockbackBase(DeferredRegister<Attribute> registry, String name) {
        return registry.register(name,
                () -> new RangedAttribute("attribute.tacz_attributes." + name, 0.0, 0.0, 100.0).setSyncable(true));
    }

    private static RegistryObject<Attribute> registerChance(DeferredRegister<Attribute> registry, String name) {
        return registry.register(name,
                () -> new RangedAttribute("attribute.tacz_attributes." + name, 0.0, 0.0, 1.0).setSyncable(true));
    }
}
