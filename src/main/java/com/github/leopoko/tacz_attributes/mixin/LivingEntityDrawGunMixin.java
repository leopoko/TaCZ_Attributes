package com.github.leopoko.tacz_attributes.mixin;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.github.leopoko.tacz_attributes.util.GunTypeResolver;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.entity.shooter.LivingEntityDrawGun;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

/**
 * LivingEntityDrawGun に対するMixin。
 * 武器の取り出し・しまい速度属性に基づいて、draw/holsterクールダウンを変更する。
 * <p>
 * 実装方針:
 * 1. draw() の TAIL で drawTimestamp の未来オフセット（holster時間）をスケーリング
 * 2. getDrawCoolDown() の RETURN で draw時間分のデルタを減算
 * <p>
 * 計算式:
 * - holster時間 = 元holster時間 / (全体draw_speed × 銃種別draw_speed)
 * - draw時間 = 元draw時間 / (全体draw_speed × 銃種別draw_speed)
 */
@Mixin(LivingEntityDrawGun.class)
public class LivingEntityDrawGunMixin {

    @Shadow(remap = false)
    private LivingEntity shooter;

    @Shadow(remap = false)
    private ShooterDataHolder data;

    /**
     * draw() の末尾で drawTimestamp の未来オフセットをスケーリングする。
     * drawTimestamp = now + holsterTimeMs に設定されているため、
     * 未来オフセット（holster時間）を draw_speed で除算して短縮する。
     */
    @Inject(method = "draw", at = @At("TAIL"), remap = false)
    private void tacz_attributes$scaleDrawTimestamp(Supplier<ItemStack> gunItemSupplier, CallbackInfo ci) {
        if (this.shooter == null) return;

        double speed = tacz_attributes$getDrawSpeedModifier();
        if (speed == 1.0) return;

        long now = System.currentTimeMillis();
        long futureOffset = this.data.drawTimestamp - now;
        if (futureOffset > 0) {
            long scaledOffset = (long) (futureOffset / speed);
            this.data.drawTimestamp = now + scaledOffset;

        }
    }

    /**
     * getDrawCoolDown() の戻り値からdraw時間分のデルタを減算する。
     * <p>
     * coolDown = drawTimeMs - (now - drawTimestamp) - 5
     * holster時間は既にdrawTimestampに反映済み。
     * draw時間を短縮するため、drawTimeMs × (1 - 1/speed) を減算する。
     */
    @Inject(method = "getDrawCoolDown", at = @At("RETURN"), remap = false, cancellable = true)
    private void tacz_attributes$scaleDrawCoolDown(CallbackInfoReturnable<Long> cir) {
        long coolDown = cir.getReturnValue();
        if (coolDown <= 0) return;
        if (this.shooter == null || this.data.currentGunItem == null) return;

        double speed = tacz_attributes$getDrawSpeedModifier();
        if (speed == 1.0) return;

        ItemStack gunItem = this.data.currentGunItem.get();
        if (!(gunItem.getItem() instanceof IGun iGun)) return;

        ResourceLocation gunId = iGun.getGunId(gunItem);
        TimelessAPI.getCommonGunIndex(gunId).ifPresent(index -> {
            long drawMs = (long) (index.getGunData().getDrawTime() * 1000);
            long delta = (long) (drawMs * (1.0 - 1.0 / speed));
            long adjusted = Math.max(0L, coolDown - delta);

            cir.setReturnValue(adjusted);
        });
    }

    @Unique
    private double tacz_attributes$getDrawSpeedModifier() {
        if (this.shooter == null) return 1.0;

        double globalSpeed = tacz_attributes$getAttributeValue(this.shooter, CustomAttributes.DRAW_SPEED);

        // 銃種は切り替え先の武器（メインハンド）で判定
        GunType gunType = GunTypeResolver.resolveFromItem(this.shooter.getMainHandItem());
        double typeSpeed = 1.0;
        if (gunType != null) {
            typeSpeed = tacz_attributes$getAttributeValue(this.shooter, gunType.getDrawSpeedAttribute());
        }

        return globalSpeed * typeSpeed;
    }

    @Unique
    private static double tacz_attributes$getAttributeValue(LivingEntity entity, Holder<Attribute> attribute) {
        if (entity.getAttributes().hasAttribute(attribute)) {
            return entity.getAttributeValue(attribute);
        }
        return 1.0;
    }
}
