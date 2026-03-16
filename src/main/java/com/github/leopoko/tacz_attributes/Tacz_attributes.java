package com.github.leopoko.tacz_attributes;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.client.AttributePropertyModifier;
import com.github.leopoko.tacz_attributes.client.DiagramsModifierWrapper;
import com.mojang.logging.LogUtils;
import com.tacz.guns.api.modifier.IAttachmentModifier;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Set;

@Mod(Tacz_attributes.MODID)
public class Tacz_attributes {

    public static final String MODID = "tacz_attributes";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Tacz_attributes(IEventBus modEventBus) {
        CustomAttributes.ATTRIBUTES.register(modEventBus);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(this::onClientSetup);
        }

        LOGGER.info("TaCZ Attributes を初期化しました");
    }

    @SuppressWarnings({"rawtypes"})
    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Map modifiers = AttachmentPropertyManager.getModifiers();

            // TaCZモディファイアをラップ（9ID）
            Set<String> wrappableIds = Set.of(
                    "damage", "recoil", "inaccuracy",
                    "rpm", "ads", "head_shot", "knockback", "pierce", "movement_speed"
            );
            for (String id : wrappableIds) {
                IAttachmentModifier existing = (IAttachmentModifier) modifiers.get(id);
                if (existing != null) {
                    modifiers.put(id, new DiagramsModifierWrapper(existing));
                }
            }

            // フォールバック用モディファイアを登録
            modifiers.put(AttributePropertyModifier.ID, new AttributePropertyModifier());
            LOGGER.info("属性プロパティモディファイアを登録しました");
        });
    }
}
