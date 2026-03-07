package com.github.leopoko.tacz_attributes.util;

import com.github.leopoko.tacz_attributes.attribute.GunType;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * gunId や ItemStack から GunType を解決するユーティリティ。
 */
public final class GunTypeResolver {

    private GunTypeResolver() {}

    /**
     * gunId (ResourceLocation) から GunType を解決する。
     */
    @Nullable
    public static GunType resolve(@Nullable ResourceLocation gunId) {
        if (gunId == null) return null;
        return TimelessAPI.getCommonGunIndex(gunId)
                .map(index -> GunType.fromTypeId(index.getType()))
                .orElse(null);
    }

    /**
     * ItemStack から GunType を解決する。
     */
    @Nullable
    public static GunType resolveFromItem(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        IGun iGun = IGun.getIGunOrNull(stack);
        if (iGun == null) return null;
        ResourceLocation gunId = iGun.getGunId(stack);
        return resolve(gunId);
    }
}
