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
 * 各銃種ごとにダメージ倍率、リロード速度倍率、コッキング速度倍率、
 * マガジン容量倍率、弾薬非消費確率、キル時弾薬回復、リロード時弾薬非消費、
 * リロード時追加弾薬の属性を保持する。
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
            type.damageAttribute = registry.register(
                    type.typeId + "_damage",
                    () -> new RangedAttribute(
                            "attribute.tacz_attributes." + type.typeId + "_damage",
                            1.0, 0.0, 1024.0
                    ).setSyncable(true)
            );
            type.reloadSpeedAttribute = registry.register(
                    type.typeId + "_reload_speed",
                    () -> new RangedAttribute(
                            "attribute.tacz_attributes." + type.typeId + "_reload_speed",
                            1.0, 0.1, 20.0
                    ).setSyncable(true)
            );
            type.boltActionSpeedAttribute = registry.register(
                    type.typeId + "_bolt_action_speed",
                    () -> new RangedAttribute(
                            "attribute.tacz_attributes." + type.typeId + "_bolt_action_speed",
                            1.0, 0.1, 20.0
                    ).setSyncable(true)
            );
            type.magazineCapacityAttribute = registry.register(
                    type.typeId + "_magazine_capacity",
                    () -> new RangedAttribute(
                            "attribute.tacz_attributes." + type.typeId + "_magazine_capacity",
                            1.0, 0.1, 100.0
                    ).setSyncable(true)
            );
            type.ammoSaveChanceAttribute = registry.register(
                    type.typeId + "_ammo_save_chance",
                    () -> new RangedAttribute(
                            "attribute.tacz_attributes." + type.typeId + "_ammo_save_chance",
                            0.0, 0.0, 1.0
                    ).setSyncable(true)
            );
            type.ammoRecoveryChanceAttribute = registry.register(
                    type.typeId + "_ammo_recovery_chance",
                    () -> new RangedAttribute(
                            "attribute.tacz_attributes." + type.typeId + "_ammo_recovery_chance",
                            0.0, 0.0, 1.0
                    ).setSyncable(true)
            );
            type.ammoRecoveryAmountAttribute = registry.register(
                    type.typeId + "_ammo_recovery_amount",
                    () -> new RangedAttribute(
                            "attribute.tacz_attributes." + type.typeId + "_ammo_recovery_amount",
                            0.0, 0.0, 100.0
                    ).setSyncable(true)
            );
            type.ammoRecoveryPercentAttribute = registry.register(
                    type.typeId + "_ammo_recovery_percent",
                    () -> new RangedAttribute(
                            "attribute.tacz_attributes." + type.typeId + "_ammo_recovery_percent",
                            0.0, 0.0, 1.0
                    ).setSyncable(true)
            );
            type.reloadAmmoSaveChanceAttribute = registry.register(
                    type.typeId + "_reload_ammo_save_chance",
                    () -> new RangedAttribute(
                            "attribute.tacz_attributes." + type.typeId + "_reload_ammo_save_chance",
                            0.0, 0.0, 1.0
                    ).setSyncable(true)
            );
            type.bonusAmmoChanceAttribute = registry.register(
                    type.typeId + "_bonus_ammo_chance",
                    () -> new RangedAttribute(
                            "attribute.tacz_attributes." + type.typeId + "_bonus_ammo_chance",
                            0.0, 0.0, 1.0
                    ).setSyncable(true)
            );
            type.bonusAmmoAmountAttribute = registry.register(
                    type.typeId + "_bonus_ammo_amount",
                    () -> new RangedAttribute(
                            "attribute.tacz_attributes." + type.typeId + "_bonus_ammo_amount",
                            0.0, 0.0, 100.0
                    ).setSyncable(true)
            );
            type.bonusAmmoPercentAttribute = registry.register(
                    type.typeId + "_bonus_ammo_percent",
                    () -> new RangedAttribute(
                            "attribute.tacz_attributes." + type.typeId + "_bonus_ammo_percent",
                            0.0, 0.0, 1.0
                    ).setSyncable(true)
            );
        }
    }
}
