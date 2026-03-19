package com.github.leopoko.tacz_attributes.mixin;

import com.github.leopoko.tacz_attributes.util.DamageModifierHelper;
import com.tacz.guns.entity.EntityKineticBullet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * TargetBlock（人型標的ブロック）に対するMixin。
 * <p>
 * TargetBlock.onProjectileHit() 内の EntityKineticBullet.getDamage() の戻り値に
 * 属性ダメージ倍率を適用し、標的に表示されるダメージ値に属性を反映させる。
 */
@Mixin(targets = "com.tacz.guns.block.TargetBlock")
public abstract class TargetBlockMixin {

    @Redirect(
            method = "onProjectileHit",
            at = @At(value = "INVOKE", target = "Lcom/tacz/guns/entity/EntityKineticBullet;getDamage(Lnet/minecraft/world/phys/Vec3;)F", remap = false)
    )
    private float tacz_attributes$modifyTargetBlockDamage(EntityKineticBullet bullet, Vec3 hitPos) {
        float damage = bullet.getDamage(hitPos);

        Entity owner = bullet.getOwner();
        if (!(owner instanceof LivingEntity shooter)) return damage;

        ResourceLocation gunId = bullet.getGunId();
        double modifier = DamageModifierHelper.calculateDamageModifier(shooter, gunId);
        if (modifier == 1.0) return damage;

        return (float) (damage * modifier);
    }
}
