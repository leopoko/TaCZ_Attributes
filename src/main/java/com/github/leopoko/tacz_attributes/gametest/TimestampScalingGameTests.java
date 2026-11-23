package com.github.leopoko.tacz_attributes.gametest;

import com.github.leopoko.tacz_attributes.Tacz_attributes;
import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.ReloadState;
import com.tacz.guns.api.item.builder.GunItemBuilder;
import com.tacz.guns.entity.shooter.LivingEntityBolt;
import com.tacz.guns.entity.shooter.LivingEntityDrawGun;
import com.tacz.guns.entity.shooter.LivingEntityReload;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.util.time.GunTime;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * リロード / コッキング / 武器切替のタイムスタンプスケーリングを検証する GameTest。
 * <p>
 * 実時間を待たず、タイムスタンプを「N ミリ秒前に開始した」状態に設定してから
 * TaCZ の tick 処理を 1 回呼び、その結果（完了したか・まだ途中か）を確認する。
 * TaCZ 1.2.0 の時刻源変更（GunTime）で起きた「一瞬で終わる / 終わらない」不具合の回帰テスト。
 * <p>
 * 実行: {@code ./gradlew runGameTestServer}（配布用 jar からは除外している）
 */
@GameTestHolder(Tacz_attributes.MODID)
@PrefixGameTestTemplate(false)
public class TimestampScalingGameTests {

    private static final String TEMPLATE = "empty";
    private static final ResourceLocation GLOCK = new ResourceLocation("tacz", "glock_17");
    private static final ResourceLocation AWP = new ResourceLocation("tacz", "ai_awp");

    // ===== リロード =====

    @GameTest(template = TEMPLATE)
    public static void reloadFastDoesNotFinishInstantly(GameTestHelper helper) {
        long cooldown = emptyReloadMs(helper, GLOCK);
        ReloadState state = tickReload(helper, 2.0, 50);
        check(state.getStateType().isReloading(),
                "speed 2.0 / 50ms経過でリロードが完了してしまった（一瞬で終わる不具合）: " + state.getStateType() + " cooldown=" + cooldown);
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void reloadFastFinishesEarly(GameTestHelper helper) {
        long cooldown = emptyReloadMs(helper, GLOCK);
        ReloadState state = tickReload(helper, 2.0, (long) (cooldown * 0.6));
        check(!state.getStateType().isReloading(),
                "speed 2.0 / 所要時間の60%経過でリロードが完了していない: " + state.getStateType());
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void reloadNormalSpeedUnchanged(GameTestHelper helper) {
        long cooldown = emptyReloadMs(helper, GLOCK);
        ReloadState state = tickReload(helper, 1.0, (long) (cooldown * 0.6));
        check(state.getStateType().isReloading(),
                "speed 1.0 / 60%経過でリロードが完了してしまった: " + state.getStateType());
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void reloadSlowStillInProgress(GameTestHelper helper) {
        long cooldown = emptyReloadMs(helper, GLOCK);
        ReloadState state = tickReload(helper, 0.5, (long) (cooldown * 1.5));
        check(state.getStateType().isReloading(),
                "speed 0.5 / 150%経過でリロードが完了してしまった: " + state.getStateType());
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void reloadSlowEventuallyFinishes(GameTestHelper helper) {
        long cooldown = emptyReloadMs(helper, GLOCK);
        ReloadState state = tickReload(helper, 0.5, (long) (cooldown * 2.2));
        check(!state.getStateType().isReloading(),
                "speed 0.5 / 220%経過でもリロードが終わらない（終わらない不具合）: " + state.getStateType());
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void reloadTimestampRestoredAfterTick(GameTestHelper helper) {
        long cooldown = emptyReloadMs(helper, GLOCK);
        Player player = mockPlayer(helper, CustomAttributes.RELOAD_SPEED.get(), 2.0);
        ItemStack gun = equip(player, GunItemBuilder.create().setId(GLOCK).setAmmoCount(0).build());
        ShooterDataHolder data = new ShooterDataHolder();
        data.currentGunItem = () -> gun;
        data.reloadStateType = ReloadState.StateType.EMPTY_RELOAD_FEEDING;
        long original = GunTime.nowMillis() - cooldown / 10;
        data.reloadTimestamp = original;

        new LivingEntityReload(player, data, null, null).tickReloadState();

        check(data.reloadTimestamp == original,
                "途中のリロードでタイムスタンプが元に戻っていない: expected=" + original + " actual=" + data.reloadTimestamp);
        helper.succeed();
    }

    // ===== コッキング =====

    @GameTest(template = TEMPLATE)
    public static void boltFastDoesNotFinishInstantly(GameTestHelper helper) {
        long action = boltActionMs(helper, AWP);
        check(tickBolt(helper, 2.0, 30),
                "speed 2.0 / 30ms経過でコッキングが完了してしまった（一瞬で終わる不具合） action=" + action);
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void boltFastFinishesEarly(GameTestHelper helper) {
        long action = boltActionMs(helper, AWP);
        check(!tickBolt(helper, 2.0, (long) (action * 0.6)),
                "speed 2.0 / 所要時間の60%経過でコッキングが完了していない");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void boltSlowStillInProgress(GameTestHelper helper) {
        long action = boltActionMs(helper, AWP);
        check(tickBolt(helper, 0.5, (long) (action * 1.5)),
                "speed 0.5 / 150%経過でコッキングが完了してしまった");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void boltSlowEventuallyFinishes(GameTestHelper helper) {
        long action = boltActionMs(helper, AWP);
        check(!tickBolt(helper, 0.5, (long) (action * 2.2)),
                "speed 0.5 / 220%経過でもコッキングが終わらない（終わらない不具合）");
        helper.succeed();
    }

    // ===== 武器切替（しまう時間） =====

    @GameTest(template = TEMPLATE)
    public static void drawHolsterTimeScaled(GameTestHelper helper) {
        GunData gunData = gunData(helper, GLOCK);
        long putAwayMs = (long) (gunData.getPutAwayTime() * 1000);
        check(putAwayMs >= 100, "テスト前提: put_away_time が短すぎる (" + putAwayMs + "ms)");

        long normal = holsterOffset(helper, 1.0, gunData.getPutAwayTime());
        long fast = holsterOffset(helper, 2.0, gunData.getPutAwayTime());

        check(Math.abs(normal - putAwayMs) <= 50,
                "speed 1.0 でしまう時間が変わっている: expected≈" + putAwayMs + " actual=" + normal);
        check(Math.abs(fast - putAwayMs / 2) <= 50,
                "speed 2.0 でしまう時間が半分になっていない: expected≈" + (putAwayMs / 2) + " actual=" + fast);
        helper.succeed();
    }

    // ===== ヘルパー =====

    private static ReloadState tickReload(GameTestHelper helper, double speed, long elapsedMs) {
        Player player = mockPlayer(helper, CustomAttributes.RELOAD_SPEED.get(), speed);
        ItemStack gun = equip(player, GunItemBuilder.create().setId(GLOCK).setAmmoCount(0).build());
        ShooterDataHolder data = new ShooterDataHolder();
        data.currentGunItem = () -> gun;
        data.reloadStateType = ReloadState.StateType.EMPTY_RELOAD_FEEDING;
        data.reloadTimestamp = GunTime.nowMillis() - elapsedMs;
        return new LivingEntityReload(player, data, null, null).tickReloadState();
    }

    /** @return tick 後もコッキング中なら true */
    private static boolean tickBolt(GameTestHelper helper, double speed, long elapsedMs) {
        Player player = mockPlayer(helper, CustomAttributes.BOLT_ACTION_SPEED.get(), speed);
        ItemStack gun = equip(player, GunItemBuilder.create().setId(AWP).setAmmoCount(5).setAmmoInBarrel(false).build());
        ShooterDataHolder data = new ShooterDataHolder();
        data.currentGunItem = () -> gun;
        data.isBolting = true;
        data.boltTimestamp = GunTime.nowMillis() - elapsedMs;
        new LivingEntityBolt(data, player, null, null).tickBolt();
        return data.isBolting;
    }

    /** 前の銃を十分前に取り出した状態で draw() し、しまい終わるまでの残り時間(ms)を返す */
    private static long holsterOffset(GameTestHelper helper, double speed, float putAwayTimeS) {
        Player player = mockPlayer(helper, CustomAttributes.DRAW_SPEED.get(), speed);
        ItemStack gun = equip(player, GunItemBuilder.create().setId(GLOCK).setAmmoCount(17).build());
        ShooterDataHolder data = new ShooterDataHolder();
        data.drawTimestamp = GunTime.nowMillis() - 10_000;
        data.currentPutAwayTimeS = putAwayTimeS;
        new LivingEntityDrawGun(player, data).draw(() -> gun);
        return data.drawTimestamp - GunTime.nowMillis();
    }

    private static Player mockPlayer(GameTestHelper helper, Attribute attribute, double value) {
        Player player = helper.makeMockPlayer();
        var instance = player.getAttribute(attribute);
        if (instance == null) {
            throw new GameTestAssertException("プレイヤーに属性が付与されていない: " + attribute.getDescriptionId());
        }
        instance.setBaseValue(value);
        return player;
    }

    private static ItemStack equip(Player player, ItemStack gun) {
        player.setItemInHand(InteractionHand.MAIN_HAND, gun);
        return gun;
    }

    private static GunData gunData(GameTestHelper helper, ResourceLocation gunId) {
        return TimelessAPI.getCommonGunIndex(gunId)
                .map(index -> index.getGunData())
                .orElseThrow(() -> new GameTestAssertException("銃データが読み込まれていない: " + gunId));
    }

    private static long emptyReloadMs(GameTestHelper helper, ResourceLocation gunId) {
        long ms = (long) (gunData(helper, gunId).getReloadData().getCooldown().getEmptyTime() * 1000);
        check(ms >= 500, "テスト前提: リロード時間が短すぎる (" + ms + "ms)");
        return ms;
    }

    private static long boltActionMs(GameTestHelper helper, ResourceLocation gunId) {
        long ms = (long) (gunData(helper, gunId).getBoltActionTime() * 1000);
        check(ms >= 300, "テスト前提: コッキング時間が短すぎる (" + ms + "ms)");
        return ms;
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new GameTestAssertException(message);
        }
    }
}
