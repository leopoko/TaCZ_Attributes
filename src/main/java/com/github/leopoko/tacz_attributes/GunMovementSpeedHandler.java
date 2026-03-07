package com.github.leopoko.tacz_attributes;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.api.item.IGun;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

/**
 * 銃装備時の移動速度属性を適用するイベントハンドラ。
 * <p>
 * プレイヤーが銃を持っている間、gun_movement_speed 属性と銃種別の移動速度属性に基づき
 * MULTIPLY_TOTAL の AttributeModifier を MOVEMENT_SPEED に適用する。
 * TaCZ 自身の速度修正（重量・エイム・リロード）の上に乗算される。
 */
@Mod.EventBusSubscriber(modid = Tacz_attributes.MODID)
public class GunMovementSpeedHandler {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final UUID TACZ_ATTR_SPEED_MODIFIER_UUID = UUID.fromString("a3c7e8f1-5d2b-4a96-b8e3-1f7d9c6a2e04");

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.side != LogicalSide.SERVER) return;

        Player player = event.player;
        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr == null) return;

        // 既存のModifierを除去
        speedAttr.removeModifier(TACZ_ATTR_SPEED_MODIFIER_UUID);

        // 銃を持っていない場合は何もしない
        ItemStack mainHand = player.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(mainHand);
        if (iGun == null) return;

        GunType gunType = GunTypeResolver.resolveFromItem(mainHand);
        double globalSpeed = getAttributeValue(player, CustomAttributes.GUN_MOVEMENT_SPEED.get());
        double typeSpeed = 1.0;
        if (gunType != null) {
            typeSpeed = getAttributeValue(player, gunType.getGunMovementSpeedAttribute().get());
        }
        double combined = globalSpeed * typeSpeed;

        if (combined == 1.0) return;

        // MULTIPLY_TOTAL: 最終値 *= (1 + amount)
        // combined=0.8 → amount=-0.2 → 最終値 *= 0.8
        double amount = combined - 1.0;

        if (!FMLEnvironment.production) {
            LOGGER.info("[TaCZ Attributes] 移動速度倍率適用: combined={} (全体: {}, 銃種[{}]: {})",
                    combined, globalSpeed,
                    gunType != null ? gunType.getTypeId() : "unknown", typeSpeed);
        }

        speedAttr.addTransientModifier(new AttributeModifier(
                TACZ_ATTR_SPEED_MODIFIER_UUID, "TaCZ Attributes Gun Movement Speed",
                amount, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    private static double getAttributeValue(Player player, Attribute attribute) {
        if (player.getAttributes().hasAttribute(attribute)) {
            return player.getAttributeValue(attribute);
        }
        return 1.0;
    }
}
