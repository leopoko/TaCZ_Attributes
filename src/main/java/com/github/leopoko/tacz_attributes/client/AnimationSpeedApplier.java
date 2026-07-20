package com.github.leopoko.tacz_attributes.client;

import com.github.leopoko.tacz_attributes.api.ISpeedModifiable;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.client.animation.AnimationController;
import com.tacz.guns.api.client.animation.DiscreteTrackArray;
import com.tacz.guns.api.client.animation.ObjectAnimation;
import com.tacz.guns.api.client.animation.ObjectAnimationRunner;
import com.tacz.guns.api.client.animation.statemachine.AnimationStateContext;
import com.tacz.guns.api.client.animation.statemachine.AnimationStateMachine;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.resource.GunDisplayInstance;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

/**
 * アニメーションランナーに速度倍率を適用するユーティリティ。
 * <p>
 * 以前はデフォルトステートマシンの MAIN_TRACK 番号（4）を直接指定していたが、
 * TaCZ 1.1.8 で default_state_machine.lua に PRE_PARALLEL_TRACK_1〜5 が
 * 追加され、MAIN_TRACK が 4 → 9 にずれた。さらに minigun のように独自の
 * トラック構成を持つ銃（MAIN_TRACK = 4 のまま）も存在するため、
 * トラック番号は銃ごとに異なり固定値では特定できない。
 * <p>
 * そのため番号ではなく「再生中のアニメーション名」でトラックを特定する。
 * この方式ならサードパーティの銃MODが独自のステートマシンを定義していても追従できる。
 */
public final class AnimationSpeedApplier {

    /** リロード系アニメーション名の接頭辞（reload_tactical, reload_empty, reload_loop など） */
    public static final String[] RELOAD_ANIMATIONS = {"reload"};

    /** ボルト系アニメーション名の接頭辞（bolt, bolt_scope, bolt_hammer） */
    public static final String[] BOLT_ANIMATIONS = {"bolt"};

    /**
     * 取り出し系アニメーション名の接頭辞（draw, draw_semi, draw_semi_caught）。
     * put_away は GunItemRendererWrapperMixin が getPutAwayTime() をスケーリングして
     * 対応済みのため、ここに含めると二重に加速されてしまう。含めないこと。
     */
    public static final String[] DRAW_ANIMATIONS = {"draw"};

    private AnimationSpeedApplier() {
    }

    /**
     * 名前が prefixes のいずれかで始まるアニメーションを再生中のランナーに速度倍率を適用する。
     */
    public static void apply(LocalPlayer player, float speed, String... prefixes) {
        forEachRunner(player, runner -> {
            if (matches(runner, prefixes)) {
                setSpeed(runner, speed);
            }
        });
    }

    /**
     * 全ランナーの速度倍率を 1.0 に戻す。
     * <p>
     * ランナーは GunDisplayInstance に紐づいて再利用されるため、
     * リロード等の終了時にリセットしないと倍率が残り続ける。
     */
    public static void reset(LocalPlayer player) {
        forEachRunner(player, runner -> setSpeed(runner, 1.0f));
    }

    /**
     * メインハンドの銃の全トラックのランナー（遷移先を含む）に action を適用する。
     */
    private static void forEachRunner(LocalPlayer player, Consumer<ObjectAnimationRunner> action) {
        ItemStack mainHand = player.getMainHandItem();
        if (IGun.getIGunOrNull(mainHand) == null) return;

        GunDisplayInstance display = TimelessAPI.getGunDisplay(mainHand).orElse(null);
        if (display == null) return;

        AnimationStateMachine<?> stateMachine = display.getAnimationStateMachine();
        if (stateMachine == null || !stateMachine.isInitialized()) return;

        AnimationController controller = stateMachine.getAnimationController();
        if (controller == null) return;

        AnimationStateContext context = stateMachine.getContext();
        if (context == null) return;

        DiscreteTrackArray trackArray = context.getTrackArray();
        if (trackArray == null) return;

        int trackLineSize = trackArray.getTrackLineSize();
        for (int line = 0; line < trackLineSize; line++) {
            List<Integer> tracks = trackArray.getByIndex(line);
            if (tracks == null) continue;

            for (int pointer : tracks) {
                ObjectAnimationRunner runner = controller.getAnimation(pointer);
                if (runner == null) continue;

                action.accept(runner);

                // 遷移中の場合は遷移先のランナーにも適用する
                ObjectAnimationRunner transitionTo = runner.getTransitionTo();
                if (transitionTo != null) {
                    action.accept(transitionTo);
                }
            }
        }
    }

    private static boolean matches(ObjectAnimationRunner runner, String[] prefixes) {
        ObjectAnimation animation = runner.getAnimation();
        if (animation == null || animation.name == null) return false;

        for (String prefix : prefixes) {
            if (animation.name.startsWith(prefix)) return true;
        }
        return false;
    }

    private static void setSpeed(ObjectAnimationRunner runner, float speed) {
        if (runner instanceof ISpeedModifiable modifiable) {
            modifiable.tacz_attributes$setSpeedMultiplier(speed);
        }
    }
}
