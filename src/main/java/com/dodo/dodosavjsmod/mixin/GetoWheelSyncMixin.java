package com.dodo.dodosavjsmod.mixin;

import com.dodo.dodosavjsmod.technique.geto.GetoSpiritStorage;
import net.mcreator.jujutsucraft.procedures.KeyChangeTechniqueOnKeyPressed2Procedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyChangeTechniqueOnKeyPressed2Procedure.class, remap = false)
public class GetoWheelSyncMixin {

    @Inject(
            method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;DD)V",
            at = @At("HEAD")
    )
    private static void resyncBeforeCycling(
            LevelAccessor world, double x, double y, double z, Entity entity,
            double playerCT, double playerSelect, CallbackInfo ci) {

        if (entity == null || entity.level().isClientSide()) return;
        if (entity instanceof Player player) {
            GetoSpiritStorage.resyncSlotCounts(player);
        }
    }
}