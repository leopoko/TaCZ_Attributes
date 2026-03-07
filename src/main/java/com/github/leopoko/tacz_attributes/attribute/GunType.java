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
 * 各銃種ごとにダメージ倍率とリロード速度倍率の属性を保持する。
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
        }
    }
}
