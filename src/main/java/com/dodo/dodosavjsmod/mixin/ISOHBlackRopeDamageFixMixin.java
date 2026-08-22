package com.dodo.dodosavjsmod.mixin;

import com.dodo.dodosavjsmod.init.DodosAVJSModGamerules;
import com.jujutsu.jujutsucraftaddon.events.ISOHBypassInfinityHandler;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ISOHBypassInfinityHandler.class)
public class ISOHBlackRopeDamageFixMixin {

    private static boolean fixEnabled(LivingEntity entity) {
        return entity != null && entity.level().getGameRules().getBoolean(DodosAVJSModGamerules.ISOHBLACKROPE_DAMAGE_FIX);
    }

    @Inject(method = "disableInfinityOneTick", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onDisableInfinityOneTick(LivingEntity target, CallbackInfo ci) {
        if (!fixEnabled(target)) {
            return;
        }
        if (target == null || !target.hasEffect((MobEffect) JujutsucraftModMobEffects.INFINITY_EFFECT.get())) {
            ci.cancel();
        }
    }

    @Redirect(
            method = "onLivingHurt",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/LivingEntity;invulnerableTime:I",
                    opcode = org.objectweb.asm.Opcodes.PUTFIELD,
                    remap = true
            ),
            remap = false
    )
    private static void onInvulnerableTimeSet(LivingEntity instance, int value) {
        if (!fixEnabled(instance)) {
            instance.invulnerableTime = value;
            return;
        }
        if (instance.hasEffect((MobEffect) JujutsucraftModMobEffects.INFINITY_EFFECT.get())) {
            instance.invulnerableTime = value;
        }
    }
}