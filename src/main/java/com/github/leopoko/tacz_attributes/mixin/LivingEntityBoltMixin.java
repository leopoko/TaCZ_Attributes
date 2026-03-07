package com.github.leopoko.tacz_attributes.mixin;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.github.leopoko.tacz_attributes.util.ShooterContext;
import com.tacz.guns.entity.shooter.LivingEntityBolt;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * コッキング（ボルトアクション）速度属性を適用するMixin。
 * <p>
 * リロード速度と同じタイムスタンプスケーリング方式を使用:
 * tickBolt() の実行前に boltTimestamp を一時的にスケーリングし、
 * 経過時間がボルト速度倍率分だけ加速/減速して見えるようにする。
 * 実行後にタイムスタンプを元に戻すことで、次のtickでも正しく計算される。
 */
@Mixin(LivingEntityBolt.class)
public abstract class LivingEntityBoltMixin {

    @Shadow(remap = false)
    private LivingEntity shooter;

    @Shadow(remap = false)
    private ShooterDataHolder data;

    /**
     * スケーリング前のオリジナルの boltTimestamp を保持。
     * -1 はスケーリングが行われていないことを示す。
     */
    @Unique
    private long tacz_attributes$originalBoltTimestamp = -1;

    /**
     * tickBolt() の実行前に、ボルトタイムスタンプをスケーリングする。
     * また、マガジン容量のために ShooterContext を設定する。
     */
    @Inject(method = "tickBolt", at = @At("HEAD"), remap = false)
    private void tacz_attributes$scaleBoltTimestamp(CallbackInfo ci) {
        tacz_attributes$originalBoltTimestamp = -1;

        if (shooter != null) {
            ShooterContext.set(shooter);
        }

        if (!data.isBolting) return;

        double boltSpeedModifier = tacz_attributes$getBoltSpeedModifier();
        if (boltSpeedModifier == 1.0) return;

        tacz_attributes$originalBoltTimestamp = data.boltTimestamp;

        long now = System.currentTimeMillis();
        long originalElapsed = now - data.boltTimestamp;
        long scaledElapsed = (long) (originalElapsed * boltSpeedModifier);
        data.boltTimestamp = now - scaledElapsed;
    }

    /**
     * tickBolt() の実行後に、オリジナルのタイムスタンプを復元する。
     * また、ShooterContext をクリアする。
     */
    @Inject(method = "tickBolt", at = @At("RETURN"), remap = false)
    private void tacz_attributes$restoreBoltTimestamp(CallbackInfo ci) {
        ShooterContext.clear();

        if (tacz_attributes$originalBoltTimestamp == -1) return;

        // ボルトが完了した場合（isBolting が false）でもタイムスタンプを復元する。
        // isBolting が false になった後は boltTimestamp は使用されないため、復元しても問題ない。
        data.boltTimestamp = tacz_attributes$originalBoltTimestamp;
        tacz_attributes$originalBoltTimestamp = -1;
    }

    @Unique
    private double tacz_attributes$getBoltSpeedModifier() {
        if (shooter == null) return 1.0;

        // 全体コッキング速度倍率
        double globalSpeed = 1.0;
        if (shooter.getAttributes().hasAttribute(CustomAttributes.BOLT_ACTION_SPEED.get())) {
            globalSpeed = shooter.getAttributeValue(CustomAttributes.BOLT_ACTION_SPEED.get());
        }

        // 銃種別コッキング速度倍率
        double typeSpeed = 1.0;
        ItemStack mainHand = shooter.getMainHandItem();
        GunType gunType = GunTypeResolver.resolveFromItem(mainHand);
        if (gunType != null) {
            Attribute typeAttr = gunType.getBoltActionSpeedAttribute().get();
            if (shooter.getAttributes().hasAttribute(typeAttr)) {
                typeSpeed = shooter.getAttributeValue(typeAttr);
            }
        }

        return globalSpeed * typeSpeed;
    }
}
