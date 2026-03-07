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
├── api/
│   └── ISpeedModifiable.java     # アニメーション速度倍率のダックインターフェース
├── attribute/
│   ├── CustomAttributes.java     # カスタム属性の定義・登録
│   ├── EntityAttributeSetup.java # プレイヤーへの属性バインド
│   └── GunType.java              # 銃種enum・銃種別属性の定義
├── client/
│   └── ReloadAnimationSpeedHandler.java # クライアント側リロードアニメーション速度同期
├── mixin/
│   ├── LivingEntityReloadMixin.java     # リロード速度の変更 (タイムスタンプスケーリング)
│   └── ObjectAnimationRunnerMixin.java  # アニメーション速度倍率のMixin
└── util/
    └── GunTypeResolver.java      # gunId/ItemStack → GunType 解決ユーティリティ
```

## カスタム属性

### 全銃共通

| 属性 | ID | デフォルト | 範囲 | 説明 |
|------|-----|-----------|------|------|
| GUN_DAMAGE | `gun_damage` | 1.0 | 0.0〜1024.0 | 全銃弾ダメージの倍率 |
| RELOAD_SPEED | `reload_speed` | 1.0 | 0.1〜20.0 | 全銃リロード速度の倍率 |

### 銃種別（最終倍率 = 全体 × 銃種別）

| 銃種 | ダメージ属性 | リロード速度属性 |
|------|------------|----------------|
| PISTOL | `pistol_damage` | `pistol_reload_speed` |
| SNIPER | `sniper_damage` | `sniper_reload_speed` |
| RIFLE | `rifle_damage` | `rifle_reload_speed` |
| SHOTGUN | `shotgun_damage` | `shotgun_reload_speed` |
| SMG | `smg_damage` | `smg_reload_speed` |
| RPG | `rpg_damage` | `rpg_reload_speed` |
| MG | `mg_damage` | `mg_reload_speed` |

銃種別属性のデフォルト値はすべて 1.0（変更なし）、範囲はダメージ: 0.0〜1024.0、リロード速度: 0.1〜20.0。
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
