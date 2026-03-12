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
import com.tacz.guns.api.entity.ReloadState;
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
 * クライアント側でリロード/ボルトアニメーションの速度を属性に追従させるイベントハンドラ。
 * <p>
 * 毎クライアントtickで以下を行う:
 * 1. プレイヤーのリロード/ボルト状態を確認
 * 2. リロード中であればリロード速度倍率、ボルト中であればコッキング速度倍率を
 *    MAIN_TRACKのアニメーションランナーに設定
 * 3. どちらも終了した時に速度倍率をリセット
 */
@Mod.EventBusSubscriber(modid = Tacz_attributes.MODID, value = Dist.CLIENT)
public class ReloadAnimationSpeedHandler {

    /**
     * デフォルトステートマシンでのMAIN_TRACKの位置。
     * STATIC_TRACK_LINE = 0, MAIN_TRACK = trackIndex 4
     * (BASE=0, BOLT_CAUGHT=1, SAFETY=2, ADS=3, MAIN=4)
     */
    private static final int STATIC_TRACK_LINE = 0;
    private static final int MAIN_TRACK_INDEX = 4;

    /** アニメーション速度が変更中であることを示すフラグ */
    private static boolean wasSpeedModified = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        IGunOperator gunOperator = IGunOperator.fromLivingEntity(player);
        ReloadState reloadState = gunOperator.getSynReloadState();
        boolean isReloading = reloadState.getStateType().isReloading();
        boolean isBolting = gunOperator.getSynIsBolting();

        if (isReloading) {
            double speed = getReloadSpeedModifier(player);
            if (speed != 1.0) {
                applySpeedToMainTrack(player, (float) speed);
            }
            wasSpeedModified = true;
        } else if (isBolting) {
            double speed = getBoltSpeedModifier(player);
            if (speed != 1.0) {
                applySpeedToMainTrack(player, (float) speed);
            }
            wasSpeedModified = true;
        } else if (wasSpeedModified) {
            // リロード/ボルト終了時に速度倍率をリセット
            applySpeedToMainTrack(player, 1.0f);
            wasSpeedModified = false;
        }
    }

    private static double getReloadSpeedModifier(LocalPlayer player) {
        // 全体リロード速度倍率
        double globalSpeed = 1.0;
        if (player.getAttributes().hasAttribute(CustomAttributes.RELOAD_SPEED.get())) {
            globalSpeed = player.getAttributeValue(CustomAttributes.RELOAD_SPEED.get());
        }

        // 銃種別リロード速度倍率
        double typeSpeed = 1.0;
        ItemStack mainHand = player.getMainHandItem();
        GunType gunType = GunTypeResolver.resolveFromItem(mainHand);
        if (gunType != null) {
            Attribute typeAttr = gunType.getReloadSpeedAttribute().get();
            if (player.getAttributes().hasAttribute(typeAttr)) {
                typeSpeed = player.getAttributeValue(typeAttr);
            }
        }

        return globalSpeed * typeSpeed;
    }

    private static double getBoltSpeedModifier(LocalPlayer player) {
        // 全体コッキング速度倍率
        double globalSpeed = 1.0;
        if (player.getAttributes().hasAttribute(CustomAttributes.BOLT_ACTION_SPEED.get())) {
            globalSpeed = player.getAttributeValue(CustomAttributes.BOLT_ACTION_SPEED.get());
        }

        // 銃種別コッキング速度倍率
        double typeSpeed = 1.0;
        ItemStack mainHand = player.getMainHandItem();
        GunType gunType = GunTypeResolver.resolveFromItem(mainHand);
        if (gunType != null) {
            Attribute typeAttr = gunType.getBoltActionSpeedAttribute().get();
            if (player.getAttributes().hasAttribute(typeAttr)) {
                typeSpeed = player.getAttributeValue(typeAttr);
            }
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

        // DiscreteTrackArray からMAIN_TRACKのコントローラポインタを取得
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
}
