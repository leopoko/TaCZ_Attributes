package com.github.leopoko.tacz_attributes.client;

import com.github.leopoko.tacz_attributes.Tacz_attributes;
import com.github.leopoko.tacz_attributes.api.ISpeedModifiable;
import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.client.animation.AnimationController;
import com.tacz.guns.api.client.animation.DiscreteTrackArray;
import com.tacz.guns.api.client.animation.ObjectAnimationRunner;
import com.tacz.guns.api.client.animation.statemachine.AnimationStateMachine;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.resource.GunDisplayInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * クライアント側でdrawアニメーションの速度をdraw_speed属性に追従させるイベントハンドラ。
 * <p>
 * Luaステートマシンでは:
 * - put_away: context:getPutAwayTime() でアニメーション速度が決まる（GunItemRendererWrapperMixinで対応済み）
 * - draw: アニメーションは固定速度で再生されるため、ObjectAnimationRunner の speedMultiplier で加速する
 * <p>
 * 毎クライアントtickで以下を行う:
 * 1. getSynDrawCoolDown() でdraw/put-away状態を検出
 * 2. draw状態中であればMAIN_TRACKのアニメーションランナーに速度倍率を設定
 * 3. draw終了時に速度倍率をリセット
 */
@Mod.EventBusSubscriber(modid = Tacz_attributes.MODID, value = Dist.CLIENT)
public class DrawAnimationSpeedHandler {

    /**
     * デフォルトステートマシンでのMAIN_TRACKの位置。
     * STATIC_TRACK_LINE = 0, MAIN_TRACK = trackIndex 4
     * (BASE=0, BOLT_CAUGHT=1, SAFETY=2, ADS=3, MAIN=4)
     */
    private static final int STATIC_TRACK_LINE = 0;
    private static final int MAIN_TRACK_INDEX = 4;

    private static boolean wasDrawing = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        long drawCoolDown = IGunOperator.fromLivingEntity(player).getSynDrawCoolDown();
        boolean isDrawing = drawCoolDown > 0;

        if (isDrawing) {
            double speed = getDrawSpeedModifier(player);
            if (speed != 1.0) {
                applySpeedToMainTrack(player, (float) speed);
            }
            wasDrawing = true;
        } else if (wasDrawing) {
            // draw終了時に速度倍率をリセット
            applySpeedToMainTrack(player, 1.0f);
            wasDrawing = false;
        }
    }

    private static double getDrawSpeedModifier(LocalPlayer player) {
        double globalSpeed = getAttributeValue(player, CustomAttributes.DRAW_SPEED.get());

        // 銃種は現在のメインハンド（新しい武器）で判定
        ItemStack mainHand = player.getMainHandItem();
        GunType gunType = GunTypeResolver.resolveFromItem(mainHand);
        double typeSpeed = 1.0;
        if (gunType != null) {
            typeSpeed = getAttributeValue(player, gunType.getDrawSpeedAttribute().get());
        }

        return globalSpeed * typeSpeed;
    }

    /**
     * MAIN_TRACKのアニメーションランナーに速度倍率を設定する。
     * ランナーが遷移中の場合、遷移先のランナーにも設定する。
     */
    private static void applySpeedToMainTrack(LocalPlayer player, float speed) {
        ItemStack mainHand = player.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(mainHand);
        if (iGun == null) return;

        GunDisplayInstance display = TimelessAPI.getGunDisplay(mainHand).orElse(null);
        if (display == null) return;

        AnimationStateMachine<?> stateMachine = display.getAnimationStateMachine();
        if (stateMachine == null || !stateMachine.isInitialized()) return;

        AnimationController controller = stateMachine.getAnimationController();
        if (controller == null) return;

        var context = stateMachine.getContext();
        if (context == null) return;

        DiscreteTrackArray trackArray = context.getTrackArray();
        if (trackArray.getTrackLineSize() <= STATIC_TRACK_LINE) return;

        List<Integer> staticTracks = trackArray.getByIndex(STATIC_TRACK_LINE);
        if (staticTracks.size() <= MAIN_TRACK_INDEX) return;

        int mainTrackPointer = staticTracks.get(MAIN_TRACK_INDEX);
        ObjectAnimationRunner runner = controller.getAnimation(mainTrackPointer);
        if (runner == null) return;

        // ランナーに速度倍率を設定
        setSpeedOnRunner(runner, speed);

        // 遷移先のランナーにも設定
        ObjectAnimationRunner transitionTo = runner.getTransitionTo();
        if (transitionTo != null) {
            setSpeedOnRunner(transitionTo, speed);
        }
    }

    private static void setSpeedOnRunner(ObjectAnimationRunner runner, float speed) {
        if (runner instanceof ISpeedModifiable modifiable) {
            modifiable.tacz_attributes$setSpeedMultiplier(speed);
        }
    }

    private static double getAttributeValue(LocalPlayer player, Attribute attribute) {
        if (player.getAttributes().hasAttribute(attribute)) {
            return player.getAttributeValue(attribute);
        }
        return 1.0;
    }
}
