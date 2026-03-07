package com.github.leopoko.tacz_attributes.util;

/**
 * リロード処理中であることを示す ThreadLocal フラグ。
 * <p>
 * LivingEntityReloadMixin の tickReloadState() の HEAD で set() を呼び、
 * RETURN で clear() を呼ぶことで、consumeAmmoFromPlayer() や putAmmoInMagazine() が
 * リロード処理のコンテキストで呼ばれているかどうかを判定する。
 * <p>
 * これにより、リロード時弾薬非消費やリロード時追加弾薬がボルト操作時に誤って発動することを防ぐ。
 */
public final class ReloadFinishingContext {

    private static final ThreadLocal<Boolean> IS_RELOADING = ThreadLocal.withInitial(() -> false);

    private ReloadFinishingContext() {}

    public static void set() {
        IS_RELOADING.set(true);
    }

    public static void clear() {
        IS_RELOADING.remove();
    }

    public static boolean isReloading() {
        return IS_RELOADING.get();
    }
}
