package com.github.leopoko.tacz_attributes.mixin;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.ShooterContext;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * GunData のマガジン容量を属性倍率で変更するMixin。
 * <p>
 * ShooterContext の ThreadLocal からプレイヤーと銃種を取得し、
 * マガジン容量属性の倍率を getAmmoAmount() / getExtendedMagAmmoAmount() の戻り値に適用する。
 * ShooterContext が設定されていない場合（データ読み込み時など）はオリジナル値をそのまま返す。
 */
@Mixin(GunData.class)
public class GunDataMixin {

    @Inject(method = "getAmmoAmount", at = @At("RETURN"), cancellable = true, remap = false)
    private void tacz_attributes$modifyAmmoAmount(CallbackInfoReturnable<Integer> cir) {
        double modifier = tacz_attributes$getMagazineCapacityModifier();
        if (modifier != 1.0) {
            cir.setReturnValue(Math.max(1, (int) Math.round(cir.getReturnValue() * modifier)));
        }
    }

    @Inject(method = "getExtendedMagAmmoAmount", at = @At("RETURN"), cancellable = true, remap = false)
    private void tacz_attributes$modifyExtendedMagAmmoAmount(CallbackInfoReturnable<int[]> cir) {
        int[] original = cir.getReturnValue();
        if (original == null) return;

        double modifier = tacz_attributes$getMagazineCapacityModifier();
        if (modifier != 1.0) {
            int[] modified = new int[original.length];
            for (int i = 0; i < original.length; i++) {
                modified[i] = Math.max(1, (int) Math.round(original[i] * modifier));
            }
            cir.setReturnValue(modified);
        }
    }

    /**
     * ShooterContext からマガジン容量倍率を計算する。
     * コンテキストがない場合は 1.0（変更なし）を返す。
     */
    @Unique
    private static double tacz_attributes$getMagazineCapacityModifier() {
        ShooterContext.ContextData ctx = ShooterContext.get();
        if (ctx == null) return 1.0;

        LivingEntity shooter = ctx.shooter();

        // 全体マガジン容量倍率
        double globalModifier = 1.0;
        if (shooter.getAttributes().hasAttribute(CustomAttributes.MAGAZINE_CAPACITY.get())) {
            globalModifier = shooter.getAttributeValue(CustomAttributes.MAGAZINE_CAPACITY.get());
        }

        // 銃種別マガジン容量倍率
        double typeModifier = 1.0;
        GunType gunType = ctx.gunType();
        if (gunType != null) {
            Attribute typeAttr = gunType.getMagazineCapacityAttribute().get();
            if (shooter.getAttributes().hasAttribute(typeAttr)) {
                typeModifier = shooter.getAttributeValue(typeAttr);
            }
        }

        return globalModifier * typeModifier;
    }
}
