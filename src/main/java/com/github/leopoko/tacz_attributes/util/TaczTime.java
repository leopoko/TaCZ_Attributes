package com.github.leopoko.tacz_attributes.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * TaCZ のタイムスタンプ（reloadTimestamp / boltTimestamp / drawTimestamp）と同じ時刻源を返すユーティリティ。
 * <p>
 * TaCZ 1.2.0 からタイムスタンプが {@code System.currentTimeMillis()} ではなく
 * {@code com.tacz.guns.util.time.GunTime.nowMillis()}（アニメーション時計基準の独自時刻）で記録されるようになった。
 * 異なる時刻源で経過時間を計算すると値が桁違いになり、リロードが一瞬で終わる・終わらなくなるため、
 * GunTime が存在すればそれを使い、存在しない（TaCZ 1.1.8 以前）場合は従来通り currentTimeMillis を使う。
 */
public final class TaczTime {

    private static final MethodHandle GUN_TIME_NOW_MILLIS = findGunTime();

    private TaczTime() {}

    private static MethodHandle findGunTime() {
        try {
            Class<?> gunTime = Class.forName("com.tacz.guns.util.time.GunTime");
            return MethodHandles.publicLookup().findStatic(gunTime, "nowMillis", MethodType.methodType(long.class));
        } catch (ReflectiveOperationException | LinkageError e) {
            return null;
        }
    }

    /**
     * TaCZ のタイムスタンプと比較可能な現在時刻（ミリ秒）を返す。
     */
    public static long nowMillis() {
        if (GUN_TIME_NOW_MILLIS != null) {
            try {
                return (long) GUN_TIME_NOW_MILLIS.invokeExact();
            } catch (Throwable ignored) {
                // フォールバック
            }
        }
        return System.currentTimeMillis();
    }
}
