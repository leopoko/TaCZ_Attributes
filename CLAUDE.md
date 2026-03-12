# TaCZ Attributes

Minecraft Forge 1.20.1 用のMODプロジェクト。TaCZ（Timeless and Classics Zero）銃MODにRPG的な属性システムを追加する。

## 言語・コミュニケーション

- 返信、コミットメッセージ、プルリクエストはすべて **日本語** で行うこと

## 技術スタック

- **Java 17** / **Minecraft Forge 1.20.1** (47.3.10)
- **Mixin** (SpongePowered 0.8.5) によるコードインジェクション
- **Gradle** ビルドシステム (ForgeGradle 6.0.16+)
- マッピング: official (Mojang mappings)

## 主な依存MOD

- **TaCZ** (Timeless and Classics Zero) - 銃MOD本体 (CurseMaven経由)
- **ApothicAttributes** - 属性拡張ライブラリ
- **Placebo** - ApothicAttributesの前提ライブラリ

## TaCZ依存の管理

TaCZはCurseMavenから取得する。CurseForgeの公開JARにはJarJar依存（LuaJ, MixinExtras,
commons-math3, bcel）が含まれており、開発環境でもForgeのJarInJarロケーターが
自動的に展開・ロードする。

```gradle
// build.gradle の dependencies ブロック
implementation fg.deobf("curse.maven:timeless-and-classics-zero-1028108:<fileId>")
```

**TaCZバージョン更新時**: CurseForgeのファイルIDを更新する。
ファイルIDは https://www.curseforge.com/minecraft/mc-mods/timeless-and-classics-zero/files
から確認できる。

**重要**: ローカルビルドのJARを`libs/`に置く方式は使わないこと。
ローカルJARではJarJar依存がForgeのクラスローダーに正しくロードされず、
`ClassNotFoundException`/`NoClassDefFoundError` が発生する。

## プロジェクト構造

```
src/main/java/com/github/leopoko/tacz_attributes/
├── Tacz_attributes.java          # MODメインクラス (@Mod エントリポイント)
├── GunDamageModifier.java        # 銃ダメージ・ヘッドショット倍率の適用 (EntityHurtByGunEvent)
├── GunKillAmmoRecovery.java     # キル時弾薬回復の適用 (EntityKillByGunEvent)
├── GunMovementSpeedHandler.java  # 銃装備時移動速度の適用 (TickEvent)
├── api/
│   └── ISpeedModifiable.java     # アニメーション速度倍率のダックインターフェース
├── attribute/
│   ├── CustomAttributes.java     # カスタム属性の定義・登録
│   ├── EntityAttributeSetup.java # プレイヤーへの属性バインド
│   └── GunType.java              # 銃種enum・銃種別属性の定義
├── client/
│   ├── DrawAnimationSpeedHandler.java   # クライアント側drawアニメーション速度同期
│   └── ReloadAnimationSpeedHandler.java # クライアント側リロード/ボルトアニメーション速度同期
├── mixin/
│   ├── GunDataMixin.java                # マガジン容量倍率の適用 (ShooterContext連携)
│   ├── LivingEntityBoltMixin.java       # コッキング速度の変更 (タイムスタンプスケーリング)
│   ├── LivingEntityReloadMixin.java     # リロード速度の変更 (タイムスタンプスケーリング)
│   ├── ModernKineticGunScriptAPIMixin.java # 弾薬非消費・リロード時弾薬非消費・追加弾薬の適用
│   ├── EntityKineticBulletMixin.java    # ノックバック・貫通数属性の適用 (EntityKineticBullet)
│   ├── LivingEntityAimMixin.java        # ADS移行速度属性の適用 (LivingEntityAim, サーバー側)
│   ├── LocalPlayerAimMixin.java         # ADS移行速度属性の適用 (LocalPlayerAim, クライアント側)
│   ├── ObjectAnimationRunnerMixin.java  # アニメーション速度倍率のMixin
│   ├── RecoilMixin.java                 # 反動属性の適用 (CameraSetupEvent, クライアント側)
│   ├── BulletAmountMixin.java            # 弾数・バースト速度属性の適用 (ModernKineticGunScriptAPI, サーバー側)
│   ├── LocalPlayerShootMixin.java       # バースト弾数・速度のクライアント側同期 (LocalPlayerShoot)
│   ├── ShootInaccuracyMixin.java        # 腰撃ち/ADS精度属性の適用 (ModernKineticGunScriptAPI)
│   ├── ShootIntervalMixin.java          # 発射レート(RPM)属性の適用 (GunData)
│   ├── LivingEntityDrawGunMixin.java    # 武器切替速度属性の適用 (LivingEntityDrawGun, サーバー側)
│   └── GunItemRendererWrapperMixin.java # 武器切替アニメーション速度の適用 (GunItemRendererWrapper, クライアント側)
└── util/
    ├── FireModeHelper.java       # FireMode別属性の解決ユーティリティ
    ├── GunTypeResolver.java      # gunId/ItemStack → GunType 解決ユーティリティ
    ├── ReloadFinishingContext.java # リロード処理中フラグ管理 (ThreadLocal)
    └── ShooterContext.java       # ThreadLocalによるプレイヤーコンテキスト管理
```

## カスタム属性

### 全銃共通

| 属性 | ID | デフォルト | 範囲 | 説明 |
|------|-----|-----------|------|------|
| GUN_DAMAGE | `gun_damage` | 1.0 | 0.0〜1024.0 | 全銃弾ダメージの倍率 |
| RELOAD_SPEED | `reload_speed` | 1.0 | 0.1〜20.0 | 全銃リロード速度の倍率 |
| BOLT_ACTION_SPEED | `bolt_action_speed` | 1.0 | 0.1〜20.0 | 全銃コッキング速度の倍率 |
| MAGAZINE_CAPACITY | `magazine_capacity` | 1.0 | 0.1〜100.0 | 全銃マガジン容量の倍率 |
| AMMO_SAVE_CHANCE | `ammo_save_chance` | 0.0 | 0.0〜1.0 | 射撃時弾薬を消費しない確率 |
| AMMO_RECOVERY_CHANCE | `ammo_recovery_chance` | 0.0 | 0.0〜1.0 | キル時に弾薬が回復する確率 |
| AMMO_RECOVERY_AMOUNT | `ammo_recovery_amount` | 0.0 | 0.0〜100.0 | キル時に回復する弾薬の固定数 |
| AMMO_RECOVERY_PERCENT | `ammo_recovery_percent` | 0.0 | 0.0〜1.0 | キル時に回復する弾薬のマガジン容量比率 |
| RELOAD_AMMO_SAVE_CHANCE | `reload_ammo_save_chance` | 0.0 | 0.0〜1.0 | リロード時にインベントリ弾薬を消費しない確率 |
| BONUS_AMMO_CHANCE | `bonus_ammo_chance` | 0.0 | 0.0〜1.0 | リロード完了時に追加弾薬が装填される確率 |
| BONUS_AMMO_AMOUNT | `bonus_ammo_amount` | 0.0 | 0.0〜100.0 | 追加装填される弾薬の固定数 |
| BONUS_AMMO_PERCENT | `bonus_ammo_percent` | 0.0 | 0.0〜1.0 | 追加装填される弾薬のマガジン容量比率 |
| HIP_FIRE_ACCURACY | `hip_fire_accuracy` | 1.0 | 0.01〜100.0 | 腰撃ち精度倍率（高い値=高精度） |
| ADS_ACCURACY | `ads_accuracy` | 1.0 | 0.01〜100.0 | ADS精度倍率（高い値=高精度） |
| HIP_FIRE_DAMAGE | `hip_fire_damage` | 1.0 | 0.0〜1024.0 | 腰撃ちダメージ倍率 |
| ADS_DAMAGE | `ads_damage` | 1.0 | 0.0〜1024.0 | ADSダメージ倍率 |
| AUTO_DAMAGE | `auto_damage` | 1.0 | 0.0〜1024.0 | フルオートダメージ倍率 |
| SEMI_DAMAGE | `semi_damage` | 1.0 | 0.0〜1024.0 | セミオートダメージ倍率 |
| BURST_DAMAGE | `burst_damage` | 1.0 | 0.0〜1024.0 | バーストダメージ倍率 |
| AUTO_ACCURACY | `auto_accuracy` | 1.0 | 0.01〜100.0 | フルオート精度倍率 |
| SEMI_ACCURACY | `semi_accuracy` | 1.0 | 0.01〜100.0 | セミオート精度倍率 |
| BURST_ACCURACY | `burst_accuracy` | 1.0 | 0.01〜100.0 | バースト精度倍率 |
| RECOIL | `recoil` | 1.0 | 0.0〜100.0 | 全般反動倍率 |
| VERTICAL_RECOIL | `vertical_recoil` | 1.0 | 0.0〜100.0 | 縦反動倍率 |
| HORIZONTAL_RECOIL | `horizontal_recoil` | 1.0 | 0.0〜100.0 | 横反動倍率 |
| ADS_RECOIL | `ads_recoil` | 1.0 | 0.0〜100.0 | ADS反動倍率 |
| ADS_VERTICAL_RECOIL | `ads_vertical_recoil` | 1.0 | 0.0〜100.0 | ADS縦反動倍率 |
| ADS_HORIZONTAL_RECOIL | `ads_horizontal_recoil` | 1.0 | 0.0〜100.0 | ADS横反動倍率 |
| HIP_FIRE_RECOIL | `hip_fire_recoil` | 1.0 | 0.0〜100.0 | 腰撃ち反動倍率 |
| HIP_FIRE_VERTICAL_RECOIL | `hip_fire_vertical_recoil` | 1.0 | 0.0〜100.0 | 腰撃ち縦反動倍率 |
| HIP_FIRE_HORIZONTAL_RECOIL | `hip_fire_horizontal_recoil` | 1.0 | 0.0〜100.0 | 腰撃ち横反動倍率 |
| GUN_MOVEMENT_SPEED | `gun_movement_speed` | 1.0 | 0.01〜10.0 | 銃装備時移動速度倍率 |
| HEADSHOT_MULTIPLIER | `headshot_multiplier` | 1.0 | 0.0〜100.0 | ヘッドショット倍率 |
| KNOCKBACK_MULTIPLIER | `knockback_multiplier` | 1.0 | 0.0〜100.0 | ノックバック倍率 |
| KNOCKBACK_BASE | `knockback_base` | 0.0 | 0.0〜100.0 | ノックバック基本値（加算） |
| PIERCE_MULTIPLIER | `pierce_multiplier` | 1.0 | 0.01〜100.0 | 貫通数倍率 |
| RPM_MULTIPLIER | `rpm_multiplier` | 1.0 | 0.01〜10.0 | 発射レート(RPM)倍率 |
| ADS_SPEED | `ads_speed` | 1.0 | 0.01〜10.0 | ADS移行速度倍率 |
| SEMI_BULLET_AMOUNT | `semi_bullet_amount` | 1.0 | 0.01〜100.0 | セミオート弾数倍率 |
| AUTO_BULLET_AMOUNT | `auto_bullet_amount` | 1.0 | 0.01〜100.0 | フルオート弾数倍率 |
| BURST_BULLET_AMOUNT | `burst_bullet_amount` | 1.0 | 0.01〜100.0 | バースト弾数倍率 |
| DRAW_SPEED | `draw_speed` | 1.0 | 0.01〜10.0 | 武器切替速度倍率（取り出し・しまい両方） |
| BURST_SPEED | `burst_speed` | 1.0 | 0.01〜10.0 | バースト内射撃間隔速度倍率（高い値=速いバースト） |

### 銃種別

ダメージ・リロード速度・コッキング速度・マガジン容量は乗算合成（最終倍率 = 全体 × 銃種別）。
確率・数量系は加算合成（最終値 = min(上限, 全体 + 銃種別)）。

各銃種（PISTOL, SNIPER, RIFLE, SHOTGUN, SMG, RPG, MG）に以下の属性が存在する:

| カテゴリ | 属性サフィックス | 範囲 |
|---------|----------------|------|
| ダメージ | `_damage` | 0.0〜1024.0 |
| リロード速度 | `_reload_speed` | 0.1〜20.0 |
| コッキング速度 | `_bolt_action_speed` | 0.1〜20.0 |
| マガジン容量 | `_magazine_capacity` | 0.1〜100.0 |
| 射撃時弾薬非消費確率 | `_ammo_save_chance` | 0.0〜1.0 |
| キル時弾薬回復確率 | `_ammo_recovery_chance` | 0.0〜1.0 |
| キル時弾薬回復量（固定） | `_ammo_recovery_amount` | 0.0〜100.0 |
| キル時弾薬回復量（割合） | `_ammo_recovery_percent` | 0.0〜1.0 |
| リロード時弾薬非消費確率 | `_reload_ammo_save_chance` | 0.0〜1.0 |
| リロード時追加弾薬確率 | `_bonus_ammo_chance` | 0.0〜1.0 |
| リロード時追加弾薬量（固定） | `_bonus_ammo_amount` | 0.0〜100.0 |
| リロード時追加弾薬量（割合） | `_bonus_ammo_percent` | 0.0〜1.0 |
| 腰撃ち精度 | `_hip_fire_accuracy` | 0.01〜100.0 |
| ADS精度 | `_ads_accuracy` | 0.01〜100.0 |
| 腰撃ちダメージ | `_hip_fire_damage` | 0.0〜1024.0 |
| ADSダメージ | `_ads_damage` | 0.0〜1024.0 |
| フルオートダメージ | `_auto_damage` | 0.0〜1024.0 |
| セミオートダメージ | `_semi_damage` | 0.0〜1024.0 |
| バーストダメージ | `_burst_damage` | 0.0〜1024.0 |
| フルオート精度 | `_auto_accuracy` | 0.01〜100.0 |
| セミオート精度 | `_semi_accuracy` | 0.01〜100.0 |
| バースト精度 | `_burst_accuracy` | 0.01〜100.0 |
| 全般反動 | `_recoil` | 0.0〜100.0 |
| 縦反動 | `_vertical_recoil` | 0.0〜100.0 |
| 横反動 | `_horizontal_recoil` | 0.0〜100.0 |
| ADS反動 | `_ads_recoil` | 0.0〜100.0 |
| ADS縦反動 | `_ads_vertical_recoil` | 0.0〜100.0 |
| ADS横反動 | `_ads_horizontal_recoil` | 0.0〜100.0 |
| 腰撃ち反動 | `_hip_fire_recoil` | 0.0〜100.0 |
| 腰撃ち縦反動 | `_hip_fire_vertical_recoil` | 0.0〜100.0 |
| 腰撃ち横反動 | `_hip_fire_horizontal_recoil` | 0.0〜100.0 |
| 移動速度 | `_gun_movement_speed` | 0.01〜10.0 |
| ヘッドショット倍率 | `_headshot_multiplier` | 0.0〜100.0 |
| ノックバック倍率 | `_knockback_multiplier` | 0.0〜100.0 |
| ノックバック基本値 | `_knockback_base` | 0.0〜100.0 |
| 貫通数倍率 | `_pierce_multiplier` | 0.01〜100.0 |
| RPM倍率 | `_rpm_multiplier` | 0.01〜10.0 |
| ADS速度 | `_ads_speed` | 0.01〜10.0 |
| セミオート弾数 | `_semi_bullet_amount` | 0.01〜100.0 |
| フルオート弾数 | `_auto_bullet_amount` | 0.01〜100.0 |
| バースト弾数 | `_burst_bullet_amount` | 0.01〜100.0 |
| 武器切替速度 | `_draw_speed` | 0.01〜10.0 |
| バースト速度 | `_burst_speed` | 0.01〜10.0 |

例: `pistol_auto_damage`, `sniper_semi_accuracy`, `rifle_vertical_recoil`, `smg_rpm_multiplier` 等。
銃種別属性のデフォルト値は倍率系がすべて 1.0（変更なし）、確率・数量系が 0.0（発動なし）。
銃種は TaCZ の `GunTabType` に準拠し、`GunType` enum で管理される。

### 精度属性の計算式

`最終inaccuracy = 基本inaccuracy / (全体精度 × 銃種別精度 × モード全体精度 × モード銃種別精度)`

- 精度 2.0 → inaccuracy が半分（より高精度）
- 精度 0.5 → inaccuracy が2倍（より低精度）
- ADS中は `ads_accuracy` / 腰撃ち時は `hip_fire_accuracy` が適用される
- 射撃モードに応じて `auto_accuracy` / `semi_accuracy` / `burst_accuracy` が適用される

### ダメージ属性の計算式

`最終ダメージ = 基本ダメージ × gun_damage × 銃種別damage × (ads/hip)ダメージ × 銃種別(ads/hip) × モードダメージ × 銃種別モードダメージ`

- ADS中は `ads_damage` / 腰撃ち時は `hip_fire_damage` が適用される
- 射撃モードに応じて `auto_damage` / `semi_damage` / `burst_damage` が適用される
- ADS判定は弾着時の `IGunOperator.getSynIsAiming()` で行う
- FireMode判定は `IGun.getFireMode(ItemStack)` で行う

### 反動属性の計算式

`pitch_modifier = TaCZ元modifier × 全般反動 × 縦反動 × (ADS/腰撃ち)反動 × (ADS/腰撃ち)縦反動 × 銃種全般反動 × 銃種縦反動 × 銃種(ADS/腰撃ち)反動 × 銃種(ADS/腰撃ち)縦反動`

`yaw_modifier = TaCZ元modifier × 全般反動 × 横反動 × (ADS/腰撃ち)反動 × (ADS/腰撃ち)横反動 × 銃種全般反動 × 銃種横反動 × 銃種(ADS/腰撃ち)反動 × 銃種(ADS/腰撃ち)横反動`

- 反動 0.5 → リコイルが半分
- 反動 2.0 → リコイルが2倍
- 反動 0.0 → 反動なし
- ADS判定は `IGunOperator.getSynIsAiming()` で行う
- CameraSetupEvent.initialCameraRecoil() 内の genPitchSplineFunction / genYawSplineFunction を @ModifyArg で適用

### ヘッドショット倍率の計算式

`最終HS倍率 = TaCZ元HS倍率 × headshot_multiplier × 銃種別headshot_multiplier`

- ヘッドショット倍率 2.0 → HSダメージ2倍
- EntityHurtByGunEvent.Pre 内で setHeadshotMultiplier() により適用

### 移動速度の計算式

`最終移動速度 = TaCZ処理後の速度 × (gun_movement_speed × 銃種別gun_movement_speed)`

- 銃を持っている場合のみ適用
- MULTIPLY_TOTAL の AttributeModifier として MOVEMENT_SPEED に追加
- TaCZの重量ペナルティ・ADS/リロード減速の上に乗算される

### ノックバックの計算式

`最終ノックバック = (TaCZ元knockback + knockback_base + 銃種別knockback_base) × knockback_multiplier × 銃種別knockback_multiplier`

- TaCZの銃はデフォルトでknockback=0のため、`knockback_base` で基本値を加算する必要がある
- knockback_base 0.4 → バニラ攻撃相当のノックバックを追加
- knockback_multiplier 2.0 → ノックバック2倍
- EntityKineticBullet.onHitEntity() 内の KnockBackModifier.setKnockBackStrength() を @ModifyArg で適用

### 貫通数の計算式

`最終貫通数 = max(1, (int)(TaCZ元pierce × pierce_multiplier × 銃種別pierce_multiplier))`

- 貫通数 2.0 → 貫通数2倍（小数点以下切り捨て、最低1）
- EntityKineticBullet コンストラクタ末尾で @Inject して適用

### 発射レート(RPM)の計算式

`最終射撃間隔 = max(1, TaCZ元インターバル / (rpm_multiplier × 銃種別rpm_multiplier))`

- RPM倍率 2.0 → 射撃間隔半分 → 2倍速射
- RPM倍率 0.5 → 射撃間隔2倍 → 半分の速度
- GunData.getShootInterval() の戻り値を @Inject(RETURN) で変更

### ADS移行速度の計算式

`最終aimTime = Math.max(0, aimTime) / (ads_speed × 銃種別ads_speed)`

- ADS速度 2.0 → ADS移行時間半分 → 2倍速でエイム
- ADS速度 0.5 → ADS移行時間2倍 → 遅いエイム
- サーバー側: LivingEntityAim.tickAimingProgress() 内の Math.max(0, aimTime) を @Redirect して適用
- クライアント側: LocalPlayerAim.getAlphaProgress() 内の Math.max(0, aimTime) を @Redirect して適用（ADSアニメーション）

### 弾数倍率の計算式

セミオート/フルオート:
`最終bulletAmount = max(1, (int)(bulletAmount × semi/auto_bullet_amount × 銃種別semi/auto_bullet_amount))`

バースト:
`最終cycles = max(1, (int)(cycles × burst_bullet_amount × 銃種別burst_bullet_amount))`

- セミオート弾数 2.0 → 1発のトリガーで発射される弾丸数が2倍
- フルオート弾数 2.0 → 1ティックで発射される弾丸数が2倍
- バースト弾数 2.0 → バーストで発射される弾数が2倍（3点バースト → 6点バースト）
- サーバー側: ModernKineticGunScriptAPI.shootOnce() 内の bulletAmount/cycles を @ModifyVariable で変更
- クライアント側: LocalPlayerShoot.doShoot() 内の cycles を @ModifyVariable で変更（反動・音声の同期）

### バースト速度の計算式

`最終burstShootInterval = TaCZ元interval / (burst_speed × 銃種別burst_speed)`

- burst_speed 2.0 → バースト内の射撃間隔が半分 → より速いバースト
- burst_speed 0.5 → バースト内の射撃間隔が2倍 → より遅いバースト
- バーストモード時のみ適用
- サーバー側: ModernKineticGunScriptAPI.shootOnce() 内の burstShootInterval (long ordinal 0) を @ModifyVariable で変更
- クライアント側: LocalPlayerShoot.doShoot() 内の burstShootInterval (long ordinal 0) を @ModifyVariable で変更（音声・反動タイミングに反映）

### 武器切替速度の計算式

`最終holster時間 = TaCZ元putAwayTime / (draw_speed × 銃種別draw_speed)`
`最終draw時間 = TaCZ元drawTime / (draw_speed × 銃種別draw_speed)`

- draw_speed 2.0 → 取り出し・しまい時間が半分 → 2倍速で武器切替
- draw_speed 0.5 → 取り出し・しまい時間が2倍 → 半分の速度
- 銃種は切り替え先の武器（メインハンド）で判定
- サーバー側: LivingEntityDrawGun.draw() の TAIL で drawTimestamp をスケーリング + getDrawCoolDown() の RETURN でdraw時間デルタを減算
- クライアント側: GunItemRendererWrapper.getPutAwayTime() の戻り値をスケーリング（アニメーション・音声タイミングに反映）

## MOD ID

`tacz_attributes`

## ビルド

```bash
./gradlew build           # JARをビルド (reobfJar が自動実行される)
./gradlew runClient       # クライアント起動 (Gradleから)
./gradlew runServer       # サーバー起動
./gradlew genIntellijRuns # IntelliJのrun設定を生成
```

## 開発環境セットアップ

クローン後またはブランチ切り替え後、IntelliJの再生ボタンで起動するには:

```bash
./gradlew genIntellijRuns
```

を実行する必要がある。これにより `.idea/runConfigurations/` にrun設定が生成される。
この設定は `.gitignore` に含まれておりコミットされないため、各開発者がローカルで実行する。

### バージョン更新手順
1. `gradle.properties` の `mod_version` を新バージョンに変更（例: `1.2+1.21.1` → `1.3+1.21.1`）
2. `CHANGELOG.md` の先頭に新バージョンのセクションを追加（`## [x.x]` 形式）
3. ビルド確認: `./gradlew build`

## 開発上の注意

- Mixin設定ファイル: `src/main/resources/tacz_attributes.mixins.json`
- MODメタデータ: `src/main/resources/META-INF/mods.toml`
- `gradle.properties` のテンプレート変数が `mods.toml` と `pack.mcmeta` に展開される
- 開発環境でのみデバッグログを出力する（`FMLEnvironment.production` で判定）
- ソースのエンコーディングは UTF-8
- `build/libs/` にJARが残っていると `runClient` 等でモジュール競合（`ResolutionException`）が発生する。`build.gradle` に自動クリーンフックが設定済み
