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
├── GunDamageModifier.java        # 銃ダメージ倍率の適用 (EntityHurtByGunEvent)
├── GunKillAmmoRecovery.java     # キル時弾薬回復の適用 (EntityKillByGunEvent)
├── api/
│   └── ISpeedModifiable.java     # アニメーション速度倍率のダックインターフェース
├── attribute/
│   ├── CustomAttributes.java     # カスタム属性の定義・登録
│   ├── EntityAttributeSetup.java # プレイヤーへの属性バインド
│   └── GunType.java              # 銃種enum・銃種別属性の定義
├── client/
│   └── ReloadAnimationSpeedHandler.java # クライアント側リロード/ボルトアニメーション速度同期
├── mixin/
│   ├── GunDataMixin.java                # マガジン容量倍率の適用 (ShooterContext連携)
│   ├── LivingEntityBoltMixin.java       # コッキング速度の変更 (タイムスタンプスケーリング)
│   ├── LivingEntityReloadMixin.java     # リロード速度の変更 (タイムスタンプスケーリング)
│   ├── ModernKineticGunScriptAPIMixin.java # 弾薬非消費・リロード時弾薬非消費・追加弾薬の適用
│   └── ObjectAnimationRunnerMixin.java  # アニメーション速度倍率のMixin
└── util/
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

### 銃種別

ダメージ・リロード速度・コッキング速度・マガジン容量は乗算合成（最終倍率 = 全体 × 銃種別）。
確率・数量系は加算合成（最終値 = min(上限, 全体 + 銃種別)）。

各銃種（PISTOL, SNIPER, RIFLE, SHOTGUN, SMG, RPG, MG）ごとに以下の属性が存在する:

| カテゴリ | 属性サフィックス | 命名例（PISTOL） |
|---------|----------------|-----------------|
| ダメージ | `_damage` | `pistol_damage` |
| リロード速度 | `_reload_speed` | `pistol_reload_speed` |
| コッキング速度 | `_bolt_action_speed` | `pistol_bolt_action_speed` |
| マガジン容量 | `_magazine_capacity` | `pistol_magazine_capacity` |
| 射撃時弾薬非消費確率 | `_ammo_save_chance` | `pistol_ammo_save_chance` |
| キル時弾薬回復確率 | `_ammo_recovery_chance` | `pistol_ammo_recovery_chance` |
| キル時弾薬回復量（固定） | `_ammo_recovery_amount` | `pistol_ammo_recovery_amount` |
| キル時弾薬回復量（割合） | `_ammo_recovery_percent` | `pistol_ammo_recovery_percent` |
| リロード時弾薬非消費確率 | `_reload_ammo_save_chance` | `pistol_reload_ammo_save_chance` |
| リロード時追加弾薬確率 | `_bonus_ammo_chance` | `pistol_bonus_ammo_chance` |
| リロード時追加弾薬量（固定） | `_bonus_ammo_amount` | `pistol_bonus_ammo_amount` |
| リロード時追加弾薬量（割合） | `_bonus_ammo_percent` | `pistol_bonus_ammo_percent` |

銃種別属性のデフォルト値は倍率系がすべて 1.0（変更なし）、確率・数量系が 0.0（発動なし）。
範囲はダメージ: 0.0〜1024.0、速度系: 0.1〜20.0、マガジン容量: 0.1〜100.0、確率: 0.0〜1.0、数量: 0.0〜100.0。
銃種は TaCZ の `GunTabType` に準拠し、`GunType` enum で管理される。

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

## 開発上の注意

- Mixin設定ファイル: `src/main/resources/tacz_attributes.mixins.json`
- MODメタデータ: `src/main/resources/META-INF/mods.toml`
- `gradle.properties` のテンプレート変数が `mods.toml` と `pack.mcmeta` に展開される
- 開発環境でのみデバッグログを出力する（`FMLEnvironment.production` で判定）
- ソースのエンコーディングは UTF-8
- `build/libs/` にJARが残っていると `runClient` 等でモジュール競合（`ResolutionException`）が発生する。`build.gradle` に自動クリーンフックが設定済み
