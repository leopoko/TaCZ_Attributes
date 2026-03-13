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
 * 各モディファイアの全エントリ（titleKey別）に対して正しい属性倍率を適用し、
 * バーの位置・数値テキスト・フォーマットを正確に更新する。
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
        if (player == null || original.isEmpty()) return original;

        GunType gunType = GunTypeResolver.resolveFromItem(gun);
        FireMode fireMode = FireModeHelper.getFireMode(gun);

        List<DiagramsData> modified = new ArrayList<>();
        for (DiagramsData d : original) {
            modified.add(transformEntry(d, player, gunType, fireMode));
        }
        return modified;
    }

    // ========================================================================================
    // エントリ変換ディスパッチ
    // ========================================================================================

    private DiagramsData transformEntry(DiagramsData d, LocalPlayer player,
                                         @Nullable GunType gunType, @Nullable FireMode fireMode) {
        return switch (modifierId) {
            case "damage" -> applySimpleMultiplier(d, getDamageMultiplier(player, gunType, fireMode));
            case "recoil" -> applySimpleMultiplier(d, getRecoilMultiplier(d, player, gunType));
            case "inaccuracy" -> transformInaccuracy(d, player, gunType, fireMode);
            case "rpm" -> applySimpleMultiplier(d, getRpmMultiplier(player, gunType));
            case "ads" -> applySimpleMultiplier(d, getAdsTimeMultiplier(player, gunType));
            case "head_shot" -> applySimpleMultiplier(d, getHeadshotMultiplier(player, gunType));
            case "knockback" -> transformKnockback(d, player, gunType);
            case "pierce" -> applySimpleMultiplier(d, getPierceMultiplier(player, gunType));
            case "movement_speed" -> applySimpleMultiplier(d, getMovementSpeedMultiplier(player, gunType));
            default -> d;
        };
    }

    // ========================================================================================
    // 汎用倍率適用（ダメージ、反動、RPM、ADS、HS、貫通、移動速度共通）
    // ========================================================================================

    /**
     * DiagramsData に単純な倍率を適用する。
     * 実値 = defaultPercent * normFactor + modifier で、全体に multiplier を掛ける。
     */
    private DiagramsData applySimpleMultiplier(DiagramsData d, double multiplier) {
        if (Math.abs(multiplier - 1.0) < 0.001) return d;

        double normFactor = getNormFactor(d);
        if (normFactor <= 0) return d;

        double baseValue = d.defaultPercent() * normFactor;
        double oldDelta = d.modifier().doubleValue();
        double combinedValue = baseValue + oldDelta;

        double newCombinedValue = combinedValue * multiplier;
        double newDelta = newCombinedValue - baseValue;
        double newModPct = newDelta / normFactor;

        return rebuildDiagramsData(d, newModPct, newDelta, newCombinedValue);
    }

    // ========================================================================================
    // Inaccuracy特殊処理（4エントリ: hipfire/sneak/lie/aim）
    // ========================================================================================

    private DiagramsData transformInaccuracy(DiagramsData d, LocalPlayer player,
                                              @Nullable GunType gunType, @Nullable FireMode fireMode) {
        String titleKey = d.titleKey();
        if (titleKey.contains("aim")) {
            return transformAimAccuracy(d, player, gunType, fireMode);
        } else {
            // hipfire/sneak/lie: inaccuracy値 → accuracy属性で割る(値が下がる=精度向上)
            double accuracyMult = getAttr(player, CustomAttributes.HIP_FIRE_ACCURACY)
                    * getTypeAttr(player, gunType, GunType::getHipFireAccuracyAttribute)
                    * getFireModeAccuracyMult(player, fireMode, gunType);
            // inaccuracy / accuracy = inaccuracy * (1/accuracy)
            return applySimpleMultiplier(d, 1.0 / accuracyMult);
        }
    }

    /**
     * AIM精度エントリの変換（特殊: 0-1範囲の%表記、positivelyBetter=true）
     * accuracy属性は inaccuracy を除算するため、accuracy が上がると精度%も上がる。
     */
    private DiagramsData transformAimAccuracy(DiagramsData d, LocalPlayer player,
                                               @Nullable GunType gunType, @Nullable FireMode fireMode) {
        double accMult = getAttr(player, CustomAttributes.ADS_ACCURACY)
                * getTypeAttr(player, gunType, GunType::getAdsAccuracyAttribute)
                * getFireModeAccuracyMult(player, fireMode, gunType);
        if (Math.abs(accMult - 1.0) < 0.001) return d;

        // AIM: defaultPercent = base accuracy (0-1), modifierPercent = attachment delta
        double baseAccuracy = d.defaultPercent();
        double oldDelta = d.modifierPercent();
        double combinedAccuracy = clamp01(baseAccuracy + oldDelta);

        // accuracy → inaccuracy → 属性適用 → accuracy に戻す
        double combinedInaccuracy = 1.0 - combinedAccuracy;
        double newInaccuracy = combinedInaccuracy / accMult;
        double newAccuracy = clamp01(1.0 - newInaccuracy);

        double newModPct = newAccuracy - baseAccuracy;

        // テキスト: %表記 (値 * 100)
        double totalPct = newAccuracy * 100.0;
        double deltaPct = newModPct * 100.0;

        String posText = String.format("%.1f%% \u00a7a(+%.1f%%)", totalPct, deltaPct);
        String negText = String.format("%.1f%% \u00a7c(%.1f%%)", totalPct, deltaPct);
        String defText = String.format("%.1f%%", totalPct);

        Number newModifier;
        if (d.modifier() instanceof Float) {
            newModifier = (float) newModPct;
        } else {
            newModifier = newModPct;
        }

        return new DiagramsData(
                baseAccuracy, newModPct, newModifier,
                d.titleKey(), posText, negText, defText,
                d.positivelyBetter()
        );
    }

    // ========================================================================================
    // Knockback特殊処理（加算 + 乗算）
    // ========================================================================================

    /**
     * ノックバック: (元の値 + knockback_base + type_base) × multiplier × type_multiplier
     */
    private DiagramsData transformKnockback(DiagramsData d, LocalPlayer player,
                                             @Nullable GunType gunType) {
        double additive = getChanceAttr(player, CustomAttributes.KNOCKBACK_BASE)
                + getTypeChanceAttr(player, gunType, GunType::getKnockbackBaseAttribute);
        double multiplier = getAttr(player, CustomAttributes.KNOCKBACK_MULTIPLIER)
                * getTypeAttr(player, gunType, GunType::getKnockbackMultiplierAttribute);

        if (Math.abs(additive) < 0.001 && Math.abs(multiplier - 1.0) < 0.001) return d;

        double normFactor = getNormFactor(d);
        if (normFactor <= 0) return d;

        double baseValue = d.defaultPercent() * normFactor;
        double oldDelta = d.modifier().doubleValue();
        double combinedValue = baseValue + oldDelta;

        double newCombinedValue = (combinedValue + additive) * multiplier;
        double newDelta = newCombinedValue - baseValue;
        double newModPct = newDelta / normFactor;

        return rebuildDiagramsData(d, newModPct, newDelta, newCombinedValue);
    }

    // ========================================================================================
    // 属性倍率の計算
    // ========================================================================================

    /** ダメージ倍率: gun_damage × type × hip_fire × type_hip_fire × fire_mode × type_fire_mode */
    private double getDamageMultiplier(LocalPlayer player, @Nullable GunType gunType,
                                        @Nullable FireMode fireMode) {
        return getAttr(player, CustomAttributes.GUN_DAMAGE)
                * getTypeAttr(player, gunType, GunType::getDamageAttribute)
                * getAttr(player, CustomAttributes.HIP_FIRE_DAMAGE)
                * getTypeAttr(player, gunType, GunType::getHipFireDamageAttribute)
                * getFireModeDamageMult(player, fireMode, gunType);
    }

    /** 反動倍率: recoil × (vertical/horizontal) × hip_fire × hip_fire_(v/h) × type系 */
    private double getRecoilMultiplier(DiagramsData d, LocalPlayer player, @Nullable GunType gunType) {
        double base = getAttr(player, CustomAttributes.RECOIL)
                * getAttr(player, CustomAttributes.HIP_FIRE_RECOIL)
                * getTypeAttr(player, gunType, GunType::getRecoilAttribute)
                * getTypeAttr(player, gunType, GunType::getHipFireRecoilAttribute);

        // titleKeyでpitch(縦)/yaw(横)を判別
        boolean isPitch = d.titleKey().contains("pitch");
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
        return base;
    }

    private double getRpmMultiplier(LocalPlayer player, @Nullable GunType gunType) {
        return getAttr(player, CustomAttributes.RPM_MULTIPLIER)
                * getTypeAttr(player, gunType, GunType::getRpmMultiplierAttribute);
    }

    /** ADS時間倍率: 速度が上がると時間が短くなる → 1/speed */
    private double getAdsTimeMultiplier(LocalPlayer player, @Nullable GunType gunType) {
        double speed = getAttr(player, CustomAttributes.ADS_SPEED)
                * getTypeAttr(player, gunType, GunType::getAdsSpeedAttribute);
        return 1.0 / speed;
    }

    private double getHeadshotMultiplier(LocalPlayer player, @Nullable GunType gunType) {
        return getAttr(player, CustomAttributes.HEADSHOT_MULTIPLIER)
                * getTypeAttr(player, gunType, GunType::getHeadshotMultiplierAttribute);
    }

    private double getPierceMultiplier(LocalPlayer player, @Nullable GunType gunType) {
        return getAttr(player, CustomAttributes.PIERCE_MULTIPLIER)
                * getTypeAttr(player, gunType, GunType::getPierceMultiplierAttribute);
    }

    private double getMovementSpeedMultiplier(LocalPlayer player, @Nullable GunType gunType) {
        return getAttr(player, CustomAttributes.GUN_MOVEMENT_SPEED)
                * getTypeAttr(player, gunType, GunType::getGunMovementSpeedAttribute);
    }

    // ========================================================================================
    // DiagramsData再構築
    // ========================================================================================

    /**
     * 倍率適用後の値でDiagramsDataを再構築する。
     * テキストフォーマットはモディファイアIDとtitleKeyに基づいて決定。
     */
    private DiagramsData rebuildDiagramsData(DiagramsData d, double newModPct,
                                              double newDelta, double newTotal) {
        String titleKey = d.titleKey();
        String posText, negText, defText;
        Number newModifier;

        switch (modifierId) {
            case "damage" -> {
                posText = String.format("%.2f \u00a7a(+%.2f)", newTotal, newDelta);
                negText = String.format("%.2f \u00a7c(%.2f)", newTotal, newDelta);
                defText = String.format("%.2f", newTotal);
                newModifier = asOriginalType(d.modifier(), newDelta);
            }
            case "recoil" -> {
                // 反動: 増加=悪い(赤), 減少=良い(緑)
                posText = String.format("%.2f \u00a7c(+%.2f)", newTotal, newDelta);
                negText = String.format("%.2f \u00a7a(%.2f)", newTotal, newDelta);
                defText = String.format("%.2f", newTotal);
                newModifier = asOriginalType(d.modifier(), newDelta);
            }
            case "inaccuracy" -> {
                // hipfire/sneak/lie inaccuracy: 増加=悪い(赤), 減少=良い(緑)
                posText = String.format("%.2f \u00a7c(+%.2f)", newTotal, newDelta);
                negText = String.format("%.2f \u00a7a(%.2f)", newTotal, newDelta);
                defText = String.format("%.2f", newTotal);
                newModifier = asOriginalType(d.modifier(), newDelta);
            }
            case "rpm" -> {
                int totalInt = (int) Math.round(newTotal);
                int deltaInt = (int) Math.round(newDelta);
                posText = String.format("%drpm \u00a7a(+%d)", totalInt, deltaInt);
                negText = String.format("%drpm \u00a7c(%d)", totalInt, deltaInt);
                defText = String.format("%drpm", totalInt);
                newModifier = deltaInt;
            }
            case "ads" -> {
                // ADS時間: 増加=悪い(赤), 減少=良い(緑)
                posText = String.format("%.2fs \u00a7c(+%.2f)", newTotal, newDelta);
                negText = String.format("%.2fs \u00a7a(%.2f)", newTotal, newDelta);
                defText = String.format("%.2fs", newTotal);
                newModifier = asOriginalType(d.modifier(), newDelta);
            }
            case "head_shot" -> {
                posText = String.format("x%.1f \u00a7a(+%.1f)", newTotal, newDelta);
                negText = String.format("x%.1f \u00a7c(%.1f)", newTotal, newDelta);
                defText = String.format("x%.1f", newTotal);
                newModifier = asOriginalType(d.modifier(), newDelta);
            }
            case "knockback" -> {
                posText = String.format("%.2f \u00a7a(+%.2f)", newTotal, newDelta);
                negText = String.format("%.2f \u00a7c(%.2f)", newTotal, newDelta);
                defText = String.format("%.2f", newTotal);
                newModifier = asOriginalType(d.modifier(), newDelta);
            }
            case "pierce" -> {
                int totalInt = (int) Math.round(newTotal);
                int deltaInt = (int) Math.round(newDelta);
                posText = String.format("%d \u00a7a(+%d)", totalInt, deltaInt);
                negText = String.format("%d \u00a7c(%d)", totalInt, deltaInt);
                defText = String.format("%d", totalInt);
                newModifier = deltaInt;
            }
            case "movement_speed" -> {
                // 移動速度: 増加=良い(緑), 減少=悪い(赤) — フォーマットは汎用
                posText = String.format("%.2f \u00a7a(+%.2f)", newTotal, newDelta);
                negText = String.format("%.2f \u00a7c(%.2f)", newTotal, newDelta);
                defText = String.format("%.2f", newTotal);
                newModifier = asOriginalType(d.modifier(), newDelta);
            }
            default -> {
                posText = String.format("%.2f \u00a7a(+%.2f)", newTotal, newDelta);
                negText = String.format("%.2f \u00a7c(%.2f)", newTotal, newDelta);
                defText = String.format("%.2f", newTotal);
                newModifier = asOriginalType(d.modifier(), newDelta);
            }
        }

        return new DiagramsData(
                d.defaultPercent(), newModPct, newModifier,
                titleKey, posText, negText, defText,
                d.positivelyBetter()
        );
    }

    // ========================================================================================
    // 正規化係数（barScale の逆数）
    // ========================================================================================

    /**
     * DiagramsData から正規化係数を取得する。
     * 可能であればデータから導出し、不可能な場合はモディファイアIDとtitleKeyから既知値を返す。
     */
    private double getNormFactor(DiagramsData d) {
        // データから導出を試みる
        double modPct = d.modifierPercent();
        double modVal = d.modifier().doubleValue();
        if (Math.abs(modPct) > 0.0001 && Math.abs(modVal) > 0.0001) {
            return modVal / modPct;
        }
        // 既知値にフォールバック
        return switch (modifierId) {
            case "damage" -> 50.0;
            case "recoil" -> 5.0;
            case "rpm" -> 1200.0;
            case "ads" -> 0.5;
            case "head_shot" -> 5.0;
            case "pierce" -> 5.0;
            case "knockback" -> 1.0;
            case "inaccuracy" -> {
                String tk = d.titleKey();
                if (tk.contains("hipfire")) yield 10.0;
                else if (tk.contains("aim")) yield 1.0;
                else yield 5.0; // sneak, lie
            }
            case "movement_speed" -> 1.0;
            default -> 1.0;
        };
    }

    // ========================================================================================
    // ヘルパーメソッド
    // ========================================================================================

    /** 元のNumber型に合わせてdelta値を返す */
    private static Number asOriginalType(Number original, double value) {
        if (original instanceof Float) return (float) value;
        if (original instanceof Integer) return (int) Math.round(value);
        return value;
    }

    private static double clamp01(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }

    private static double getAttr(LocalPlayer player, RegistryObject<Attribute> attr) {
        return FireModeHelper.getAttributeValue(player, attr);
    }

    private static double getTypeAttr(LocalPlayer player, @Nullable GunType gunType,
                                       Function<GunType, RegistryObject<Attribute>> getter) {
        if (gunType == null) return 1.0;
        return FireModeHelper.getAttributeValue(player, getter.apply(gunType));
    }

    /** 確率/加算系属性の値を取得（デフォルト0.0） */
    private static double getChanceAttr(LocalPlayer player, RegistryObject<Attribute> attr) {
        Attribute a = attr.get();
        if (player.getAttributes().hasAttribute(a)) {
            return player.getAttributeValue(a);
        }
        return 0.0;
    }

    /** 銃種別の確率/加算系属性（デフォルト0.0） */
    private static double getTypeChanceAttr(LocalPlayer player, @Nullable GunType gunType,
                                             Function<GunType, RegistryObject<Attribute>> getter) {
        if (gunType == null) return 0.0;
        Attribute a = getter.apply(gunType).get();
        if (player.getAttributes().hasAttribute(a)) {
            return player.getAttributeValue(a);
        }
        return 0.0;
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
