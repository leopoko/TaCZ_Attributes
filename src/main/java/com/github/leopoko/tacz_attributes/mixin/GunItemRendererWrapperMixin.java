package com.github.leopoko.tacz_attributes.mixin;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.client.renderer.item.GunItemRendererWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * GunItemRendererWrapper に対するクライアント側Mixin。
 * getPutAwayTime() の戻り値をスケーリングし、
 * しまいアニメーションの速度を draw_speed 属性に基づいて変更する。
 * <p>
 * この値は LocalPlayerDraw.draw() → getDrawTime() → renderer.getPutAwayTime() で使用され、
 * doPutAway（アニメーション）と doDraw（音声タイミング）の両方に影響する。
 */
@Mixin(GunItemRendererWrapper.class)
public class GunItemRendererWrapperMixin {

    /**
     * getPutAwayTime() の戻り値をdraw_speed属性でスケーリングする。
     * 戻り値はミリ秒単位。
     */
    @Inject(method = "getPutAwayTime", at = @At("RETURN"), remap = false, cancellable = true)
    private void tacz_attributes$scalePutAwayTime(ItemStack stack, CallbackInfoReturnable<Long> cir) {
        long time = cir.getReturnValue();
        if (time <= 0) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        double speed = tacz_attributes$getDrawSpeedModifier(player);
        if (speed == 1.0) return;

        long scaled = Math.max(1L, (long) (time / speed));

        cir.setReturnValue(scaled);
    }

    @Unique
    private static double tacz_attributes$getDrawSpeedModifier(LocalPlayer player) {
        double globalSpeed = tacz_attributes$getAttributeValue(player, CustomAttributes.DRAW_SPEED.get());

        // 銃種は切り替え先の武器（メインハンド）で判定
        GunType gunType = GunTypeResolver.resolveFromItem(player.getMainHandItem());
        double typeSpeed = 1.0;
        if (gunType != null) {
            typeSpeed = tacz_attributes$getAttributeValue(player, gunType.getDrawSpeedAttribute().get());
        }

        return globalSpeed * typeSpeed;
    }

    @Unique
    private static double tacz_attributes$getAttributeValue(LocalPlayer player, Attribute attribute) {
        if (player.getAttributes().hasAttribute(attribute)) {
            return player.getAttributeValue(attribute);
        }
        return 1.0;
    }
}
