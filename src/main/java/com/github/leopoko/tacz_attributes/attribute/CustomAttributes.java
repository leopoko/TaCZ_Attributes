package com.github.leopoko.tacz_attributes.attribute;

import com.github.leopoko.tacz_attributes.Tacz_attributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;

public class CustomAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Tacz_attributes.MODID);

    // === 全銃共通属性 ===

    // 全銃弾ダメージの倍率
    public static final RegistryObject<Attribute> GUN_DAMAGE = ATTRIBUTES.register("gun_damage",
            () -> new RangedAttribute("attribute.tacz_attributes.gun_damage", 1.0, 0.0, 1024.0).setSyncable(true));

    // 全銃リロード速度の倍率
    public static final RegistryObject<Attribute> RELOAD_SPEED = ATTRIBUTES.register("reload_speed",
            () -> new RangedAttribute("attribute.tacz_attributes.reload_speed", 1.0, 0.1, 20.0).setSyncable(true));

    // 全銃コッキング（ボルトアクション）速度の倍率
    public static final RegistryObject<Attribute> BOLT_ACTION_SPEED = ATTRIBUTES.register("bolt_action_speed",
            () -> new RangedAttribute("attribute.tacz_attributes.bolt_action_speed", 1.0, 0.1, 20.0).setSyncable(true));

    // 全銃マガジン容量の倍率
    public static final RegistryObject<Attribute> MAGAZINE_CAPACITY = ATTRIBUTES.register("magazine_capacity",
            () -> new RangedAttribute("attribute.tacz_attributes.magazine_capacity", 1.0, 0.1, 100.0).setSyncable(true));

    // 全銃の弾薬を消費しない確率 (0.0 = 0%, 1.0 = 100%)
    public static final RegistryObject<Attribute> AMMO_SAVE_CHANCE = ATTRIBUTES.register("ammo_save_chance",
            () -> new RangedAttribute("attribute.tacz_attributes.ammo_save_chance", 0.0, 0.0, 1.0).setSyncable(true));

    // === キル時弾薬回復 ===

    // キル時に弾薬が回復する確率
    public static final RegistryObject<Attribute> AMMO_RECOVERY_CHANCE = ATTRIBUTES.register("ammo_recovery_chance",
            () -> new RangedAttribute("attribute.tacz_attributes.ammo_recovery_chance", 0.0, 0.0, 1.0).setSyncable(true));

    // キル時に回復する弾薬の固定数
    public static final RegistryObject<Attribute> AMMO_RECOVERY_AMOUNT = ATTRIBUTES.register("ammo_recovery_amount",
            () -> new RangedAttribute("attribute.tacz_attributes.ammo_recovery_amount", 0.0, 0.0, 100.0).setSyncable(true));

    // キル時に回復する弾薬のマガジン容量比率
    public static final RegistryObject<Attribute> AMMO_RECOVERY_PERCENT = ATTRIBUTES.register("ammo_recovery_percent",
            () -> new RangedAttribute("attribute.tacz_attributes.ammo_recovery_percent", 0.0, 0.0, 1.0).setSyncable(true));

    // === リロード時弾薬非消費 ===

    // リロード時にインベントリ弾薬を消費しない確率
    public static final RegistryObject<Attribute> RELOAD_AMMO_SAVE_CHANCE = ATTRIBUTES.register("reload_ammo_save_chance",
            () -> new RangedAttribute("attribute.tacz_attributes.reload_ammo_save_chance", 0.0, 0.0, 1.0).setSyncable(true));

    // === リロード時追加弾薬 ===

    // リロード完了時に追加弾薬が装填される確率
    public static final RegistryObject<Attribute> BONUS_AMMO_CHANCE = ATTRIBUTES.register("bonus_ammo_chance",
            () -> new RangedAttribute("attribute.tacz_attributes.bonus_ammo_chance", 0.0, 0.0, 1.0).setSyncable(true));

    // 追加装填される弾薬の固定数
    public static final RegistryObject<Attribute> BONUS_AMMO_AMOUNT = ATTRIBUTES.register("bonus_ammo_amount",
            () -> new RangedAttribute("attribute.tacz_attributes.bonus_ammo_amount", 0.0, 0.0, 100.0).setSyncable(true));

    // 追加装填される弾薬のマガジン容量比率
    public static final RegistryObject<Attribute> BONUS_AMMO_PERCENT = ATTRIBUTES.register("bonus_ammo_percent",
            () -> new RangedAttribute("attribute.tacz_attributes.bonus_ammo_percent", 0.0, 0.0, 1.0).setSyncable(true));

    // 銃種別属性の一括登録
    static {
        GunType.registerAll(ATTRIBUTES);
    }
}