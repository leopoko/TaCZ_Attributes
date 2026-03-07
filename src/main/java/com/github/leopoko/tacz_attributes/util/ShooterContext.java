package com.github.leopoko.tacz_attributes.util;

import com.github.leopoko.tacz_attributes.attribute.GunType;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/**
 * ThreadLocal を使って現在の操作プレイヤーと銃種をコンテキストとして保持するユーティリティ。
 * <p>
 * GunData.getAmmoAmount() などの共有データにプレイヤー固有の属性倍率を適用するために使用する。
 * Mixin の HEAD で set() を呼び、RETURN で clear() を呼ぶことで安全にコンテキストを管理する。
 */
public final class ShooterContext {

    private static final ThreadLocal<ContextData> CONTEXT = new ThreadLocal<>();

    private ShooterContext() {}

    /**
     * コンテキストを設定する。プレイヤーのメインハンドから銃種を自動解決する。
     */
    public static void set(LivingEntity shooter) {
        GunType gunType = GunTypeResolver.resolveFromItem(shooter.getMainHandItem());
        CONTEXT.set(new ContextData(shooter, gunType));
    }

    /**
     * コンテキストをクリアする。必ず finally ブロックや RETURN インジェクションで呼ぶこと。
     */
    public static void clear() {
        CONTEXT.remove();
    }

    /**
     * 現在のコンテキストを取得する。設定されていない場合は null。
     */
    @Nullable
    public static ContextData get() {
        return CONTEXT.get();
    }

    public record ContextData(LivingEntity shooter, @Nullable GunType gunType) {}
}
