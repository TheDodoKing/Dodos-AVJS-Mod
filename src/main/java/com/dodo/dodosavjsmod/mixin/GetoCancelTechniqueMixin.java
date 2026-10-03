package com.dodo.dodosavjsmod.mixin;

import com.dodo.dodosavjsmod.technique.geto.GetoCaptureScheduler;
import com.dodo.dodosavjsmod.technique.geto.GetoSpiritStorage;
import net.mcreator.jujutsucraft.procedures.GetoCancelTechniqueProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = GetoCancelTechniqueProcedure.class, remap = false)
public class GetoCancelTechniqueMixin {

    private static final ThreadLocal<UUID> TARGET_SPIRIT_ID = new ThreadLocal<>();

    @Inject(method = "execute", at = @At("HEAD"))
    private static void findTarget(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        TARGET_SPIRIT_ID.remove();
        if (entity == null || entity.level().isClientSide()) return;
        if (!(entity instanceof Player player)) return;

        double friendNum = GetoSpiritStorage.ensureFriendNum(player);
        if (friendNum == 0.0) return;

        Vec3 center = new Vec3(x, y, z);
        for (Entity candidate : world.getEntities(entity, new AABB(center, center).inflate(128.0))) {
            if (candidate == entity) continue;
            if (!candidate.getPersistentData().getBoolean("CursedSpirit")) continue;
            if (candidate instanceof Player) continue;
            if (!candidate.isAlive()) continue;
            if (friendNum != candidate.getPersistentData().getDouble("friend_num_worker")) continue;
            if (!candidate.getPersistentData().getString("OWNER_UUID").equals(player.getStringUUID())) continue;
            if (candidate.getPersistentData().getDouble("select") != 0.0) continue;

            TARGET_SPIRIT_ID.set(candidate.getUUID());
            break;
        }
    }

    @Inject(method = "execute", at = @At("RETURN"))
    private static void scheduleCleanup(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        try {
            if (entity == null || entity.level().isClientSide()) return;
            UUID spiritId = TARGET_SPIRIT_ID.get();
            if (spiritId == null || !(entity instanceof Player player)) return;

            GetoCaptureScheduler.schedule(player, spiritId);
        } finally {
            TARGET_SPIRIT_ID.remove();
        }
    }
}