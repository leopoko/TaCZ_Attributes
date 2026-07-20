package com.github.leopoko.tacz_attributes.client;

import com.github.leopoko.tacz_attributes.Tacz_attributes;
import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.entity.ReloadState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * クライアント側でリロード/ボルトアニメーションの速度を属性に追従させるイベントハンドラ。
 * <p>
 * 毎クライアントtickで以下を行う:
 * 1. プレイヤーのリロード/ボルト状態を確認
 * 2. リロード中であればリロード速度倍率、ボルト中であればコッキング速度倍率を
 *    該当アニメーションのランナーに設定
 * 3. どちらも終了した時に速度倍率をリセット
 * <p>
 * トラックの特定は {@link AnimationSpeedApplier} がアニメーション名で行う。
 * TaCZ 1.1.8 でトラック番号が変動したため、番号の直接指定はしていない。
 */
@Mod.EventBusSubscriber(modid = Tacz_attributes.MODID, value = Dist.CLIENT)
public class ReloadAnimationSpeedHandler {

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
                AnimationSpeedApplier.apply(player, (float) speed, AnimationSpeedApplier.RELOAD_ANIMATIONS);
            }
            wasSpeedModified = true;
        } else if (isBolting) {
            double speed = getBoltSpeedModifier(player);
            if (speed != 1.0) {
                AnimationSpeedApplier.apply(player, (float) speed, AnimationSpeedApplier.BOLT_ANIMATIONS);
            }
            wasSpeedModified = true;
        } else if (wasSpeedModified) {
            // リロード/ボルト終了時に速度倍率をリセット
            AnimationSpeedApplier.reset(player);
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
}
