package com.github.leopoko.tacz_attributes;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.client.AttributePropertyModifier;
import com.mojang.logging.LogUtils;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

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

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            AttachmentPropertyManager.getModifiers().put(
                    AttributePropertyModifier.ID, new AttributePropertyModifier());
            LOGGER.info("属性プロパティモディファイアを登録しました");
        });
    }
}
