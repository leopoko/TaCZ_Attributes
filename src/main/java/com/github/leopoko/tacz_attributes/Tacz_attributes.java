package com.github.leopoko.tacz_attributes;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Tacz_attributes.MODID)
public class Tacz_attributes {

    public static final String MODID = "tacz_attributes";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Tacz_attributes() {
        CustomAttributes.ATTRIBUTES.register(FMLJavaModLoadingContext.get().getModEventBus());
        LOGGER.info("TaCZ Attributes を初期化しました");
    }
}
