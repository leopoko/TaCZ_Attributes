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
import java.util.function.Function;

/**
 * TaCZの既存IAttachmentModifierをラップし、getPropertyDiagramsData()の結果に
 * プレイヤー属性の効果を反映するデコレーター。
 * <p>
 * 既存のダメージ・反動・RPM等のバーに属性倍率を適用し、
 * バーの位置と数値テキストを正しく更新する。
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DiagramsModifierWrapper implements IAttachmentModifier {

    private final IAttachmentModifier delegate;
    private final String modifierId;

    public DiagramsModifierWrapper(IAttachmentModifier delegate) {
        this.delegate = delegate;
        this.modifierId = delegate.getId();
    }

    @Override
    public String getId() { return modifierId; }

    @Override
    public JsonProperty readJson(String json) { return delegate.readJson(json); }

    @Override
    public CacheValue initCache(ItemStack gun, GunData data) { return delegate.initCache(gun, data); }

    @Override
    public void eval(List list, CacheValue cache) { delegate.eval(list, cache); }

    @Override
    public int getDiagramsDataSize() { return delegate.getDiagramsDataSize(); }

    @Override
    public List<DiagramsData> getPropertyDiagramsData(
            ItemStack gun, GunData data, AttachmentCacheProperty cache) {
        List<DiagramsData> original = delegate.getPropertyDiagramsData(gun, data, cache);

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return original;

        GunType gunType = GunTypeResolver.resolveFromItem(gun);
        FireMode fireMode = FireModeHelper.getFireMode(gun);

        List<DiagramsData> modified = new ArrayList<>();
        for (DiagramsData d : original) {
            double attrMult = getMultiplier(player, gunType, fireMode, d);
            if (Math.abs(attrMult - 1.0) < 0.001) {
                modified.add(d);
            } else {
                modified.add(applyMultiplier(d, attrMult));
            }
        }
        return modified;
    }

    // --- 属性倍率の計算 ---

    private double getMultiplier(LocalPlayer player, @Nullable GunType gunType,
                                  @Nullable FireMode fireMode, DiagramsData entry) {
        return switch (modifierId) {
            case "damage" -> getAttr(player, CustomAttributes.GUN_DAMAGE)
                    * getTypeAttr(player, gunType, GunType::getDamageAttribute)
                    * getAttr(player, CustomAttributes.HIP_FIRE_DAMAGE)
                    * getTypeAttr(player, gunType, GunType::getHipFireDamageAttribute)
                    * getFireModeDamageMult(player, fireMode, gunType);

            case "recoil_modifier" -> {
                // pitch/yaw別に縦反動/横反動を適用
                double base = getAttr(player, CustomAttributes.RECOIL)
                        * getAttr(player, CustomAttributes.HIP_FIRE_RECOIL)
                        * getTypeAttr(player, gunType, GunType::getRecoilAttribute)
                        * getTypeAttr(player, gunType, GunType::getHipFireRecoilAttribute);

                boolean isPitch = entry.titleKey().contains("pitch");
                if (isPitch) {
                    base *= getAttr(player, CustomAttributes.VERTICAL_RECOIL)
                            * getAttr(player, CustomAttributes.HIP_FIRE_VERTICAL_RECOIL)
                            * getTypeAttr(player, gunType, GunType::getVerticalRecoilAttribute)
                            * getTypeAttr(player, gunType, GunType::getHipFireVerticalRecoilAttribute);
                } else {
                    base *= getAttr(player, CustomAttributes.HORIZONTAL_RECOIL)
                            * getAttr(player, CustomAttributes.HIP_FIRE_HORIZONTAL_RECOIL)
                            * getTypeAttr(player, gunType, GunType::getHorizontalRecoilAttribute)
                            * getTypeAttr(player, gunType, GunType::getHipFireHorizontalRecoilAttribute);
                }
                yield base;
            }

            case "inaccuracy_addend" -> {
                // 腰撃ちinaccuracy: accuracy↑ = inaccuracy↓ → 1/accuracy
                double acc = getAttr(player, CustomAttributes.HIP_FIRE_ACCURACY)
                        * getTypeAttr(player, gunType, GunType::getHipFireAccuracyAttribute)
                        * getFireModeAccuracyMult(player, fireMode, gunType);
                yield 1.0 / acc;
            }

            case "inaccuracy" -> {
                // ADS inaccuracy
                double acc = getAttr(player, CustomAttributes.ADS_ACCURACY)
                        * getTypeAttr(player, gunType, GunType::getAdsAccuracyAttribute)
                        * getFireModeAccuracyMult(player, fireMode, gunType);
                yield 1.0 / acc;
            }

            case "rpm" -> getAttr(player, CustomAttributes.RPM_MULTIPLIER)
                    * getTypeAttr(player, gunType, GunType::getRpmMultiplierAttribute);

            case "ads_addend" -> {
                // ADS速度↑ = ADS時間↓ → 1/speed
                double speed = getAttr(player, CustomAttributes.ADS_SPEED)
                        * getTypeAttr(player, gunType, GunType::getAdsSpeedAttribute);
                yield 1.0 / speed;
            }

            case "head_shot" -> getAttr(player, CustomAttributes.HEADSHOT_MULTIPLIER)
                    * getTypeAttr(player, gunType, GunType::getHeadshotMultiplierAttribute);

            case "knockback" -> getAttr(player, CustomAttributes.KNOCKBACK_MULTIPLIER)
                    * getTypeAttr(player, gunType, GunType::getKnockbackMultiplierAttribute);

            case "pierce" -> getAttr(player, CustomAttributes.PIERCE_MULTIPLIER)
                    * getTypeAttr(player, gunType, GunType::getPierceMultiplierAttribute);

            case "movement_speed" -> getAttr(player, CustomAttributes.GUN_MOVEMENT_SPEED)
                    * getTypeAttr(player, gunType, GunType::getGunMovementSpeedAttribute);

            default -> 1.0;
        };
    }

    // --- DiagramsData変換 ---

    private DiagramsData applyMultiplier(DiagramsData d, double attrMult) {
        double defaultPct = d.defaultPercent();
        double modPct = d.modifierPercent();
        double modVal = d.modifier().doubleValue();

        // 新しいmodifierPercent: (base + attachment) * attr - base = 属性+アタッチメントの合成効果
        double newModPct = (defaultPct + modPct) * attrMult - defaultPct;

        // 正規化係数を導出して実数値を計算
        double normFactor;
        if (Math.abs(modPct) > 0.0001) {
            normFactor = modVal / modPct;
        } else {
            normFactor = getKnownNormFactor();
            if (normFactor <= 0) return d; // 正規化係数不明、変更なし
        }

        double baseValue = defaultPct * normFactor;
        double totalNew = (baseValue + modVal) * attrMult;
        double newDelta = totalNew - baseValue;

        // テキスト再構築
        TextConfig config = getTextConfig();
        String posText, negText, defText;

        if (config != null) {
            if (config.useInt) {
                int totalInt = (int) Math.round(totalNew);
                int deltaInt = (int) Math.round(newDelta);
                posText = String.format(config.positiveFormat, totalInt, deltaInt);
                negText = String.format(config.negativeFormat, totalInt, deltaInt);
                defText = String.format(config.defaultFormat, totalInt);
            } else {
                posText = String.format(config.positiveFormat, totalNew, newDelta);
                negText = String.format(config.negativeFormat, totalNew, newDelta);
                defText = String.format(config.defaultFormat, totalNew);
            }
        } else {
            // フォーマット不明: 汎用フォーマット (%.2f)
            posText = String.format("%.2f \u00a7a(+%.2f)", totalNew, newDelta);
            negText = String.format("%.2f \u00a7c(%.2f)", totalNew, newDelta);
            defText = String.format("%.2f", totalNew);
        }

        // modifier型をオリジナルに合わせる
        Number newModifier;
        if (config != null && config.useInt) {
            newModifier = (int) Math.round(newDelta);
        } else if (d.modifier() instanceof Float) {
            newModifier = (float) newDelta;
        } else {
            newModifier = newDelta;
        }

        return new DiagramsData(
                defaultPct, newModPct, newModifier,
                d.titleKey(), posText, negText, defText,
                d.positivelyBetter()
        );
    }

    // --- 設定定数 ---

    private double getKnownNormFactor() {
        return switch (modifierId) {
            case "damage" -> 50.0;
            case "recoil_modifier" -> 5.0;
            case "rpm" -> 1200.0;
            case "inaccuracy_addend", "inaccuracy" -> 10.0;
            default -> 0.0;
        };
    }

    @Nullable
    private TextConfig getTextConfig() {
        return switch (modifierId) {
            case "damage" ->
                    new TextConfig("%.2f \u00a7a(+%.2f)", "%.2f \u00a7c(%.2f)", "%.2f", false);
            case "recoil_modifier" ->
                    new TextConfig("%.2f \u00a7c(+%.2f)", "%.2f \u00a7a(%.2f)", "%.2f", false);
            case "rpm" ->
                    new TextConfig("%drpm \u00a7a(+%d)", "%drpm \u00a7c(%d)", "%drpm", true);
            default -> null; // 汎用フォーマットを使用
        };
    }

    private record TextConfig(String positiveFormat, String negativeFormat,
                               String defaultFormat, boolean useInt) {}

    // --- 属性値取得ヘルパー ---

    private static double getAttr(LocalPlayer player, RegistryObject<Attribute> attr) {
        return FireModeHelper.getAttributeValue(player, attr);
    }

    private static double getTypeAttr(LocalPlayer player, @Nullable GunType gunType,
                                       Function<GunType, RegistryObject<Attribute>> getter) {
        if (gunType == null) return 1.0;
        return FireModeHelper.getAttributeValue(player, getter.apply(gunType));
    }

    private double getFireModeDamageMult(LocalPlayer player,
                                          @Nullable FireMode mode, @Nullable GunType gunType) {
        if (mode == null) return 1.0;
        return FireModeHelper.getAttributeValue(player, FireModeHelper.getGlobalDamageAttribute(mode))
                * FireModeHelper.getAttributeValue(player, FireModeHelper.getTypeDamageAttribute(gunType, mode));
    }

    private double getFireModeAccuracyMult(LocalPlayer player,
                                            @Nullable FireMode mode, @Nullable GunType gunType) {
        if (mode == null) return 1.0;
        return FireModeHelper.getAttributeValue(player, FireModeHelper.getGlobalAccuracyAttribute(mode))
                * FireModeHelper.getAttributeValue(player, FireModeHelper.getTypeAccuracyAttribute(gunType, mode));
    }
}
