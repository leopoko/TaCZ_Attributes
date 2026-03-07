package com.github.leopoko.tacz_attributes.mixin;

import com.github.leopoko.tacz_attributes.api.ISpeedModifiable;
import com.tacz.guns.api.client.animation.ObjectAnimationRunner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * ObjectAnimationRunner にアニメーション速度倍率を追加するクライアントMixin。
 * <p>
 * updateProgress() の alphaProgress パラメータをスケーリングすることで、
 * アニメーションの再生速度を変更する。
 * <p>
 * updateProgress は update() と updateSoundOnly() の両方から呼ばれるため、
 * アニメーション進行とサウンドタイミングの両方が正しくスケーリングされる。
 * <p>
 * update() 内の transitionProgressNs += alphaProgress は updateProgress のパラメータとは
 * 別の変数なので、遷移タイミングには影響しない（遷移は通常速度で動作する）。
 */
@Mixin(ObjectAnimationRunner.class)
public class ObjectAnimationRunnerMixin implements ISpeedModifiable {

    @Unique
    private float tacz_attributes$speedMultiplier = 1.0f;

    @Override
    public float tacz_attributes$getSpeedMultiplier() {
        return tacz_attributes$speedMultiplier;
    }

    @Override
    public void tacz_attributes$setSpeedMultiplier(float speed) {
        tacz_attributes$speedMultiplier = speed;
    }

    /**
     * updateProgress() の alphaProgress パラメータを速度倍率でスケーリングする。
     * これにより progressNs の増分が倍率分だけ加速/減速される。
     */
    @ModifyVariable(method = "updateProgress", at = @At("HEAD"), argsOnly = true, ordinal = 0, remap = false)
    private long tacz_attributes$scaleAlphaProgress(long alphaProgress) {
        if (tacz_attributes$speedMultiplier != 1.0f) {
            return (long) (alphaProgress * tacz_attributes$speedMultiplier);
        }
        return alphaProgress;
    }
}
