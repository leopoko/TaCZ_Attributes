# TaCZ Attributes

**Minecraft Forge 1.20.1** | **License: MIT**

---

## English

### Overview

**TaCZ Attributes** is an addon mod for [Timeless and Classics Zero (TaCZ)](https://www.curseforge.com/minecraft/mc-mods/timeless-and-classics-zero) that brings an RPG-style attribute system to firearms. Customize every aspect of gun behavior — damage, accuracy, recoil, fire rate, movement speed, and more — through Minecraft's native attribute system.

This means you can modify gun stats using **commands**, **equipment (curios/trinkets)**, **enchantments**, **potions**, or any other mod that interacts with vanilla attributes. Perfect for RPG servers, progression systems, and custom modpacks.

### Features

**33 global attributes** that apply to all guns, plus **33 per-gun-type attributes** for each of the 7 gun types (Pistol, Sniper, Rifle, Shotgun, SMG, RPG, MG) — totaling **264 customizable attributes**.

#### Damage & Combat
- **Gun Damage** — Global bullet damage multiplier
- **Hip Fire / ADS Damage** — Damage multiplier based on aiming state
- **Semi / Auto / Burst Damage** — Damage multiplier based on fire mode
- **Headshot Multiplier** — Scale headshot bonus damage
- **Knockback Multiplier & Base** — Add and scale bullet knockback
- **Pierce Multiplier** — Scale bullet penetration count

#### Accuracy & Recoil
- **Hip Fire / ADS Accuracy** — Accuracy based on aiming state
- **Semi / Auto / Burst Accuracy** — Accuracy based on fire mode
- **Recoil (General / Vertical / Horizontal)** — Overall recoil control
- **ADS Recoil (General / Vertical / Horizontal)** — Recoil while aiming
- **Hip Fire Recoil (General / Vertical / Horizontal)** — Recoil while hip firing

#### Speed & Handling
- **Reload Speed** — Reload animation speed multiplier
- **Bolt Action Speed** — Cocking/bolt action speed multiplier
- **RPM Multiplier** — Fire rate multiplier
- **ADS Speed** — Aim-down-sight transition speed
- **Draw Speed** — Weapon switch speed (both draw and holster)
- **Burst Speed** — Interval between shots in a burst
- **Gun Movement Speed** — Movement speed while holding a gun

#### Ammo & Magazine
- **Magazine Capacity** — Magazine size multiplier
- **Semi / Auto / Burst Bullet Amount** — Bullets per shot/burst multiplier
- **Ammo Save Chance** — Chance to not consume ammo when firing
- **Ammo Recovery Chance / Amount / Percent** — Recover ammo on kill
- **Reload Ammo Save Chance** — Chance to not consume inventory ammo on reload
- **Bonus Ammo Chance / Amount / Percent** — Extra ammo loaded on reload

#### Gun Type System

All attributes above also exist for each gun type. The final value is calculated as:

`Final Value = Global Attribute × Gun Type Attribute`

For example, setting `sniper_damage = 1.5` and `gun_damage = 1.2` results in sniper rifles dealing `1.8×` damage.

### Usage Example

```
/attribute @p tacz_attributes:gun_damage base set 2.0
/attribute @p tacz_attributes:sniper_headshot_multiplier base set 3.0
/attribute @p tacz_attributes:rifle_recoil base set 0.5
```

### Dependencies

- **Required**: [Timeless and Classics Zero (TaCZ)](https://www.curseforge.com/minecraft/mc-mods/timeless-and-classics-zero)
- **Recommended**: [Apothic Attributes](https://www.curseforge.com/minecraft/mc-mods/apothic-attributes) — Allows attribute values beyond vanilla limits

---

## 日本語

### 概要

**TaCZ Attributes** は [Timeless and Classics Zero (TaCZ)](https://www.curseforge.com/minecraft/mc-mods/timeless-and-classics-zero) のアドオンMODで、銃にRPG的な属性システムを追加します。ダメージ、精度、反動、発射レート、移動速度など、銃のあらゆる挙動をMinecraftの属性システムで制御できます。

つまり、**コマンド**、**装備品（Curios/トリンケット）**、**エンチャント**、**ポーション**など、バニラ属性と連携するあらゆるMODで銃のステータスを変更できます。RPGサーバー、成長システム、カスタムMODパックに最適です。

### 特徴

**33種の全銃共通属性** と、7銃種（ピストル、スナイパー、ライフル、ショットガン、SMG、RPG、MG）ごとに**33種の銃種別属性** — 合計 **264のカスタマイズ可能な属性**。

#### ダメージ・戦闘
- **銃ダメージ** — 全弾丸ダメージ倍率
- **腰撃ち / ADS ダメージ** — エイム状態別ダメージ倍率
- **セミ / フルオート / バースト ダメージ** — 射撃モード別ダメージ倍率
- **ヘッドショット倍率** — ヘッドショットボーナスダメージの倍率
- **ノックバック倍率・基本値** — 弾丸ノックバックの追加・倍率
- **貫通数倍率** — 弾丸貫通数の倍率

#### 精度・反動
- **腰撃ち / ADS 精度** — エイム状態別の精度
- **セミ / フルオート / バースト 精度** — 射撃モード別の精度
- **反動（全般 / 縦 / 横）** — 全般的なリコイル制御
- **ADS反動（全般 / 縦 / 横）** — ADS中のリコイル
- **腰撃ち反動（全般 / 縦 / 横）** — 腰撃ち時のリコイル

#### 速度・操作性
- **リロード速度** — リロードアニメーション速度倍率
- **コッキング速度** — ボルトアクション速度倍率
- **RPM倍率** — 発射レート倍率
- **ADS速度** — エイム移行速度
- **武器切替速度** — 取り出し・しまい速度
- **バースト速度** — バースト内の射撃間隔速度
- **銃装備時移動速度** — 銃を持っている時の移動速度

#### 弾薬・マガジン
- **マガジン容量** — マガジンサイズ倍率
- **セミ / フルオート / バースト 弾数** — 一度に発射する弾数の倍率
- **弾薬非消費確率** — 射撃時に弾薬を消費しない確率
- **キル時弾薬回復 確率 / 固定数 / 割合** — キル時に弾薬を回復
- **リロード時弾薬非消費確率** — リロード時にインベントリ弾薬を消費しない確率
- **追加弾薬 確率 / 固定数 / 割合** — リロード時に追加弾薬を装填

#### 銃種別システム

すべての属性は銃種ごとにも設定可能です：

`最終値 = 全体属性 × 銃種別属性`

例：`sniper_damage = 1.5` かつ `gun_damage = 1.2` の場合、スナイパーライフルは `1.8倍` のダメージ。

### 使用例

```
/attribute @p tacz_attributes:gun_damage base set 2.0
/attribute @p tacz_attributes:sniper_headshot_multiplier base set 3.0
/attribute @p tacz_attributes:rifle_recoil base set 0.5
```

### 前提MOD

- **必須**: [Timeless and Classics Zero (TaCZ)](https://www.curseforge.com/minecraft/mc-mods/timeless-and-classics-zero)
- **推奨**: [Apothic Attributes](https://www.curseforge.com/minecraft/mc-mods/apothic-attributes) — バニラの制限を超えた属性値を設定可能

---

## 한국어

### 개요

**TaCZ Attributes**는 [Timeless and Classics Zero (TaCZ)](https://www.curseforge.com/minecraft/mc-mods/timeless-and-classics-zero)의 애드온 모드로, 총기에 RPG 스타일의 속성 시스템을 추가합니다. 데미지, 정확도, 반동, 발사 속도, 이동 속도 등 총기의 모든 동작을 마인크래프트의 기본 속성 시스템으로 제어할 수 있습니다.

즉, **커맨드**, **장비 (Curios/트링킷)**, **인챈트**, **포션** 등 바닐라 속성과 연동하는 모든 모드로 총기 스탯을 변경할 수 있습니다. RPG 서버, 성장 시스템, 커스텀 모드팩에 최적입니다.

### 특징

**33종의 전체 총기 공통 속성**과 7가지 총기 유형(피스톨, 스나이퍼, 라이플, 샷건, SMG, RPG, MG)별 **33종의 유형별 속성** — 총 **264개의 커스터마이징 가능한 속성**.

#### 데미지 & 전투
- **총기 데미지** — 전체 탄환 데미지 배율
- **힙파이어 / ADS 데미지** — 조준 상태별 데미지 배율
- **세미 / 풀오토 / 버스트 데미지** — 발사 모드별 데미지 배율
- **헤드샷 배율** — 헤드샷 보너스 데미지 스케일링
- **넉백 배율 & 기본값** — 탄환 넉백 추가 및 배율
- **관통수 배율** — 탄환 관통 수 배율

#### 정확도 & 반동
- **힙파이어 / ADS 정확도** — 조준 상태별 정확도
- **세미 / 풀오토 / 버스트 정확도** — 발사 모드별 정확도
- **반동 (전체 / 수직 / 수평)** — 전체적인 반동 제어
- **ADS 반동 (전체 / 수직 / 수평)** — 조준 시 반동
- **힙파이어 반동 (전체 / 수직 / 수평)** — 힙파이어 시 반동

#### 속도 & 조작성
- **리로드 속도** — 리로드 애니메이션 속도 배율
- **볼트 액션 속도** — 코킹 속도 배율
- **RPM 배율** — 발사 속도 배율
- **ADS 속도** — 조준 전환 속도
- **무기 전환 속도** — 무기 꺼내기/넣기 속도
- **버스트 속도** — 버스트 내 발사 간격 속도
- **총기 이동 속도** — 총기 소지 시 이동 속도

#### 탄약 & 탄창
- **탄창 용량** — 탄창 크기 배율
- **세미 / 풀오토 / 버스트 탄수** — 한 번에 발사하는 탄수 배율
- **탄약 절약 확률** — 발사 시 탄약을 소비하지 않을 확률
- **킬 시 탄약 회복 확률 / 고정량 / 비율** — 킬 시 탄약 회복
- **리로드 시 탄약 절약 확률** — 리로드 시 인벤토리 탄약을 소비하지 않을 확률
- **추가 탄약 확률 / 고정량 / 비율** — 리로드 시 추가 탄약 장전

#### 총기 유형 시스템

모든 속성은 총기 유형별로도 설정할 수 있습니다:

`최종값 = 전체 속성 × 유형별 속성`

예시: `sniper_damage = 1.5`이고 `gun_damage = 1.2`이면, 스나이퍼 라이플은 `1.8배` 데미지.

### 사용 예시

```
/attribute @p tacz_attributes:gun_damage base set 2.0
/attribute @p tacz_attributes:sniper_headshot_multiplier base set 3.0
/attribute @p tacz_attributes:rifle_recoil base set 0.5
```

### 의존 모드

- **필수**: [Timeless and Classics Zero (TaCZ)](https://www.curseforge.com/minecraft/mc-mods/timeless-and-classics-zero)
- **권장**: [Apothic Attributes](https://www.curseforge.com/minecraft/mc-mods/apothic-attributes) — 바닐라 제한을 넘는 속성 값 설정 가능

---

## 中文

### 概述

**TaCZ Attributes** 是 [Timeless and Classics Zero (TaCZ)](https://www.curseforge.com/minecraft/mc-mods/timeless-and-classics-zero) 的附属模组，为枪械添加了 RPG 风格的属性系统。通过 Minecraft 原生属性系统来控制枪械的方方面面——伤害、精准度、后坐力、射速、移动速度等。

这意味着您可以通过**命令**、**装备（Curios/饰品）**、**附魔**、**药水**或任何与原版属性交互的模组来修改枪械属性。非常适合 RPG 服务器、成长系统和自定义整合包。

### 特性

**33 种全局通用属性**，加上 7 种枪械类型（手枪、狙击枪、步枪、霰弹枪、冲锋枪、火箭筒、机枪）各 **33 种类型专属属性** — 共计 **264 个可自定义的属性**。

#### 伤害与战斗
- **枪械伤害** — 全局子弹伤害倍率
- **腰射 / 瞄准伤害** — 基于瞄准状态的伤害倍率
- **半自动 / 全自动 / 点射伤害** — 基于射击模式的伤害倍率
- **爆头倍率** — 爆头额外伤害的缩放
- **击退倍率与基础值** — 添加和缩放子弹击退
- **穿透数倍率** — 子弹穿透次数倍率

#### 精准度与后坐力
- **腰射 / 瞄准精准度** — 基于瞄准状态的精准度
- **半自动 / 全自动 / 点射精准度** — 基于射击模式的精准度
- **后坐力（综合 / 垂直 / 水平）** — 整体后坐力控制
- **瞄准后坐力（综合 / 垂直 / 水平）** — 瞄准时的后坐力
- **腰射后坐力（综合 / 垂直 / 水平）** — 腰射时的后坐力

#### 速度与操控
- **换弹速度** — 换弹动画速度倍率
- **拉栓速度** — 栓动速度倍率
- **RPM 倍率** — 射速倍率
- **瞄准速度** — 瞄准切换速度
- **切枪速度** — 武器掏出/收起速度
- **点射速度** — 点射内射击间隔速度
- **持枪移动速度** — 持枪时的移动速度

#### 弹药与弹匣
- **弹匣容量** — 弹匣大小倍率
- **半自动 / 全自动 / 点射弹量** — 每次发射的子弹数倍率
- **弹药节省概率** — 射击时不消耗弹药的概率
- **击杀回弹 概率 / 固定量 / 比例** — 击杀时回复弹药
- **换弹节省概率** — 换弹时不消耗背包弹药的概率
- **额外弹药 概率 / 固定量 / 比例** — 换弹时额外装填弹药

#### 枪械类型系统

所有属性均可按枪械类型单独设置：

`最终值 = 全局属性 × 类型属性`

例如：`sniper_damage = 1.5` 且 `gun_damage = 1.2` 时，狙击枪造成 `1.8 倍` 伤害。

### 使用示例

```
/attribute @p tacz_attributes:gun_damage base set 2.0
/attribute @p tacz_attributes:sniper_headshot_multiplier base set 3.0
/attribute @p tacz_attributes:rifle_recoil base set 0.5
```

### 依赖模组

- **必需**: [Timeless and Classics Zero (TaCZ)](https://www.curseforge.com/minecraft/mc-mods/timeless-and-classics-zero)
- **推荐**: [Apothic Attributes](https://www.curseforge.com/minecraft/mc-mods/apothic-attributes) — 允许设置超出原版限制的属性值
