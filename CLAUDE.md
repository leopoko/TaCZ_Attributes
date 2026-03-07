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

## プロジェクト構造

```
src/main/java/com/github/leopoko/tacz_attributes/
├── Tacz_attributes.java          # MODメインクラス (@Mod エントリポイント)
├── Config.java                   # ForgeConfigSpec によるコンフィグ管理
├── GunDamageModifier.java        # 銃ダメージ倍率の適用 (LivingHurtEvent)
├── attribute/
│   ├── CustomAttributes.java     # カスタム属性の定義・登録
│   └── EntityAttributeSetup.java # プレイヤーへの属性バインド
└── mixin/
    ├── LivingEntityReloadMixin.java    # リロード速度の変更 (Mixin)
    └── LivingEntityReloadAccessor.java # privateフィールドアクセス用
```

## カスタム属性

| 属性 | ID | デフォルト | 範囲 | 説明 |
|------|-----|-----------|------|------|
| GUN_DAMAGE | `gun_damage` | 1.0 | 0.0〜1024.0 | 銃弾ダメージの倍率 |
| RELOAD_SPEED | `reload_speed` | 1.0 | 0.1〜20.0 | リロード速度の倍率 |

## MOD ID

`tacz_attributes`

## ビルド

```bash
./gradlew build        # JARをビルド (reobfJar が自動実行される)
./gradlew runClient    # クライアント起動
./gradlew runServer    # サーバー起動
```

## 開発上の注意

- Mixin設定ファイル: `src/main/resources/tacz_attributes.mixins.json`
- MODメタデータ: `src/main/resources/META-INF/mods.toml`
- `gradle.properties` のテンプレート変数が `mods.toml` と `pack.mcmeta` に展開される
- 開発環境でのみデバッグログを出力する（`FMLEnvironment.production` で判定）
- ソースのエンコーディングは UTF-8
