package com.github.leopoko.tacz_attributes.client;

import com.github.leopoko.tacz_attributes.Tacz_attributes;
import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.entity.ReloadState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * クライアント側でリロード/ボルトアニメーションの速度を属性に追従させるイベントハンドラ。
 * <p>
 * 毎クライアントtickで以下を行う:
 * 1. プレイヤーのリロード/ボルト状態を確認
 * 2. リロード中であればリロード速度倍率、ボルト中であればコッキング速度倍率を
 *    該当アニメーションのランナーに設定
 * 3. どちらも終了した時に速度倍率をリセット
 */
@EventBusSubscriber(modid = Tacz_attributes.MODID, value = Dist.CLIENT)
public class ReloadAnimationSpeedHandler {

    /** リロードアニメーション名の接頭辞（reload_empty / reload_tactical） */
    private static final String RELOAD_ANIMATION_PREFIX = "reload";

    /** コッキングアニメーション名の接頭辞（bolt） */
    private static final String BOLT_ANIMATION_PREFIX = "bolt";

    /** アニメーション速度が変更中であることを示すフラグ */
    private static boolean wasSpeedModified = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        IGunOperator gunOperator = IGunOperator.fromLivingEntity(player);
        ReloadState reloadState = gunOperator.getSynReloadState();
        boolean isReloading = reloadState.getStateType().isReloading();
        boolean isBolting = gunOperator.getSynIsBolting();

        if (isReloading) {
            double speed = getReloadSpeedModifier(player);
            if (speed != 1.0) {
                AnimationSpeedApplier.applyToAnimation(player, (float) speed, RELOAD_ANIMATION_PREFIX);
            }
            wasSpeedModified = true;
        } else if (isBolting) {
            double speed = getBoltSpeedModifier(player);
            if (speed != 1.0) {
                AnimationSpeedApplier.applyToAnimation(player, (float) speed, BOLT_ANIMATION_PREFIX);
            }
            wasSpeedModified = true;
        } else if (wasSpeedModified) {
            // リロード/ボルト終了時に速度倍率をリセット
            AnimationSpeedApplier.resetAll(player);
            wasSpeedModified = false;
        }
    }

    private static double getReloadSpeedModifier(LocalPlayer player) {
        // 全体リロード速度倍率
        double globalSpeed = 1.0;
        if (player.getAttributes().hasAttribute(CustomAttributes.RELOAD_SPEED)) {
            globalSpeed = player.getAttributeValue(CustomAttributes.RELOAD_SPEED);
        }

        // 銃種別リロード速度倍率
        double typeSpeed = 1.0;
        ItemStack mainHand = player.getMainHandItem();
        GunType gunType = GunTypeResolver.resolveFromItem(mainHand);
        if (gunType != null) {
            var typeAttr = gunType.getReloadSpeedAttribute();
            if (player.getAttributes().hasAttribute(typeAttr)) {
                typeSpeed = player.getAttributeValue(typeAttr);
            }
        }

        return globalSpeed * typeSpeed;
    }

    private static double getBoltSpeedModifier(LocalPlayer player) {
        // 全体コッキング速度倍率
        double globalSpeed = 1.0;
        if (player.getAttributes().hasAttribute(CustomAttributes.BOLT_ACTION_SPEED)) {
            globalSpeed = player.getAttributeValue(CustomAttributes.BOLT_ACTION_SPEED);
        }

        // 銃種別コッキング速度倍率
        double typeSpeed = 1.0;
        ItemStack mainHand = player.getMainHandItem();
        GunType gunType = GunTypeResolver.resolveFromItem(mainHand);
        if (gunType != null) {
            var typeAttr = gunType.getBoltActionSpeedAttribute();
            if (player.getAttributes().hasAttribute(typeAttr)) {
                typeSpeed = player.getAttributeValue(typeAttr);
            }
        }

        return globalSpeed * typeSpeed;
    }
}
