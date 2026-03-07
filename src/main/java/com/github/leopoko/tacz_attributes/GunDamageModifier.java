package com.github.leopoko.tacz_attributes;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
public class GunDamageModifier {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onGunHurt(EntityHurtByGunEvent.Pre event) {
        if (event.getLogicalSide() != LogicalSide.SERVER) return;

        LivingEntity attacker = event.getAttacker();
        if (attacker == null) return;

        if (attacker.getAttributes().hasAttribute(CustomAttributes.GUN_DAMAGE.get())) {
            double damageModifier = attacker.getAttributeValue(CustomAttributes.GUN_DAMAGE.get());
            float modifiedDamage = (float) (event.getBaseAmount() * damageModifier);

            if (!FMLEnvironment.production) {
                LOGGER.debug("銃ダメージ倍率適用: {} -> {} (倍率: {})",
                        event.getBaseAmount(), modifiedDamage, damageModifier);
            }

            event.setBaseAmount(modifiedDamage);
        }
    }
}
