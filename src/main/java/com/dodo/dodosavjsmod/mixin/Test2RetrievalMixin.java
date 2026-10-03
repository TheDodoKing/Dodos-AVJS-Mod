package com.dodo.dodosavjsmod.mixin;

import com.dodo.dodosavjsmod.technique.geto.GetoSpiritStorage;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.mcreator.jujutsucraft.procedures.Test2Procedure;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Test2Procedure.class, remap = false)
public class Test2RetrievalMixin {

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void materializeForRetrieval(
            LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {

        if (entity == null || entity.level().isClientSide()) return;
        if (!(entity instanceof Player player)) return;
        if (!(world instanceof ServerLevel serverLevel)) return;

        GetoSpiritStorage.resyncSlotCounts(player);

        if (player.getPersistentData().getDouble("data_cursed_spirit_manipulation1") == 0.0) return;

        var caps = player.getCapability(JujutsucraftModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(null);
        if (caps == null) return;

        String selected = caps.PlayerSelectCurseTechniqueName;
        if (selected == null || selected.isEmpty()) return;

        for (int i = 1; i < 10000; i++) {
            String key = "data_cursed_spirit_manipulation" + i;
            if (player.getPersistentData().getDouble(key) == 0.0) break;

            String displayName = player.getPersistentData().getString(key + "_name");
            String registryKey = player.getPersistentData().getString(key + "_registry");
            double storedNum = player.getPersistentData().getDouble(key + "_num");
            String expected = displayName + " ×" + Math.round(storedNum);

            if (selected.equals(expected)) {
                ci.cancel();
                GetoSpiritStorage.recallOneByRegistryKey(serverLevel, player, registryKey, x, y, z);
                return;
            }
        }
    }
}