package com.github.leopoko.tacz_attributes.mixin;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.github.leopoko.tacz_attributes.util.ReloadFinishingContext;
import com.github.leopoko.tacz_attributes.util.ShooterContext;
import com.tacz.guns.api.entity.ReloadState;
import com.tacz.guns.entity.shooter.LivingEntityReload;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * リロード速度属性を適用するMixin。
 * <p>
 * タイムスタンプスケーリング方式を使用:
 * tickReloadState() の実行前に reloadTimestamp を一時的にスケーリングし、
 * 経過時間がリロード速度倍率分だけ加速/減速して見えるようにする。
 * 実行後にタイムスタンプを元に戻すことで、次のtickでも正しく計算される。
 * <p>
 * また、マガジン容量属性のために ShooterContext を設定する。
 * reload() と tickReloadState() の両方で設定し、GunData.getAmmoAmount() が
 * 呼ばれる際にプレイヤー固有の倍率が適用されるようにする。
 */
@Mixin(LivingEntityReload.class)
public abstract class LivingEntityReloadMixin {

    @Shadow(remap = false)
    private LivingEntity shooter;

    @Shadow(remap = false)
    private ShooterDataHolder data;

    /**
     * スケーリング前のオリジナルのタイムスタンプを保持。
     * -1 はスケーリングが行われていないことを示す。
     */
    @Unique
    private long tacz_attributes$originalTimestamp = -1;

    // === tickReloadState() ===

    /**
     * tickReloadState() の実行前に、リロードタイムスタンプをスケーリングする。
     * reloadSpeedModifier が 2.0 なら、経過時間が2倍に見え、リロードが2倍速くなる。
     * また、ShooterContext を設定してマガジン容量属性が効くようにする。
     */
    @Inject(method = "tickReloadState", at = @At("HEAD"), remap = false)
    private void tacz_attributes$scaleTimestamp(CallbackInfoReturnable<ReloadState> cir) {
        tacz_attributes$originalTimestamp = -1;

        // マガジン容量用の ShooterContext を設定
        if (shooter != null) {
            ShooterContext.set(shooter);
        }

        // リロード処理中フラグを設定（リロード時弾薬非消費・追加弾薬用）
        ReloadFinishingContext.set();

        if (data.reloadTimestamp == -1) return;

        double reloadSpeedModifier = tacz_attributes$getReloadSpeedModifier();
        if (reloadSpeedModifier == 1.0) return;

        tacz_attributes$originalTimestamp = data.reloadTimestamp;

        long now = System.currentTimeMillis();
        long originalElapsed = now - data.reloadTimestamp;
        long scaledElapsed = (long) (originalElapsed * reloadSpeedModifier);
        data.reloadTimestamp = now - scaledElapsed;
    }

    /**
     * tickReloadState() の実行後に、オリジナルのタイムスタンプを復元する。
     * リロードが完了した場合（reloadTimestamp が -1 に設定された場合）は復元しない。
     * また、ShooterContext をクリアする。
     */
    @Inject(method = "tickReloadState", at = @At("RETURN"), remap = false)
    private void tacz_attributes$restoreTimestamp(CallbackInfoReturnable<ReloadState> cir) {
        ShooterContext.clear();
        ReloadFinishingContext.clear();

        if (tacz_attributes$originalTimestamp == -1) return;

        // リロードが完了していない場合のみ復元
        if (data.reloadTimestamp != -1) {
            data.reloadTimestamp = tacz_attributes$originalTimestamp;
        }
        tacz_attributes$originalTimestamp = -1;
    }

    // === reload() ===

    /**
     * reload() の実行前に ShooterContext を設定する。
     * リロード開始時にも getAmmoAmount() が呼ばれる可能性があるため。
     */
    @Inject(method = "reload", at = @At("HEAD"), remap = false)
    private void tacz_attributes$setContextOnReload(CallbackInfo ci) {
        if (shooter != null) {
            ShooterContext.set(shooter);
        }
    }

    /**
     * reload() の実行後に ShooterContext をクリアする。
     */
    @Inject(method = "reload", at = @At("RETURN"), remap = false)
    private void tacz_attributes$clearContextOnReload(CallbackInfo ci) {
        ShooterContext.clear();
    }

    @Unique
    private double tacz_attributes$getReloadSpeedModifier() {
        if (shooter == null) return 1.0;

        // 全体リロード速度倍率
        double globalSpeed = 1.0;
        if (shooter.getAttributes().hasAttribute(CustomAttributes.RELOAD_SPEED.get())) {
            globalSpeed = shooter.getAttributeValue(CustomAttributes.RELOAD_SPEED.get());
        }

        // 銃種別リロード速度倍率
        double typeSpeed = 1.0;
        ItemStack mainHand = shooter.getMainHandItem();
        GunType gunType = GunTypeResolver.resolveFromItem(mainHand);
        if (gunType != null) {
            Attribute typeAttr = gunType.getReloadSpeedAttribute().get();
            if (shooter.getAttributes().hasAttribute(typeAttr)) {
                typeSpeed = shooter.getAttributeValue(typeAttr);
            }
        }

        return globalSpeed * typeSpeed;
    }
}
