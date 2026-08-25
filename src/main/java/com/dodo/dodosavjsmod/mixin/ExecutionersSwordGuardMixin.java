package com.dodo.dodosavjsmod.mixin;

import com.dodo.dodosavjsmod.init.DodosAVJSModGamerules;
import net.mcreator.jujutsucraft.procedures.EffectAttackProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EffectAttackProcedure.class, remap = false)
public class ExecutionersSwordGuardMixin {

    @Redirect(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;m_21023_(Lnet/minecraft/world/effect/MobEffect;)Z",
                    ordinal = 1,
                    remap = false
            )
    )
    private static boolean executionerGuardFix(LivingEntity instance, MobEffect effect) {
        boolean ruleEnabled = instance.level().getGameRules().getBoolean(DodosAVJSModGamerules.EXECUTIONER_GUARD_FIX);
        boolean hasEffect = instance.hasEffect(effect);
        int amplifier = hasEffect ? instance.getEffect(effect).getAmplifier() : -1;

        if (!ruleEnabled) {
            return hasEffect;
        }

        if (!hasEffect) return false;
        return amplifier >= 1;
    }
}