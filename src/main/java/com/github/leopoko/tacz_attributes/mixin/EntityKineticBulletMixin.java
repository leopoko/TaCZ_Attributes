package com.github.leopoko.tacz_attributes.mixin;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.entity.EntityKineticBullet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * EntityKineticBullet に対するMixin。
 * <p>
 * - ノックバック倍率: onHitEntity() 内の KnockBackModifier.setKnockBackStrength() の引数を変更
 * - 貫通数倍率: コンストラクタ末尾で pierce フィールドを変更
 */
@Mixin(EntityKineticBullet.class)
public abstract class EntityKineticBulletMixin extends Projectile {

    @Shadow(remap = false)
    private int pierce;

    @Shadow(remap = false)
    private ResourceLocation gunId;

    protected EntityKineticBulletMixin(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    /**
     * onHitEntity() 内の KnockBackModifier.setKnockBackStrength(double) の引数を変更し、
     * ノックバック基本値を加算した後、ノックバック倍率を適用する。
     * <p>
     * 計算式: 最終knockback = (元knockback + knockback_base + 銃種別knockback_base) × knockback_multiplier × 銃種別knockback_multiplier
     * TaCZの銃はデフォルトknockback=0のため、基本値を加算しないと倍率が効かない。
     */
    @ModifyArg(
            method = "onHitEntity",
            at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/entity/KnockBackModifier;setKnockBackStrength(D)V"),
            remap = false
    )
    private double tacz_attributes$modifyKnockback(double knockback) {
        Entity owner = this.getOwner();
        if (!(owner instanceof LivingEntity shooter)) return knockback;

        GunType gunType = GunTypeResolver.resolve(this.gunId);

        // 基本値を加算（デフォルト0.0 = 追加なし）
        double globalBase = tacz_attributes$getKnockbackBaseValue(shooter, CustomAttributes.KNOCKBACK_BASE);
        double typeBase = 0.0;
        if (gunType != null) {
            typeBase = tacz_attributes$getKnockbackBaseValue(shooter, gunType.getKnockbackBaseAttribute());
        }
        double withBase = knockback + globalBase + typeBase;

        // 倍率を適用
        double globalMult = tacz_attributes$getAttributeValue(shooter, CustomAttributes.KNOCKBACK_MULTIPLIER);
        double typeMult = 1.0;
        if (gunType != null) {
            typeMult = tacz_attributes$getAttributeValue(shooter, gunType.getKnockbackMultiplierAttribute());
        }
        double combinedMult = globalMult * typeMult;

        double result = withBase * combinedMult;

        if (result == knockback) return knockback;

        return result;
    }

    /**
     * コンストラクタ末尾で pierce フィールドを変更し、
     * 貫通数属性に基づいた倍率を適用する。
     */
    @Inject(
            method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;ZLcom/tacz/guns/resource/pojo/data/gun/GunData;Lcom/tacz/guns/resource/pojo/data/gun/BulletData;)V",
            at = @At("TAIL"),
            remap = false
    )
    private void tacz_attributes$modifyPierce(CallbackInfo ci) {
        Entity owner = this.getOwner();
        if (!(owner instanceof LivingEntity shooter)) return;

        GunType gunType = GunTypeResolver.resolve(this.gunId);
        double globalPierce = tacz_attributes$getAttributeValue(shooter, CustomAttributes.PIERCE_MULTIPLIER);
        double typePierce = 1.0;
        if (gunType != null) {
            typePierce = tacz_attributes$getAttributeValue(shooter, gunType.getPierceMultiplierAttribute());
        }
        double combined = globalPierce * typePierce;
        if (combined == 1.0) return;

        int oldPierce = this.pierce;
        this.pierce = Math.max(1, (int) (this.pierce * combined));

    }

    @Unique
    private static double tacz_attributes$getAttributeValue(LivingEntity entity, Holder<Attribute> attribute) {
        if (entity.getAttributes().hasAttribute(attribute)) {
            return entity.getAttributeValue(attribute);
        }
        return 1.0;
    }

    @Unique
    private static double tacz_attributes$getKnockbackBaseValue(LivingEntity entity, Holder<Attribute> attribute) {
        if (entity.getAttributes().hasAttribute(attribute)) {
            return entity.getAttributeValue(attribute);
        }
        return 0.0;
    }
}
