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

/**
 * アニメーションランナーへの速度倍率設定を担う共通ユーティリティ。
 * <p>
 * 対象トラックの特定には<b>アニメーション名の接頭辞一致</b>を用いる。
 * かつてはデフォルトステートマシンにおける MAIN_TRACK のインデックス（4）を
 * ハードコードしていたが、TaCZ 1.1.8 で default_state_machine.lua に
 * PRE_PARALLEL_TRACK_1〜5 が挿入されて MAIN_TRACK が 9 に移動したため、
 * 無効なトラックを指して速度倍率が無視される不具合が発生した。
 * <p>
 * この種の破損はコンパイルエラーにもMixinエラーにもならず静かに誤動作するため、
 * Luaのトラック番号に依存しない実装とすること。
 */
public final class AnimationSpeedApplier {

    private AnimationSpeedApplier() {
    }

    /**
     * 指定した接頭辞のいずれかに一致する名前のアニメーションを保持するランナーに、
     * 速度倍率を設定する。
     * <p>
     * 遷移中は遷移元と遷移先で別のアニメーションが動いているため、
     * 両者を個別に判定し、一致した側にのみ倍率を設定する。
     *
     * @param namePrefixes 対象アニメーション名の接頭辞（例: "reload", "bolt", "draw"）
     */
    public static void applyToAnimation(LocalPlayer player, float speed, String... namePrefixes) {
        forEachRunner(player, runner -> {
            if (matches(runner, namePrefixes)) {
                setSpeed(runner, speed);
            }
        });
    }

    /**
     * ステートマシン上の全ランナーの速度倍率を 1.0 に戻す。
     * <p>
     * リセット時点では対象アニメーションが既に停止・切り替わっている場合があり、
     * 接頭辞一致では取りこぼすため、全ランナーを対象にする。
     */
    public static void resetAll(LocalPlayer player) {
        forEachRunner(player, runner -> setSpeed(runner, 1.0f));
    }

    /**
     * メインハンドの銃のステートマシンに属する全ランナー（遷移先を含む）に処理を適用する。
     */
    private static void forEachRunner(LocalPlayer player, java.util.function.Consumer<ObjectAnimationRunner> action) {
        ItemStack mainHand = player.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(mainHand);
        if (iGun == null) return;

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

            for (Integer pointer : tracks) {
                if (pointer == null) continue;

                ObjectAnimationRunner runner = controller.getAnimation(pointer);
                if (runner == null) continue;

                action.accept(runner);

                ObjectAnimationRunner transitionTo = runner.getTransitionTo();
                if (transitionTo != null) {
                    action.accept(transitionTo);
                }
            }
        }
    }

    private static boolean matches(ObjectAnimationRunner runner, String[] namePrefixes) {
        ObjectAnimation animation = runner.getAnimation();
        if (animation == null || animation.name == null) return false;

        for (String prefix : namePrefixes) {
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
