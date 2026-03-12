package com.github.leopoko.tacz_attributes.attribute;

import com.github.leopoko.tacz_attributes.Tacz_attributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CustomAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, Tacz_attributes.MODID);

    // === 全銃共通属性 ===

    // 全銃弾ダメージの倍率
    public static final DeferredHolder<Attribute, Attribute> GUN_DAMAGE = ATTRIBUTES.register("gun_damage",
            () -> new RangedAttribute("attribute.tacz_attributes.gun_damage", 1.0, 0.0, 1024.0));

    // 全銃リロード速度の倍率
    public static final DeferredHolder<Attribute, Attribute> RELOAD_SPEED = ATTRIBUTES.register("reload_speed",
            () -> new RangedAttribute("attribute.tacz_attributes.reload_speed", 1.0, 0.1, 20.0));

    // 全銃コッキング（ボルトアクション）速度の倍率
    public static final DeferredHolder<Attribute, Attribute> BOLT_ACTION_SPEED = ATTRIBUTES.register("bolt_action_speed",
            () -> new RangedAttribute("attribute.tacz_attributes.bolt_action_speed", 1.0, 0.1, 20.0));

    // 全銃マガジン容量の倍率
    public static final DeferredHolder<Attribute, Attribute> MAGAZINE_CAPACITY = ATTRIBUTES.register("magazine_capacity",
            () -> new RangedAttribute("attribute.tacz_attributes.magazine_capacity", 1.0, 0.1, 100.0));

    // 全銃の弾薬を消費しない確率 (0.0 = 0%, 1.0 = 100%)
    public static final DeferredHolder<Attribute, Attribute> AMMO_SAVE_CHANCE = ATTRIBUTES.register("ammo_save_chance",
            () -> new RangedAttribute("attribute.tacz_attributes.ammo_save_chance", 0.0, 0.0, 1.0));

    // === キル時弾薬回復 ===

    // キル時に弾薬が回復する確率
    public static final DeferredHolder<Attribute, Attribute> AMMO_RECOVERY_CHANCE = ATTRIBUTES.register("ammo_recovery_chance",
            () -> new RangedAttribute("attribute.tacz_attributes.ammo_recovery_chance", 0.0, 0.0, 1.0));

    // キル時に回復する弾薬の固定数
    public static final DeferredHolder<Attribute, Attribute> AMMO_RECOVERY_AMOUNT = ATTRIBUTES.register("ammo_recovery_amount",
            () -> new RangedAttribute("attribute.tacz_attributes.ammo_recovery_amount", 0.0, 0.0, 100.0));

    // キル時に回復する弾薬のマガジン容量比率
    public static final DeferredHolder<Attribute, Attribute> AMMO_RECOVERY_PERCENT = ATTRIBUTES.register("ammo_recovery_percent",
            () -> new RangedAttribute("attribute.tacz_attributes.ammo_recovery_percent", 0.0, 0.0, 1.0));

    // === リロード時弾薬非消費 ===

    // リロード時にインベントリ弾薬を消費しない確率
    public static final DeferredHolder<Attribute, Attribute> RELOAD_AMMO_SAVE_CHANCE = ATTRIBUTES.register("reload_ammo_save_chance",
            () -> new RangedAttribute("attribute.tacz_attributes.reload_ammo_save_chance", 0.0, 0.0, 1.0));

    // === リロード時追加弾薬 ===

    // リロード完了時に追加弾薬が装填される確率
    public static final DeferredHolder<Attribute, Attribute> BONUS_AMMO_CHANCE = ATTRIBUTES.register("bonus_ammo_chance",
            () -> new RangedAttribute("attribute.tacz_attributes.bonus_ammo_chance", 0.0, 0.0, 1.0));

    // 追加装填される弾薬の固定数
    public static final DeferredHolder<Attribute, Attribute> BONUS_AMMO_AMOUNT = ATTRIBUTES.register("bonus_ammo_amount",
            () -> new RangedAttribute("attribute.tacz_attributes.bonus_ammo_amount", 0.0, 0.0, 100.0));

    // 追加装填される弾薬のマガジン容量比率
    public static final DeferredHolder<Attribute, Attribute> BONUS_AMMO_PERCENT = ATTRIBUTES.register("bonus_ammo_percent",
            () -> new RangedAttribute("attribute.tacz_attributes.bonus_ammo_percent", 0.0, 0.0, 1.0));

    // 腰撃ち精度倍率（高い値 = 高精度 = 低inaccuracy）
    public static final DeferredHolder<Attribute, Attribute> HIP_FIRE_ACCURACY = ATTRIBUTES.register("hip_fire_accuracy",
            () -> new RangedAttribute("attribute.tacz_attributes.hip_fire_accuracy", 1.0, 0.01, 100.0));

    // ADS精度倍率（高い値 = 高精度 = 低inaccuracy）
    public static final DeferredHolder<Attribute, Attribute> ADS_ACCURACY = ATTRIBUTES.register("ads_accuracy",
            () -> new RangedAttribute("attribute.tacz_attributes.ads_accuracy", 1.0, 0.01, 100.0));

    // 腰撃ちダメージ倍率
    public static final DeferredHolder<Attribute, Attribute> HIP_FIRE_DAMAGE = ATTRIBUTES.register("hip_fire_damage",
            () -> new RangedAttribute("attribute.tacz_attributes.hip_fire_damage", 1.0, 0.0, 1024.0));

    // ADSダメージ倍率
    public static final DeferredHolder<Attribute, Attribute> ADS_DAMAGE = ATTRIBUTES.register("ads_damage",
            () -> new RangedAttribute("attribute.tacz_attributes.ads_damage", 1.0, 0.0, 1024.0));

    // フルオートダメージ倍率
    public static final DeferredHolder<Attribute, Attribute> AUTO_DAMAGE = ATTRIBUTES.register("auto_damage",
            () -> new RangedAttribute("attribute.tacz_attributes.auto_damage", 1.0, 0.0, 1024.0));

    // セミオートダメージ倍率
    public static final DeferredHolder<Attribute, Attribute> SEMI_DAMAGE = ATTRIBUTES.register("semi_damage",
            () -> new RangedAttribute("attribute.tacz_attributes.semi_damage", 1.0, 0.0, 1024.0));

    // バーストダメージ倍率
    public static final DeferredHolder<Attribute, Attribute> BURST_DAMAGE = ATTRIBUTES.register("burst_damage",
            () -> new RangedAttribute("attribute.tacz_attributes.burst_damage", 1.0, 0.0, 1024.0));

    // フルオート精度倍率
    public static final DeferredHolder<Attribute, Attribute> AUTO_ACCURACY = ATTRIBUTES.register("auto_accuracy",
            () -> new RangedAttribute("attribute.tacz_attributes.auto_accuracy", 1.0, 0.01, 100.0));

    // セミオート精度倍率
    public static final DeferredHolder<Attribute, Attribute> SEMI_ACCURACY = ATTRIBUTES.register("semi_accuracy",
            () -> new RangedAttribute("attribute.tacz_attributes.semi_accuracy", 1.0, 0.01, 100.0));

    // バースト精度倍率
    public static final DeferredHolder<Attribute, Attribute> BURST_ACCURACY = ATTRIBUTES.register("burst_accuracy",
            () -> new RangedAttribute("attribute.tacz_attributes.burst_accuracy", 1.0, 0.01, 100.0));

    // 全般反動倍率（高い値 = 高反動）
    public static final DeferredHolder<Attribute, Attribute> RECOIL = ATTRIBUTES.register("recoil",
            () -> new RangedAttribute("attribute.tacz_attributes.recoil", 1.0, 0.0, 100.0));

    // 縦反動倍率
    public static final DeferredHolder<Attribute, Attribute> VERTICAL_RECOIL = ATTRIBUTES.register("vertical_recoil",
            () -> new RangedAttribute("attribute.tacz_attributes.vertical_recoil", 1.0, 0.0, 100.0));

    // 横反動倍率
    public static final DeferredHolder<Attribute, Attribute> HORIZONTAL_RECOIL = ATTRIBUTES.register("horizontal_recoil",
            () -> new RangedAttribute("attribute.tacz_attributes.horizontal_recoil", 1.0, 0.0, 100.0));

    // ADS反動倍率
    public static final DeferredHolder<Attribute, Attribute> ADS_RECOIL = ATTRIBUTES.register("ads_recoil",
            () -> new RangedAttribute("attribute.tacz_attributes.ads_recoil", 1.0, 0.0, 100.0));

    // ADS縦反動倍率
    public static final DeferredHolder<Attribute, Attribute> ADS_VERTICAL_RECOIL = ATTRIBUTES.register("ads_vertical_recoil",
            () -> new RangedAttribute("attribute.tacz_attributes.ads_vertical_recoil", 1.0, 0.0, 100.0));

    // ADS横反動倍率
    public static final DeferredHolder<Attribute, Attribute> ADS_HORIZONTAL_RECOIL = ATTRIBUTES.register("ads_horizontal_recoil",
            () -> new RangedAttribute("attribute.tacz_attributes.ads_horizontal_recoil", 1.0, 0.0, 100.0));

    // 腰撃ち反動倍率
    public static final DeferredHolder<Attribute, Attribute> HIP_FIRE_RECOIL = ATTRIBUTES.register("hip_fire_recoil",
            () -> new RangedAttribute("attribute.tacz_attributes.hip_fire_recoil", 1.0, 0.0, 100.0));

    // 腰撃ち縦反動倍率
    public static final DeferredHolder<Attribute, Attribute> HIP_FIRE_VERTICAL_RECOIL = ATTRIBUTES.register("hip_fire_vertical_recoil",
            () -> new RangedAttribute("attribute.tacz_attributes.hip_fire_vertical_recoil", 1.0, 0.0, 100.0));

    // 腰撃ち横反動倍率
    public static final DeferredHolder<Attribute, Attribute> HIP_FIRE_HORIZONTAL_RECOIL = ATTRIBUTES.register("hip_fire_horizontal_recoil",
            () -> new RangedAttribute("attribute.tacz_attributes.hip_fire_horizontal_recoil", 1.0, 0.0, 100.0));

    // 銃装備時移動速度倍率（1.0 = 変更なし、0.5 = 50%速度、2.0 = 200%速度）
    public static final DeferredHolder<Attribute, Attribute> GUN_MOVEMENT_SPEED = ATTRIBUTES.register("gun_movement_speed",
            () -> new RangedAttribute("attribute.tacz_attributes.gun_movement_speed", 1.0, 0.01, 10.0));

    // ヘッドショット倍率（1.0 = 変更なし、2.0 = ヘッドショットダメージ2倍）
    public static final DeferredHolder<Attribute, Attribute> HEADSHOT_MULTIPLIER = ATTRIBUTES.register("headshot_multiplier",
            () -> new RangedAttribute("attribute.tacz_attributes.headshot_multiplier", 1.0, 0.0, 100.0));

    // ノックバック倍率（1.0 = 変更なし、0.0 = ノックバックなし、2.0 = 2倍）
    public static final DeferredHolder<Attribute, Attribute> KNOCKBACK_MULTIPLIER = ATTRIBUTES.register("knockback_multiplier",
            () -> new RangedAttribute("attribute.tacz_attributes.knockback_multiplier", 1.0, 0.0, 100.0));

    // ノックバック基本値（0.0 = 追加なし、0.4 = バニラ攻撃相当のノックバック追加）
    public static final DeferredHolder<Attribute, Attribute> KNOCKBACK_BASE = ATTRIBUTES.register("knockback_base",
            () -> new RangedAttribute("attribute.tacz_attributes.knockback_base", 0.0, 0.0, 100.0));

    // 貫通数倍率（1.0 = 変更なし、2.0 = 貫通数2倍）
    public static final DeferredHolder<Attribute, Attribute> PIERCE_MULTIPLIER = ATTRIBUTES.register("pierce_multiplier",
            () -> new RangedAttribute("attribute.tacz_attributes.pierce_multiplier", 1.0, 0.01, 100.0));

    // 発射レート(RPM)倍率（1.0 = 変更なし、2.0 = 2倍速射）
    public static final DeferredHolder<Attribute, Attribute> RPM_MULTIPLIER = ATTRIBUTES.register("rpm_multiplier",
            () -> new RangedAttribute("attribute.tacz_attributes.rpm_multiplier", 1.0, 0.01, 10.0));

    // ADS移行速度倍率（1.0 = 変更なし、2.0 = ADS2倍速）
    public static final DeferredHolder<Attribute, Attribute> ADS_SPEED = ATTRIBUTES.register("ads_speed",
            () -> new RangedAttribute("attribute.tacz_attributes.ads_speed", 1.0, 0.01, 10.0));

    // セミオート弾数倍率（1.0 = 変更なし、2.0 = 弾数2倍）
    public static final DeferredHolder<Attribute, Attribute> SEMI_BULLET_AMOUNT = ATTRIBUTES.register("semi_bullet_amount",
            () -> new RangedAttribute("attribute.tacz_attributes.semi_bullet_amount", 1.0, 0.01, 100.0));

    // フルオート弾数倍率（1.0 = 変更なし、2.0 = 弾数2倍）
    public static final DeferredHolder<Attribute, Attribute> AUTO_BULLET_AMOUNT = ATTRIBUTES.register("auto_bullet_amount",
            () -> new RangedAttribute("attribute.tacz_attributes.auto_bullet_amount", 1.0, 0.01, 100.0));

    // バースト弾数倍率（1.0 = 変更なし、2.0 = バースト数2倍）
    public static final DeferredHolder<Attribute, Attribute> BURST_BULLET_AMOUNT = ATTRIBUTES.register("burst_bullet_amount",
            () -> new RangedAttribute("attribute.tacz_attributes.burst_bullet_amount", 1.0, 0.01, 100.0));

    // 武器切替速度倍率（1.0 = 変更なし、2.0 = 取り出し/しまい2倍速）
    public static final DeferredHolder<Attribute, Attribute> DRAW_SPEED = ATTRIBUTES.register("draw_speed",
            () -> new RangedAttribute("attribute.tacz_attributes.draw_speed", 1.0, 0.01, 10.0));

    // バースト速度倍率（1.0 = 変更なし、2.0 = バースト間隔半分）
    public static final DeferredHolder<Attribute, Attribute> BURST_SPEED = ATTRIBUTES.register("burst_speed",
            () -> new RangedAttribute("attribute.tacz_attributes.burst_speed", 1.0, 0.01, 10.0));

    // 銃種別属性の一括登録
    static {
        GunType.registerAll(ATTRIBUTES);
    }
}