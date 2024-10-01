package com.github.leopoko.tacz_attributes.mixin;

import com.tacz.guns.entity.shooter.LivingEntityReload;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntityReload.class)
public interface LivingEntityReloadAccessor {

    @Accessor("shooter")
    LivingEntity getShooter();

    @Accessor("data")
    ShooterDataHolder getData();

}