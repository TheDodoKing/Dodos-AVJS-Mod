package com.dodo.dodosavjsmod.mixin;

import com.dodo.dodosavjsmod.init.DodosAVJSModGamerules;
import net.mcreator.jujutsucraft.procedures.WhenEntityAttacked2Procedure;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = WhenEntityAttacked2Procedure.class, remap = false)
public abstract class WhenEntityAttacked2ProcedureMixin {

    @ModifyConstant(
            method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;D)V",
            constant = @Constant(doubleValue = 0.05D)
    )
    private static double stunthreshold1(double original,
                                               Event event, LevelAccessor world, DamageSource damagesource,
                                               Entity entity, Entity immediatesourceentity, Entity sourceentity, double amount) {
        int rule = world.getLevelData().getGameRules().getInt(DodosAVJSModGamerules.STUN_DAMAGE_THRESHOLD_1);
        return rule / 100.0D;
    }

    @ModifyConstant(
            method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;D)V",
            constant = @Constant(doubleValue = 0.1D, ordinal = 1)
    )
    private static double stunthreshold2(double original,
                                               Event event, LevelAccessor world, DamageSource damagesource,
                                               Entity entity, Entity immediatesourceentity, Entity sourceentity, double amount) {
        int rule = world.getLevelData().getGameRules().getInt(DodosAVJSModGamerules.STUN_DAMAGE_THRESHOLD_2);
        return rule / 100.0D;
    }

    @ModifyConstant(
            method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;D)V",
            constant = @Constant(doubleValue = 0.2D, ordinal = 1)
    )
    private static double stunthreshold3(double original,
                                               Event event, LevelAccessor world, DamageSource damagesource,
                                               Entity entity, Entity immediatesourceentity, Entity sourceentity, double amount) {
        int rule = world.getLevelData().getGameRules().getInt(DodosAVJSModGamerules.STUN_DAMAGE_THRESHOLD_3);
        return rule / 100.0D;
    }
}