package com.github.leopoko.tacz_attributes.api;

/**
 * ObjectAnimationRunner に速度倍率フィールドを追加するためのダックインターフェース。
 * クライアントMixin経由で実装される。
 */
public interface ISpeedModifiable {
    float tacz_attributes$getSpeedMultiplier();
    void tacz_attributes$setSpeedMultiplier(float speed);
}
