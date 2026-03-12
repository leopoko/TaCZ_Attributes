package com.github.leopoko.tacz_attributes;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.api.item.IGun;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 銃装備時の移動速度属性を適用するイベントハンドラ。
 * <p>
 * プレイヤーが銃を持っている間、gun_movement_speed 属性と銃種別の移動速度属性に基づき
 * MULTIPLY_TOTAL の AttributeModifier を MOVEMENT_SPEED に適用する。
 * TaCZ 自身の速度修正（重量・エイム・リロード）の上に乗算される。
 */
@EventBusSubscriber(modid = Tacz_attributes.MODID)
public class GunMovementSpeedHandler {

    private static final ResourceLocation TACZ_ATTR_SPEED_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(Tacz_attributes.MODID, "gun_movement_speed");

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) return;

        Player player = event.getEntity();
        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr == null) return;

        // 既存のModifierを除去
        speedAttr.removeModifier(TACZ_ATTR_SPEED_MODIFIER_ID);

        // 銃を持っていない場合は何もしない
        ItemStack mainHand = player.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(mainHand);
        if (iGun == null) return;

        GunType gunType = GunTypeResolver.resolveFromItem(mainHand);
        double globalSpeed = getAttributeValue(player, CustomAttributes.GUN_MOVEMENT_SPEED);
        double typeSpeed = 1.0;
        if (gunType != null) {
            typeSpeed = getAttributeValue(player, gunType.getGunMovementSpeedAttribute());
        }
        double combined = globalSpeed * typeSpeed;

        if (combined == 1.0) return;

        // MULTIPLY_TOTAL: 最終値 *= (1 + amount)
        // combined=0.8 → amount=-0.2 → 最終値 *= 0.8
        double amount = combined - 1.0;

        speedAttr.addTransientModifier(new AttributeModifier(
                TACZ_ATTR_SPEED_MODIFIER_ID, amount,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    private static double getAttributeValue(Player player, Holder<Attribute> attribute) {
        if (player.getAttributes().hasAttribute(attribute)) {
            return player.getAttributeValue(attribute);
        }
        return 1.0;
    }
}
