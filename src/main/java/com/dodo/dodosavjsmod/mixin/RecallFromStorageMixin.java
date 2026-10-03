package com.dodo.dodosavjsmod.mixin;

import com.dodo.dodosavjsmod.technique.geto.GetoSpiritStorage;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.mcreator.jujutsucraft.procedures.CursedSpiritBallFoodEatenProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CursedSpiritBallFoodEatenProcedure.class, remap = false)
public class RecallFromStorageMixin {

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false)
    private static void bankDirectlyFromOrb(
            LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack,
            CallbackInfo ci) {

        if (entity == null || entity.level().isClientSide()) return;
        if (!(entity instanceof Player player)) return;

        CompoundTag tag = itemstack.getTag();
        if (tag == null || !tag.hasUUID("spirit_uuid_addon")) return;

        ci.cancel();

        var caps = player.getCapability(JujutsucraftModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(null);
        boolean hasGetoActive = caps != null
                && (caps.PlayerCurseTechnique == 18.0 || caps.PlayerCurseTechnique2 == 18.0);

        if (!hasGetoActive) {
            player.displayClientMessage(Component.literal("Equip Geto's Cursed Technique to use this."), true);
            return;
        }

        String registryKey = tag.getString("spirit_registry_addon");
        boolean valid = !registryKey.isEmpty() && GetoSpiritStorage.countByRegistryKey(player, registryKey) > 0;

        if (!valid) {
            player.displayClientMessage(Component.literal("You don't own this spirit."), true);
            return;
        }

        itemstack.shrink(1);
    }
}