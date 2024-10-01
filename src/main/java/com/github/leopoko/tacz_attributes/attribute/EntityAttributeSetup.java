package com.github.leopoko.tacz_attributes.attribute;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityAttributeSetup {

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeModificationEvent event) {

        LOGGER.info("EntityAttributeModificationEventが呼び出されました");

        // プレイヤーエンティティの属性にカスタム属性を追加
        if (event.getTypes().contains(EntityType.PLAYER)) {
            event.add(EntityType.PLAYER, CustomAttributes.GUN_DAMAGE.get());
            event.add(EntityType.PLAYER, CustomAttributes.RELOAD_SPEED.get());
        }

        //Logに出力
        LOGGER.info("EntityAttributeSetup.onEntityAttributeCreation fuck you");
    }
}
