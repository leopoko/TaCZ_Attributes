package com.github.leopoko.tacz_attributes;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = Tacz_attributes.MODID)
public class GunDamageModifier {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onGunHurt(EntityHurtByGunEvent.Pre event) {
        if (event.getLogicalSide() != LogicalSide.SERVER) return;

        LivingEntity attacker = event.getAttacker();
        if (attacker == null) return;

        // 全体ダメージ倍率
        double globalModifier = 1.0;
        if (attacker.getAttributes().hasAttribute(CustomAttributes.GUN_DAMAGE.get())) {
            globalModifier = attacker.getAttributeValue(CustomAttributes.GUN_DAMAGE.get());
        }

        // 銃種別ダメージ倍率
        double typeModifier = 1.0;
        ResourceLocation gunId = event.getGunId();
        GunType gunType = GunTypeResolver.resolve(gunId);
        if (gunType != null) {
            Attribute typeAttr = gunType.getDamageAttribute().get();
            if (attacker.getAttributes().hasAttribute(typeAttr)) {
                typeModifier = attacker.getAttributeValue(typeAttr);
            }
        }

        double combinedModifier = globalModifier * typeModifier;
        if (combinedModifier == 1.0) return;

        float modifiedDamage = (float) (event.getBaseAmount() * combinedModifier);

        if (!FMLEnvironment.production) {
            LOGGER.info("[TaCZ Attributes] 銃ダメージ倍率適用: {} -> {} (全体: {}, 銃種[{}]: {})",
                    event.getBaseAmount(), modifiedDamage, globalModifier,
                    gunType != null ? gunType.getTypeId() : "unknown", typeModifier);
        }

        event.setBaseAmount(modifiedDamage);
    }
}
