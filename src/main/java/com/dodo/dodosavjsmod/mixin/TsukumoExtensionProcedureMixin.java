package com.dodo.dodosavjsmod.mixin;

import com.dodo.dodosavjsmod.init.DodosAVJSModGamerules;
import com.jujutsu.jujutsucraftaddon.procedures.TsukumoExtensionProcedure;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(value = TsukumoExtensionProcedure.class, remap = false)
public class TsukumoExtensionProcedureMixin {

    @Redirect(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
            )
    )
    private static boolean skipEffectsIfUnstable(LivingEntity instance, MobEffectInstance effectInstance) {
        MobEffect unstable = BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.parse("jujutsucraft:unstable"));
        if (instance.level().getGameRules().getBoolean(DodosAVJSModGamerules.STOP_TSUKUMO_EXTENSION_EFFECTS_WHILE_UNSTABLE)) {
            if (unstable != null && instance.hasEffect(unstable)) {
                return false;
            }
        }
        return instance.addEffect(effectInstance);
    }
}