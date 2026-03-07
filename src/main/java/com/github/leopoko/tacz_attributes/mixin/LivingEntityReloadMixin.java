package com.github.leopoko.tacz_attributes.mixin;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.tacz.guns.api.entity.ReloadState;
import com.tacz.guns.entity.shooter.LivingEntityReload;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * リロード速度属性を適用するMixin。
 * <p>
 * タイムスタンプスケーリング方式を使用:
 * tickReloadState() の実行前に reloadTimestamp を一時的にスケーリングし、
 * 経過時間がリロード速度倍率分だけ加速/減速して見えるようにする。
 * 実行後にタイムスタンプを元に戻すことで、次のtickでも正しく計算される。
 * <p>
 * この方式の利点:
 * - TaCZの内部リロードロジック（defaultTickReload、Luaスクリプト）をそのまま利用
 * - ReloadStateのcountdownが正しくスケーリングされ、ModSyncedEntityData経由でクライアントに同期
 * - クライアント側のアニメーションもサーバーからの同期データに基づいて動作するため、同期が取れる
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

    /**
     * tickReloadState() の実行前に、リロードタイムスタンプをスケーリングする。
     * reloadSpeedModifier が 2.0 なら、経過時間が2倍に見え、リロードが2倍速くなる。
     */
    @Inject(method = "tickReloadState", at = @At("HEAD"), remap = false)
    private void tacz_attributes$scaleTimestamp(CallbackInfoReturnable<ReloadState> cir) {
        tacz_attributes$originalTimestamp = -1;

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
     */
    @Inject(method = "tickReloadState", at = @At("RETURN"), remap = false)
    private void tacz_attributes$restoreTimestamp(CallbackInfoReturnable<ReloadState> cir) {
        if (tacz_attributes$originalTimestamp == -1) return;

        // リロードが完了していない場合のみ復元
        if (data.reloadTimestamp != -1) {
            data.reloadTimestamp = tacz_attributes$originalTimestamp;
        }
        tacz_attributes$originalTimestamp = -1;
    }

    @Unique
    private double tacz_attributes$getReloadSpeedModifier() {
        if (shooter != null && shooter.getAttributes().hasAttribute(CustomAttributes.RELOAD_SPEED.get())) {
            return shooter.getAttributeValue(CustomAttributes.RELOAD_SPEED.get());
        }
        return 1.0;
    }
}
