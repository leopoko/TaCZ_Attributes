package com.github.leopoko.tacz_attributes.client;

import com.github.leopoko.tacz_attributes.Tacz_attributes;
import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.api.entity.IGunOperator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * クライアント側でdrawアニメーションの速度をdraw_speed属性に追従させるイベントハンドラ。
 * <p>
 * Luaステートマシンでは:
 * - put_away: context:getPutAwayTime() でアニメーション速度が決まる（GunItemRendererWrapperMixinで対応済み）
 * - draw: アニメーションは固定速度で再生されるため、ObjectAnimationRunner の speedMultiplier で加速する
 * <p>
 * 毎クライアントtickで以下を行う:
 * 1. getSynDrawCoolDown() でdraw/put-away状態を検出
 * 2. draw状態中であればdrawアニメーションのランナーに速度倍率を設定
 * 3. draw終了時に速度倍率をリセット
 * <p>
 * トラックの特定は {@link AnimationSpeedApplier} がアニメーション名で行う。
 * TaCZ 1.1.8 でトラック番号が変動したため、番号の直接指定はしていない。
 */
@Mod.EventBusSubscriber(modid = Tacz_attributes.MODID, value = Dist.CLIENT)
public class DrawAnimationSpeedHandler {

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
                AnimationSpeedApplier.apply(player, (float) speed, AnimationSpeedApplier.DRAW_ANIMATIONS);
            }
            wasDrawing = true;
        } else if (wasDrawing) {
            // draw終了時に速度倍率をリセット
            AnimationSpeedApplier.reset(player);
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

    private static double getAttributeValue(LocalPlayer player, Attribute attribute) {
        if (player.getAttributes().hasAttribute(attribute)) {
            return player.getAttributeValue(attribute);
        }
        return 1.0;
    }
}
