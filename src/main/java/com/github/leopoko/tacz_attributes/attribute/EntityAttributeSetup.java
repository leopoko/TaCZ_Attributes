package com.github.leopoko.tacz_attributes.attribute;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityAttributeSetup {

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeModificationEvent event) {
        if (event.getTypes().contains(EntityType.PLAYER)) {
            event.add(EntityType.PLAYER, CustomAttributes.GUN_DAMAGE.get());
            event.add(EntityType.PLAYER, CustomAttributes.RELOAD_SPEED.get());

            // 銃種別属性
            for (GunType gunType : GunType.values()) {
                event.add(EntityType.PLAYER, gunType.getDamageAttribute().get());
                event.add(EntityType.PLAYER, gunType.getReloadSpeedAttribute().get());
            }
        }
    }
}
