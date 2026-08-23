package com.dodo.dodosavjsmod.mixin;

import com.dodo.dodosavjsmod.init.DodosAVJSModGamerules;
import com.jujutsu.jujutsucraftaddon.procedures.DagonExtensionProcedure;
import com.jujutsu.jujutsucraftaddon.network.JujutsucraftaddonModVariables;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DagonExtensionProcedure.class, remap = false)
public class DagonExtensionProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false)
    private static void dodo$onExecute(LevelAccessor world, Entity entity, CallbackInfo ci) {
        boolean fixEnabled = world.getLevelData().getGameRules()
                .getBoolean(DodosAVJSModGamerules.DAGON_EXT_FIX);

        if (!fixEnabled) {
            return;
        }
        ci.cancel();

        if (!(entity instanceof LivingEntity living)) return;

        boolean infused = ((JujutsucraftaddonModVariables.PlayerVariables) entity
                .getCapability(JujutsucraftaddonModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null)
                .orElse(new JujutsucraftaddonModVariables.PlayerVariables())).InfusedDomain;

        if (!infused) return;
        if (world.isClientSide()) return;

        boolean waterBoost = living.isInWater();

        living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 1, false, false));

        if (waterBoost) {
            living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 3, false, false));
            living.addEffect(new MobEffectInstance(MobEffects.SATURATION, 40, 0, false, false));
            living.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 40, 0, false, false));
            living.setAirSupply(living.getMaxAirSupply());
        }
    }
}