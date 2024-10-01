package com.github.leopoko.tacz_attributes;

import com.github.leopoko.tacz_attributes.attribute.CustomAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber()
public class GunDamageModifier {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        // ダメージソースの情報を取得してログに出力
        String damageSourceName = event.getSource().getMsgId();
        if (!FMLEnvironment.production) {
            LOGGER.info("ダメージソース: " + damageSourceName);
            LOGGER.info("ダメージソースのエンティティ: " + event.getSource().getEntity());
            LOGGER.info("ダメージ量: " + event.getAmount());
        }

        // ダメージソースが銃に関するものであるかを判定（仮にダメージソース名が"gun"と仮定）
        if (damageSourceName.equals("tacz.bullet")) {
            // エンティティの属性に gun_damage がある場合、その値でダメージを調整する
            if (event.getSource().getEntity() instanceof LivingEntity) {
                LivingEntity attacker = (LivingEntity) event.getSource().getEntity();

                // 攻撃者がgun_damage属性を持っている場合、その属性を使用してダメージを修正
                if (attacker.getAttributes().hasAttribute(CustomAttributes.GUN_DAMAGE.get())) {
                    double damageModifier = attacker.getAttributeValue(CustomAttributes.GUN_DAMAGE.get());


                    float modifiedDamage = (float) (event.getAmount() * damageModifier);

                    // 開発環境でのみ修正後のダメージをログに出力する
                    if (!FMLEnvironment.production) {
                        LOGGER.info("修正後のダメージ: " + modifiedDamage);
                    }
                    event.setAmount(modifiedDamage);
                }
            }
        }
    }
}