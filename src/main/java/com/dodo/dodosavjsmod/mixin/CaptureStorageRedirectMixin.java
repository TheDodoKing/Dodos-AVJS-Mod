package com.dodo.dodosavjsmod.mixin;

import com.dodo.dodosavjsmod.technique.geto.GetoCaptureScheduler;
import com.dodo.dodosavjsmod.technique.geto.GetoSpiritStorage;
import net.mcreator.jujutsucraft.procedures.Test1Procedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Test1Procedure.class, remap = false)
public class CaptureStorageRedirectMixin {

    private static final ThreadLocal<Entity> CAPTURED_SPIRIT = new ThreadLocal<>();

    @Inject(
            method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)V",
            at = @At("HEAD")
    )
    private static void captureContext(
            Event event, LevelAccessor world, double x, double y, double z,
            Entity entity, Entity sourceentity, CallbackInfo ci) {

        if (entity == null || entity.level().isClientSide()) {
            CAPTURED_SPIRIT.remove();
            return;
        }

        CAPTURED_SPIRIT.set(entity);

        if (sourceentity instanceof Player player) {
            GetoSpiritStorage.ensureFriendNum(player);
        }
    }

    @Redirect(
            method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V")
    )
    private static void redirectGiveOrb(Player player, ItemStack stack) {
        if (player.level().isClientSide()) {
            ItemHandlerHelper.giveItemToPlayer(player, stack);
            return;
        }

        Entity spirit = CAPTURED_SPIRIT.get();
        if (spirit != null) {
            CompoundTag tag = stack.getOrCreateTag();
            tag.putUUID("spirit_uuid_addon", spirit.getUUID());
            tag.putString("spirit_registry_addon", GetoSpiritStorage.registryKeyFor(spirit));
        }
        ItemHandlerHelper.giveItemToPlayer(player, stack);
    }

    @Inject(
            method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)V",
            at = @At("RETURN")
    )
    private static void afterCapture(
            Event event, LevelAccessor world, double x, double y, double z,
            Entity entity, Entity sourceentity, CallbackInfo ci) {

        try {
            if (entity == null || entity.level().isClientSide()) return;
            if (!(sourceentity instanceof Player player)) return;
            if (!entity.getPersistentData().getBoolean("CursedSpirit")) return;

            GetoCaptureScheduler.schedule(player, entity.getUUID());
        } finally {
            CAPTURED_SPIRIT.remove();
        }
    }
}