package com.github.leopoko.tacz_attributes;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.client.AttributePropertyModifier;
import com.github.leopoko.tacz_attributes.client.DiagramsModifierWrapper;
import com.mojang.logging.LogUtils;
import com.tacz.guns.api.modifier.IAttachmentModifier;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Set;

@Mod(Tacz_attributes.MODID)
public class Tacz_attributes {

    public static final String MODID = "tacz_attributes";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Tacz_attributes() {
        CustomAttributes.ATTRIBUTES.register(FMLJavaModLoadingContext.get().getModEventBus());

        if (FMLEnvironment.dist == Dist.CLIENT) {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        }

        LOGGER.info("TaCZ Attributes を初期化しました");
    }

    @SuppressWarnings({"rawtypes"})
    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Map modifiers = AttachmentPropertyManager.getModifiers();

            // 既存のTaCZモディファイアをラップして属性倍率を反映
            Set<String> wrappableIds = Set.of(
                    "damage", "recoil_modifier", "inaccuracy_addend", "inaccuracy",
                    "rpm", "ads_addend", "head_shot", "knockback", "pierce", "movement_speed"
            );
            for (String id : wrappableIds) {
                IAttachmentModifier existing = (IAttachmentModifier) modifiers.get(id);
                if (existing != null) {
                    modifiers.put(id, new DiagramsModifierWrapper(existing));
                }
            }

            // TaCZモディファイアでカバーされないカテゴリ用の属性モディファイア
            modifiers.put(AttributePropertyModifier.ID, new AttributePropertyModifier());
            LOGGER.info("属性プロパティモディファイアを登録しました");
        });
    }
}
