# Changelog

## [1.4] - 2026-07-20

### 修正
- TaCZ 1.1.8 でリロード/コッキング/武器切替のアニメーション速度が反映されなくなる問題を修正
  - TaCZ 1.1.8 の `default_state_machine.lua` に `PRE_PARALLEL_TRACK_1〜5` が追加され、MAIN_TRACK のトラック番号が 4 → 9 にずれた
  - `DrawAnimationSpeedHandler` / `ReloadAnimationSpeedHandler` がトラック番号 4 を直接指定していたため、速度倍率が未使用トラックに適用され無効化されていた
  - サーバー側の所要時間だけが短縮され、アニメーションが実時間と一致しない状態になっていた
- トラックの特定方法をトラック番号から再生中のアニメーション名（`reload*` / `bolt*` / `draw*`）に変更（AnimationSpeedApplier）
  - `minigun` のように独自トラック構成を持つ銃（MAIN_TRACK = 4 のまま）や、サードパーティ銃MODの独自ステートマシンにも追従する

### 変更
- TaCZ 依存を 1.1.7-hotfix (7401617) から 1.1.8-hotfix (8141310) に更新

### 改善
- 2つのアニメーション速度ハンドラで重複していたトラック走査処理を `AnimationSpeedApplier` に統合

## [1.3] - 2026-03-19

### 修正
- 本番環境でのMixinTransformerErrorクラッシュを修正（TargetBlockMixin）
  - `onProjectileHit`はバニラメソッドのオーバーライドであるため、SRG名へのリマッピングが必要だが、`remap = false`が誤ってアノテーション全体に設定されていた
  - `remap = false`を`@Redirect`から`@At`に移動し、バニラメソッド名のリマッピングを有効化
- ApothicAttributesを任意依存に変更（mods.tomlからmandatory依存を削除）

## [1.2] - 2026-03-13

### 追加
- 標的ブロック（tacz:target）にダメージ属性倍率を反映（TargetBlockMixin）
- ITargetEntity（TargetMinecart等）にダメージ属性倍率を反映（EntityKineticBulletMixin）
- ダメージ倍率計算の共通ユーティリティ（DamageModifierHelper）
- GunRefitScreen（Zキー）にプレイヤー属性効果をステータスバーとして表示
  - TaCZの既存モディファイアバー（ダメージ、反動、精度、RPM、ADS、HS倍率、ノックバック、貫通、移動速度）にプレイヤー属性の効果を反映（DiagramsModifierWrapper）
  - アタッチメントがない場合のフォールバック表示（AttributePropertyModifier）
  - TaCZモディファイアでカバーされないカテゴリ（リロード速度、コッキング速度、マガジン容量、武器切替速度、弾数、バースト速度、弾薬非消費、弾薬回復、装填節約、追加弾薬）を独自バーで表示

### 改善
- 属性取得ヘルパーメソッドをFireModeHelperに統合し、コード重複を削減

## [1.1] - 2026-03-12

### 追加
- 全銃共通属性: ダメージ、リロード速度、コッキング速度、マガジン容量
- 弾薬関連属性: 射撃時弾薬非消費、キル時弾薬回復、リロード時弾薬非消費、追加弾薬
- 精度属性: 腰撃ち精度、ADS精度、射撃モード別精度（フルオート/セミ/バースト）
- ダメージ属性: 腰撃ち/ADSダメージ、射撃モード別ダメージ
- 反動属性: 全般/縦/横反動、ADS/腰撃ち反動（各方向）
- 戦闘属性: ヘッドショット倍率、ノックバック倍率・基本値、貫通数倍率
- 機動性属性: 銃装備時移動速度、ADS移行速度、武器切替速度
- 射撃属性: RPM倍率、弾数倍率（セミ/フルオート/バースト）、バースト速度
- 銃種別属性: 全属性に対してPISTOL/SNIPER/RIFLE/SHOTGUN/SMG/RPG/MG個別の倍率
- CurseForge自動公開（GitHub Actions）

### 修正
- 不要なデバッグログ出力を削除
