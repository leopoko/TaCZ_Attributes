package com.github.leopoko.tacz_attributes;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(Tacz_attributes.MODID)
public class Tacz_attributes {

    public static final String MODID = "tacz_attributes";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Tacz_attributes(IEventBus modEventBus) {
        CustomAttributes.ATTRIBUTES.register(modEventBus);
        LOGGER.info("TaCZ Attributes を初期化しました");
    }
}
