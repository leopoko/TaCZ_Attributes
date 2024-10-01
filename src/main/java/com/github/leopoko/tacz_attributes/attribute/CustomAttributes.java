package com.github.leopoko.tacz_attributes.attribute;

import com.github.leopoko.tacz_attributes.Tacz_attributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;

public class CustomAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Tacz_attributes.MODID);

    // カスタム属性 gun_damage の定義
    public static final RegistryObject<Attribute> GUN_DAMAGE = ATTRIBUTES.register("gun_damage",
            () -> new RangedAttribute("attribute.tacz_attributes.gun_damage", 1.0, 0.0, 1024.0).setSyncable(true));

    public static final RegistryObject<Attribute> RELOAD_SPEED = ATTRIBUTES.register("reload_speed",
            () -> new RangedAttribute("attribute.tacz_attributes.reload_speed", 1.0, 0.1, 20.0).setSyncable(true));
}