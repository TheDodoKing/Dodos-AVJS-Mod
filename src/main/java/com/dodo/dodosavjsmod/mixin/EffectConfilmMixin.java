package com.dodo.dodosavjsmod.mixin;

import com.dodo.dodosavjsmod.init.DodosAVJSModGamerules;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.EffectConfilmCharactorProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EffectConfilmCharactorProcedure.class, remap = false)
public class EffectConfilmMixin {
    @Inject(method = "execute", at = @At("HEAD"))
    private static void instantInfinityBreak(LevelAccessor world, double x, double y, double z,
                                             Entity entity, Entity entityiterator, CallbackInfo ci) {
        if (!world.getLevelData().getGameRules().getBoolean(DodosAVJSModGamerules.REMOVE_INFINITY_LAYERS)) {
            return;
        }

        if (entity instanceof LivingEntity attacker
                && entityiterator instanceof LivingEntity victim
                && attacker.hasEffect(JujutsucraftModMobEffects.DOMAIN_AMPLIFICATION.get())
                && entity.getPersistentData().getBoolean("attack")
                && victim.hasEffect(JujutsucraftModMobEffects.INFINITY_EFFECT.get())) {
            victim.removeEffect(JujutsucraftModMobEffects.INFINITY_EFFECT.get());
        }
    }
}