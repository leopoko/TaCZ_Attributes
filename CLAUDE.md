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

- **TaCZ** (Timeless and Classics Zero) - 銃MOD本体 (ローカルビルド、JarJar付き`-all.jar`)
- **ApothicAttributes** - 属性拡張ライブラリ
- **Placebo** - ApothicAttributesの前提ライブラリ

## TaCZ JARの更新手順

TaCZを更新する場合は、**必ずJarJar付きの`-all.jar`を使用**すること。

```bash
# 1. TaCZソースディレクトリでJarJarタスクを実行
cd /path/to/TACZ
./gradlew jarJar

# 2. 生成された-all.jarをlibsにコピー（ファイル名は-allを除く）
cp build/libs/tacz-1.20.1-X.X.X-all.jar /path/to/TaCZ_Attributes/libs/tacz-1.20.1-X.X.X.jar

# 3. Gradle deobfキャッシュを削除して再生成させる
rm -rf ~/.gradle/caches/forge_gradle/deobf_dependencies/libs/tacz-1.20.1/
```

**重要**: 通常の`./gradlew build`で生成されるJARにはJarJar依存（LuaJ, MixinExtras,
commons-math3, bcel）が含まれない。`-all.jar`を使わないと開発環境で
`ClassNotFoundException`/`NoClassDefFoundError` が発生する。

## プロジェクト構造

```
src/main/java/com/github/leopoko/tacz_attributes/
├── Tacz_attributes.java          # MODメインクラス (@Mod エントリポイント)
├── GunDamageModifier.java        # 銃ダメージ倍率の適用 (EntityHurtByGunEvent)
├── attribute/
│   ├── CustomAttributes.java     # カスタム属性の定義・登録
│   └── EntityAttributeSetup.java # プレイヤーへの属性バインド
└── mixin/
    └── LivingEntityReloadMixin.java # リロード速度の変更 (タイムスタンプスケーリング)
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
