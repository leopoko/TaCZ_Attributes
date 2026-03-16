package com.github.leopoko.tacz_attributes.client;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.FireModeHelper;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.api.modifier.CacheValue;
import com.tacz.guns.api.modifier.IAttachmentModifier;
import com.tacz.guns.api.modifier.JsonProperty;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * TaCZ の IAttachmentModifier を実装し、プレイヤー属性の効果を
 * GunRefitScreen（Zキー）のステータスバーに表示する。
 *
 * AttachmentPropertyManager.getModifiers() マップに登録することで、
 * 既存の GunPropertyDiagrams.draw() の描画ループがそのまま属性効果を描画する。
 */
public class AttributePropertyModifier implements IAttachmentModifier<Object, Object> {

    public static final String ID = "tacz_attributes";

    /** 前フレームの DiagramsData 行数キャッシュ（背景パネルサイズ計算用） */
    private static int cachedDiagramsCount = 0;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public JsonProperty<Object> readJson(String json) {
        // アタッチメントJSONに当MODのIDは存在しないため呼ばれない
        return null;
    }

    @Override
    public CacheValue<Object> initCache(ItemStack gun, GunData data) {
        return new CacheValue<>(null);
    }

    @Override
    public void eval(List<Object> list, CacheValue<Object> cache) {
        // アタッチメントデータは使用しない
    }

    @Override
    public int getDiagramsDataSize() {
        return cachedDiagramsCount;
    }

    @Override
    public List<DiagramsData> getPropertyDiagramsData(
            ItemStack gunStack, GunData gunData, AttachmentCacheProperty cache) {
        List<DiagramsData> result = new ArrayList<>();

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            cachedDiagramsCount = 0;
            return result;
        }

        GunType gunType = GunTypeResolver.resolveFromItem(gunStack);
        FireMode fireMode = FireModeHelper.getFireMode(gunStack);

        // === TaCZモディファイアが空だった場合のフォールバック表示 ===
        // アタッチメントがない場合、TaCZモディファイアのデリゲートが空リストを返すため、
        // ラッパーが属性効果を反映できない。その場合はここで独自バーとして表示する。

        // ダメージ
        if (!DiagramsModifierWrapper.wasProcessed("damage")) {
            double damage = FireModeHelper.getAttributeValue(player, CustomAttributes.GUN_DAMAGE)
                    * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getDamageAttribute)
                    * FireModeHelper.getAttributeValue(player, CustomAttributes.HIP_FIRE_DAMAGE)
                    * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getHipFireDamageAttribute)
                    * FireModeHelper.getFireModeDamageMultiplier(player, fireMode, gunType);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.damage", damage, true);
        }

        // 縦反動・横反動
        if (!DiagramsModifierWrapper.wasProcessed("recoil")) {
            double baseRecoil = FireModeHelper.getAttributeValue(player, CustomAttributes.RECOIL)
                    * FireModeHelper.getAttributeValue(player, CustomAttributes.HIP_FIRE_RECOIL)
                    * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getRecoilAttribute)
                    * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getHipFireRecoilAttribute);

            double vertRecoil = baseRecoil
                    * FireModeHelper.getAttributeValue(player, CustomAttributes.VERTICAL_RECOIL)
                    * FireModeHelper.getAttributeValue(player, CustomAttributes.HIP_FIRE_VERTICAL_RECOIL)
                    * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getVerticalRecoilAttribute)
                    * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getHipFireVerticalRecoilAttribute);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.vertical_recoil", vertRecoil, false);

            double horizRecoil = baseRecoil
                    * FireModeHelper.getAttributeValue(player, CustomAttributes.HORIZONTAL_RECOIL)
                    * FireModeHelper.getAttributeValue(player, CustomAttributes.HIP_FIRE_HORIZONTAL_RECOIL)
                    * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getHorizontalRecoilAttribute)
                    * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getHipFireHorizontalRecoilAttribute);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.horizontal_recoil", horizRecoil, false);
        }

        // 精度
        if (!DiagramsModifierWrapper.wasProcessed("inaccuracy")) {
            double accuracy = FireModeHelper.getAttributeValue(player, CustomAttributes.HIP_FIRE_ACCURACY)
                    * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getHipFireAccuracyAttribute)
                    * FireModeHelper.getFireModeAccuracyMultiplier(player, fireMode, gunType);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.accuracy", accuracy, true);
        }

        // RPM
        if (!DiagramsModifierWrapper.wasProcessed("rpm")) {
            double rpm = FireModeHelper.getAttributeValue(player, CustomAttributes.RPM_MULTIPLIER)
                    * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getRpmMultiplierAttribute);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.rpm", rpm, true);
        }

        // ADS速度
        if (!DiagramsModifierWrapper.wasProcessed("ads")) {
            double adsSpeed = FireModeHelper.getAttributeValue(player, CustomAttributes.ADS_SPEED)
                    * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getAdsSpeedAttribute);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.ads_speed", adsSpeed, true);
        }

        // ヘッドショット
        if (!DiagramsModifierWrapper.wasProcessed("head_shot")) {
            double headshot = FireModeHelper.getAttributeValue(player, CustomAttributes.HEADSHOT_MULTIPLIER)
                    * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getHeadshotMultiplierAttribute);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.headshot", headshot, true);
        }

        // ノックバック
        if (!DiagramsModifierWrapper.wasProcessed("knockback")) {
            double knockback = FireModeHelper.getAttributeValue(player, CustomAttributes.KNOCKBACK_MULTIPLIER)
                    * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getKnockbackMultiplierAttribute);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.knockback", knockback, true);
        }

        // 貫通
        if (!DiagramsModifierWrapper.wasProcessed("pierce")) {
            double pierce = FireModeHelper.getAttributeValue(player, CustomAttributes.PIERCE_MULTIPLIER)
                    * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getPierceMultiplierAttribute);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.pierce", pierce, true);
        }

        // 移動速度
        if (!DiagramsModifierWrapper.wasProcessed("movement_speed")) {
            double moveSpeed = FireModeHelper.getAttributeValue(player, CustomAttributes.GUN_MOVEMENT_SPEED)
                    * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getGunMovementSpeedAttribute);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.movement", moveSpeed, true);
        }

        // === TaCZモディファイアでカバーされないカテゴリ ===

        // リロード速度
        double reloadSpeed = FireModeHelper.getAttributeValue(player, CustomAttributes.RELOAD_SPEED)
                * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getReloadSpeedAttribute);
        addMultiplierEntry(result, "gui.tacz_attributes.diagram.reload_speed", reloadSpeed, true);

        // コッキング速度
        double boltSpeed = FireModeHelper.getAttributeValue(player, CustomAttributes.BOLT_ACTION_SPEED)
                * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getBoltActionSpeedAttribute);
        addMultiplierEntry(result, "gui.tacz_attributes.diagram.bolt_speed", boltSpeed, true);

        // マガジン容量
        double magCap = FireModeHelper.getAttributeValue(player, CustomAttributes.MAGAZINE_CAPACITY)
                * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getMagazineCapacityAttribute);
        addMultiplierEntry(result, "gui.tacz_attributes.diagram.magazine", magCap, true);

        // 武器取り出し速度
        double drawSpeed = FireModeHelper.getAttributeValue(player, CustomAttributes.DRAW_SPEED)
                * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getDrawSpeedAttribute);
        addMultiplierEntry(result, "gui.tacz_attributes.diagram.draw_speed", drawSpeed, true);

        // 武器しまい速度
        double holsterSpeed = FireModeHelper.getAttributeValue(player, CustomAttributes.HOLSTER_SPEED)
                * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getHolsterSpeedAttribute);
        addMultiplierEntry(result, "gui.tacz_attributes.diagram.holster_speed", holsterSpeed, true);

        // 弾数（モード別）
        if (fireMode != null) {
            double bulletAmount = getBulletAmountMultiplier(player, fireMode, gunType);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.bullet_amount", bulletAmount, true);
        }

        // バースト速度（BURSTモードのみ）
        if (fireMode == FireMode.BURST) {
            double burstSpeed = FireModeHelper.getAttributeValue(player, CustomAttributes.BURST_SPEED)
                    * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getBurstSpeedAttribute);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.burst_speed", burstSpeed, true);
        }

        // 弾速
        double bulletVelocity = FireModeHelper.getAttributeValue(player, CustomAttributes.BULLET_VELOCITY)
                * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getBulletVelocityAttribute);
        addMultiplierEntry(result, "gui.tacz_attributes.diagram.bullet_velocity", bulletVelocity, true);

        // 射程
        double bulletLife = FireModeHelper.getAttributeValue(player, CustomAttributes.BULLET_LIFE)
                * FireModeHelper.getTypeAttributeValue(player, gunType, GunType::getBulletLifeAttribute);
        addMultiplierEntry(result, "gui.tacz_attributes.diagram.bullet_life", bulletLife, true);

        // === 確率系属性 ===

        // 弾薬非消費
        double ammoSave = FireModeHelper.getChanceAttributeValue(player, CustomAttributes.AMMO_SAVE_CHANCE)
                + FireModeHelper.getTypeChanceAttributeValue(player, gunType, GunType::getAmmoSaveChanceAttribute);
        addChanceEntry(result, "gui.tacz_attributes.diagram.ammo_save", Math.min(1.0, ammoSave));

        // キル時弾薬回復
        double ammoRecovery = FireModeHelper.getChanceAttributeValue(player, CustomAttributes.AMMO_RECOVERY_CHANCE)
                + FireModeHelper.getTypeChanceAttributeValue(player, gunType, GunType::getAmmoRecoveryChanceAttribute);
        addChanceEntry(result, "gui.tacz_attributes.diagram.ammo_recovery", Math.min(1.0, ammoRecovery));

        // リロード弾薬節約
        double reloadSave = FireModeHelper.getChanceAttributeValue(player, CustomAttributes.RELOAD_AMMO_SAVE_CHANCE)
                + FireModeHelper.getTypeChanceAttributeValue(player, gunType, GunType::getReloadAmmoSaveChanceAttribute);
        addChanceEntry(result, "gui.tacz_attributes.diagram.reload_save", Math.min(1.0, reloadSave));

        // 追加弾薬
        double bonusAmmo = FireModeHelper.getChanceAttributeValue(player, CustomAttributes.BONUS_AMMO_CHANCE)
                + FireModeHelper.getTypeChanceAttributeValue(player, gunType, GunType::getBonusAmmoChanceAttribute);
        addChanceEntry(result, "gui.tacz_attributes.diagram.bonus_ammo", Math.min(1.0, bonusAmmo));

        cachedDiagramsCount = result.size();
        return result;
    }

    // --- 倍率系 DiagramsData 生成 ---

    private static void addMultiplierEntry(List<DiagramsData> list, String titleKey,
                                           double combinedMultiplier, boolean positivelyBetter) {
        double delta = combinedMultiplier - 1.0;
        if (Math.abs(delta) < 0.001) return; // デフォルト値なら非表示

        double defaultPercent = 0.5;
        double modifierPercent = Math.max(-0.5, Math.min(0.5, delta * 0.5));

        int pctChange = (int) Math.round(delta * 100);
        String positivelyString = String.format("\u00d7%.2f(+%d%%)", combinedMultiplier, pctChange);
        String negativeString = String.format("\u00d7%.2f(%d%%)", combinedMultiplier, pctChange);
        String defaultString = String.format("\u00d7%.2f", combinedMultiplier);

        list.add(new DiagramsData(
                defaultPercent, modifierPercent, delta,
                titleKey, positivelyString, negativeString, defaultString,
                positivelyBetter
        ));
    }

    // --- 確率系 DiagramsData 生成 ---

    private static void addChanceEntry(List<DiagramsData> list, String titleKey, double chance) {
        if (chance < 0.001) return; // 0%なら非表示

        int pct = (int) Math.round(chance * 100);
        String text = String.format("%d%%", pct);

        list.add(new DiagramsData(
                0.0, chance, chance,
                titleKey, text, text, "0%",
                true
        ));
    }

    /** FireMode に基づく弾数倍率 */
    private static double getBulletAmountMultiplier(LocalPlayer player, FireMode mode,
                                                    @Nullable GunType gunType) {
        RegistryObject<Attribute> globalAttr = switch (mode) {
            case SEMI -> CustomAttributes.SEMI_BULLET_AMOUNT;
            case AUTO -> CustomAttributes.AUTO_BULLET_AMOUNT;
            case BURST -> CustomAttributes.BURST_BULLET_AMOUNT;
            default -> null;
        };
        RegistryObject<Attribute> typeAttr = (gunType == null) ? null : switch (mode) {
            case SEMI -> gunType.getSemiBulletAmountAttribute();
            case AUTO -> gunType.getAutoBulletAmountAttribute();
            case BURST -> gunType.getBurstBulletAmountAttribute();
            default -> null;
        };
        return FireModeHelper.getAttributeValue(player, globalAttr)
                * FireModeHelper.getAttributeValue(player, typeAttr);
    }
}
