package com.github.leopoko.tacz_attributes.mixin;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.ReloadState;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.entity.shooter.LivingEntityReload;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.GunReloadData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.tacz.guns.api.item.IGun;

import java.util.Optional;
import java.util.logging.Logger;

@Mixin(LivingEntityReload.class)
public abstract class LivingEntityReloadMixin {

    Logger LOGGER = Logger.getLogger("tacz_attributes");

    @Inject(method = "tickReloadState", at = @At("HEAD"), cancellable = true, remap = false)
    private void modifyReloadSpeed(CallbackInfoReturnable<ReloadState> cir) {
        // LivingEntityReloadのインスタンスを取得
        LivingEntityReload reloadHandler = (LivingEntityReload) (Object) this;

        // privateフィールドへのアクセスをサポートするようにshooterを取得
        LivingEntity shooter = ((LivingEntityReloadAccessor) reloadHandler).getShooter();

        // リロード速度の属性を取得
        double reloadSpeedModifier = 1.0;
        if (shooter.getAttributes().hasAttribute(CustomAttributes.RELOAD_SPEED.get())) {
            reloadSpeedModifier = shooter.getAttributeValue(CustomAttributes.RELOAD_SPEED.get());
            if (reloadSpeedModifier != 1.0) {
                LOGGER.info("リロード速度の修正: " + reloadSpeedModifier);
                ShooterDataHolder data = ((LivingEntityReloadAccessor) reloadHandler).getData();
                ReloadState reloadState = new ReloadState();
                reloadState.setStateType(ReloadState.StateType.NOT_RELOADING);
                reloadState.setCountDown(ReloadState.NOT_RELOADING_COUNTDOWN);
                // 判断是否正在进行装填流程。如果没有则返回。
                if (data.reloadTimestamp == -1 || data.currentGunItem == null) {
                    cir.setReturnValue(reloadState);
                    cir.cancel();
                    return;
                }
                if (!(data.currentGunItem.get().getItem() instanceof IGun iGun)) {
                    cir.setReturnValue(reloadState);
                    cir.cancel();
                    return;
                }
                ItemStack currentGunItem = data.currentGunItem.get();
                // 获取当前枪械的 ReloadData。如果没有则返回。
                ResourceLocation gunId = iGun.getGunId(currentGunItem);
                Optional<CommonGunIndex> gunIndexOptional = TimelessAPI.getCommonGunIndex(gunId);
                if (gunIndexOptional.isEmpty()) {
                    cir.setReturnValue(reloadState);
                    cir.cancel();
                    return;
                }
                GunData gunData = gunIndexOptional.get().getGunData();
                GunReloadData reloadData = gunData.getReloadData();
                // 计算新的 stateType 和 countDown
                long countDown = ReloadState.NOT_RELOADING_COUNTDOWN;
                ReloadState.StateType stateType = data.reloadStateType;
                long progressTime = System.currentTimeMillis() - data.reloadTimestamp;
                if (stateType.isReloadingEmpty()) {
                    long feedTime = (long) (reloadData.getFeed().getEmptyTime() * 1000 / reloadSpeedModifier);
                    long finishingTime = (long) (reloadData.getCooldown().getEmptyTime() * 1000 / reloadSpeedModifier);
                    if (progressTime < feedTime) {
                        stateType = ReloadState.StateType.EMPTY_RELOAD_FEEDING;
                        countDown = feedTime - progressTime;
                    } else if (progressTime < finishingTime) {
                        stateType = ReloadState.StateType.EMPTY_RELOAD_FINISHING;
                        countDown = finishingTime - progressTime;
                    } else {
                        stateType = ReloadState.StateType.NOT_RELOADING;
                        data.reloadTimestamp = -1;
                    }
                } else if (stateType.isReloadingTactical()) {
                    long feedTime = (long) (reloadData.getFeed().getTacticalTime() * 1000 / reloadSpeedModifier);
                    long finishingTime = (long) (reloadData.getCooldown().getTacticalTime() * 1000 / reloadSpeedModifier);
                    if (progressTime < feedTime) {
                        stateType = ReloadState.StateType.TACTICAL_RELOAD_FEEDING;
                        countDown = feedTime - progressTime;
                    } else if (progressTime < finishingTime) {
                        stateType = ReloadState.StateType.TACTICAL_RELOAD_FINISHING;
                        countDown = finishingTime - progressTime;
                    } else {
                        stateType = ReloadState.StateType.NOT_RELOADING;
                        data.reloadTimestamp = -1;
                    }
                }
                if (data.reloadStateType == ReloadState.StateType.EMPTY_RELOAD_FEEDING) {
                    if (stateType == ReloadState.StateType.EMPTY_RELOAD_FINISHING) {
                        if (iGun instanceof AbstractGunItem abstractGunItem && data.currentGunItem != null) {
                            abstractGunItem.doReload(shooter, currentGunItem, true);
                        }
                    }
                }
                if (data.reloadStateType == ReloadState.StateType.TACTICAL_RELOAD_FEEDING) {
                    if (stateType == ReloadState.StateType.TACTICAL_RELOAD_FINISHING) {
                        if (iGun instanceof AbstractGunItem abstractGunItem && data.currentGunItem != null) {
                            abstractGunItem.doReload(shooter, currentGunItem, false);
                        }
                    }
                }
                // 更新换弹状态缓存
                data.reloadStateType = stateType;
                // 返回 tick 结果
                reloadState.setStateType(stateType);
                reloadState.setCountDown(countDown);
                cir.setReturnValue(reloadState);
                cir.cancel();
                return;
            }
        }
    }
}