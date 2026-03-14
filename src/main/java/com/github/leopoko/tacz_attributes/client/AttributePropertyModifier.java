package com.github.leopoko.tacz_attributes.client;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.FireModeHelper;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.api.item.IGun;
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
        IGun iGun = IGun.getIGunOrNull(gunStack);
        FireMode fireMode = (iGun != null) ? iGun.getFireMode(gunStack) : null;
        if (fireMode == FireMode.UNKNOWN) fireMode = null;

        // === TaCZモディファイアが空だった場合のフォールバック表示 ===
        // アタッチメントがない場合、TaCZモディファイアのデリゲートが空リストを返すため、
        // ラッパーが属性効果を反映できない。その場合はここで独自バーとして表示する。

        // ダメージ
        if (!DiagramsModifierWrapper.wasProcessed("damage")) {
            double damage = getAttr(player, CustomAttributes.GUN_DAMAGE)
                    * getTypeAttr(player, gunType, GunType::getDamageAttribute)
                    * getAttr(player, CustomAttributes.HIP_FIRE_DAMAGE)
                    * getTypeAttr(player, gunType, GunType::getHipFireDamageAttribute)
                    * getFireModeDamageMult(player, fireMode, gunType);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.damage", damage, true);
        }

        // 縦反動
        if (!DiagramsModifierWrapper.wasProcessed("recoil")) {
            double vertRecoil = getAttr(player, CustomAttributes.RECOIL)
                    * getAttr(player, CustomAttributes.VERTICAL_RECOIL)
                    * getAttr(player, CustomAttributes.HIP_FIRE_RECOIL)
                    * getAttr(player, CustomAttributes.HIP_FIRE_VERTICAL_RECOIL)
                    * getTypeAttr(player, gunType, GunType::getRecoilAttribute)
                    * getTypeAttr(player, gunType, GunType::getVerticalRecoilAttribute)
                    * getTypeAttr(player, gunType, GunType::getHipFireRecoilAttribute)
                    * getTypeAttr(player, gunType, GunType::getHipFireVerticalRecoilAttribute);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.vertical_recoil", vertRecoil, false);

            // 横反動
            double horizRecoil = getAttr(player, CustomAttributes.RECOIL)
                    * getAttr(player, CustomAttributes.HORIZONTAL_RECOIL)
                    * getAttr(player, CustomAttributes.HIP_FIRE_RECOIL)
                    * getAttr(player, CustomAttributes.HIP_FIRE_HORIZONTAL_RECOIL)
                    * getTypeAttr(player, gunType, GunType::getRecoilAttribute)
                    * getTypeAttr(player, gunType, GunType::getHorizontalRecoilAttribute)
                    * getTypeAttr(player, gunType, GunType::getHipFireRecoilAttribute)
                    * getTypeAttr(player, gunType, GunType::getHipFireHorizontalRecoilAttribute);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.horizontal_recoil", horizRecoil, false);
        }

        // 精度
        if (!DiagramsModifierWrapper.wasProcessed("inaccuracy")) {
            double accuracy = getAttr(player, CustomAttributes.HIP_FIRE_ACCURACY)
                    * getTypeAttr(player, gunType, GunType::getHipFireAccuracyAttribute)
                    * getFireModeAccuracyMult(player, fireMode, gunType);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.accuracy", accuracy, true);
        }

        // RPM
        if (!DiagramsModifierWrapper.wasProcessed("rpm")) {
            double rpm = getAttr(player, CustomAttributes.RPM_MULTIPLIER)
                    * getTypeAttr(player, gunType, GunType::getRpmMultiplierAttribute);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.rpm", rpm, true);
        }

        // ADS速度
        if (!DiagramsModifierWrapper.wasProcessed("ads")) {
            double adsSpeed = getAttr(player, CustomAttributes.ADS_SPEED)
                    * getTypeAttr(player, gunType, GunType::getAdsSpeedAttribute);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.ads_speed", adsSpeed, true);
        }

        // ヘッドショット
        if (!DiagramsModifierWrapper.wasProcessed("head_shot")) {
            double headshot = getAttr(player, CustomAttributes.HEADSHOT_MULTIPLIER)
                    * getTypeAttr(player, gunType, GunType::getHeadshotMultiplierAttribute);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.headshot", headshot, true);
        }

        // ノックバック
        if (!DiagramsModifierWrapper.wasProcessed("knockback")) {
            double knockback = getAttr(player, CustomAttributes.KNOCKBACK_MULTIPLIER)
                    * getTypeAttr(player, gunType, GunType::getKnockbackMultiplierAttribute);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.knockback", knockback, true);
        }

        // 貫通
        if (!DiagramsModifierWrapper.wasProcessed("pierce")) {
            double pierce = getAttr(player, CustomAttributes.PIERCE_MULTIPLIER)
                    * getTypeAttr(player, gunType, GunType::getPierceMultiplierAttribute);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.pierce", pierce, true);
        }

        // 移動速度
        if (!DiagramsModifierWrapper.wasProcessed("movement_speed")) {
            double moveSpeed = getAttr(player, CustomAttributes.GUN_MOVEMENT_SPEED)
                    * getTypeAttr(player, gunType, GunType::getGunMovementSpeedAttribute);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.movement", moveSpeed, true);
        }

        // === TaCZモディファイアでカバーされないカテゴリ ===

        // リロード速度
        double reloadSpeed = getAttr(player, CustomAttributes.RELOAD_SPEED)
                * getTypeAttr(player, gunType, GunType::getReloadSpeedAttribute);
        addMultiplierEntry(result, "gui.tacz_attributes.diagram.reload_speed", reloadSpeed, true);

        // コッキング速度
        double boltSpeed = getAttr(player, CustomAttributes.BOLT_ACTION_SPEED)
                * getTypeAttr(player, gunType, GunType::getBoltActionSpeedAttribute);
        addMultiplierEntry(result, "gui.tacz_attributes.diagram.bolt_speed", boltSpeed, true);

        // マガジン容量
        double magCap = getAttr(player, CustomAttributes.MAGAZINE_CAPACITY)
                * getTypeAttr(player, gunType, GunType::getMagazineCapacityAttribute);
        addMultiplierEntry(result, "gui.tacz_attributes.diagram.magazine", magCap, true);

        // 武器切替速度
        double drawSpeed = getAttr(player, CustomAttributes.DRAW_SPEED)
                * getTypeAttr(player, gunType, GunType::getDrawSpeedAttribute);
        addMultiplierEntry(result, "gui.tacz_attributes.diagram.draw_speed", drawSpeed, true);

        // 弾数（モード別）
        if (fireMode != null) {
            double bulletAmount = getBulletAmountMultiplier(player, fireMode, gunType);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.bullet_amount", bulletAmount, true);
        }

        // バースト速度（BURSTモードのみ）
        if (fireMode == FireMode.BURST) {
            double burstSpeed = getAttr(player, CustomAttributes.BURST_SPEED)
                    * getTypeAttr(player, gunType, GunType::getBurstSpeedAttribute);
            addMultiplierEntry(result, "gui.tacz_attributes.diagram.burst_speed", burstSpeed, true);
        }

        // === 確率系属性 ===

        // 弾薬非消費
        double ammoSave = getChanceAttr(player, CustomAttributes.AMMO_SAVE_CHANCE)
                + getTypeChanceAttr(player, gunType, GunType::getAmmoSaveChanceAttribute);
        addChanceEntry(result, "gui.tacz_attributes.diagram.ammo_save", Math.min(1.0, ammoSave));

        // キル時弾薬回復
        double ammoRecovery = getChanceAttr(player, CustomAttributes.AMMO_RECOVERY_CHANCE)
                + getTypeChanceAttr(player, gunType, GunType::getAmmoRecoveryChanceAttribute);
        addChanceEntry(result, "gui.tacz_attributes.diagram.ammo_recovery", Math.min(1.0, ammoRecovery));

        // リロード弾薬節約
        double reloadSave = getChanceAttr(player, CustomAttributes.RELOAD_AMMO_SAVE_CHANCE)
                + getTypeChanceAttr(player, gunType, GunType::getReloadAmmoSaveChanceAttribute);
        addChanceEntry(result, "gui.tacz_attributes.diagram.reload_save", Math.min(1.0, reloadSave));

        // 追加弾薬
        double bonusAmmo = getChanceAttr(player, CustomAttributes.BONUS_AMMO_CHANCE)
                + getTypeChanceAttr(player, gunType, GunType::getBonusAmmoChanceAttribute);
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

    // --- 属性値取得ヘルパー ---

    private static double getAttr(LocalPlayer player, RegistryObject<Attribute> attr) {
        return FireModeHelper.getAttributeValue(player, attr);
    }

    /** 確率系属性の値を取得（デフォルト0.0） */
    private static double getChanceAttr(LocalPlayer player, RegistryObject<Attribute> attr) {
        Attribute a = attr.get();
        if (player.getAttributes().hasAttribute(a)) {
            return player.getAttributeValue(a);
        }
        return 0.0;
    }

    /** 銃種別属性の値を取得（gunType==nullなら1.0） */
    private static double getTypeAttr(LocalPlayer player, @Nullable GunType gunType,
                                      java.util.function.Function<GunType, RegistryObject<Attribute>> getter) {
        if (gunType == null) return 1.0;
        return FireModeHelper.getAttributeValue(player, getter.apply(gunType));
    }

    /** 銃種別確率属性の値を取得（gunType==nullなら0.0） */
    private static double getTypeChanceAttr(LocalPlayer player, @Nullable GunType gunType,
                                            java.util.function.Function<GunType, RegistryObject<Attribute>> getter) {
        if (gunType == null) return 0.0;
        Attribute a = getter.apply(gunType).get();
        if (player.getAttributes().hasAttribute(a)) {
            return player.getAttributeValue(a);
        }
        return 0.0;
    }

    /** FireMode に基づくダメージ倍率 */
    private static double getFireModeDamageMult(LocalPlayer player, @Nullable FireMode mode,
                                                 @Nullable GunType gunType) {
        if (mode == null) return 1.0;
        return FireModeHelper.getAttributeValue(player, FireModeHelper.getGlobalDamageAttribute(mode))
                * FireModeHelper.getAttributeValue(player, FireModeHelper.getTypeDamageAttribute(gunType, mode));
    }

    /** FireMode に基づく精度倍率 */
    private static double getFireModeAccuracyMult(LocalPlayer player, @Nullable FireMode mode,
                                                   @Nullable GunType gunType) {
        if (mode == null) return 1.0;
        return FireModeHelper.getAttributeValue(player, FireModeHelper.getGlobalAccuracyAttribute(mode))
                * FireModeHelper.getAttributeValue(player, FireModeHelper.getTypeAccuracyAttribute(gunType, mode));
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
