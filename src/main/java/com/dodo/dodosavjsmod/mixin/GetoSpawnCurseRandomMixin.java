package com.dodo.dodosavjsmod.mixin;

import com.dodo.dodosavjsmod.technique.geto.GetoSpiritCache;
import net.mcreator.jujutsucraft.procedures.GetoSpawnCurseRandomProcedure;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = GetoSpawnCurseRandomProcedure.class, remap = false)
public class GetoSpawnCurseRandomMixin {

    private static final ThreadLocal<Entity> CURRENT_OWNER = new ThreadLocal<>();

    @Inject(method = "execute", at = @At("HEAD"))
    private static void captureCaller(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        CURRENT_OWNER.set(entity);
    }

    @Redirect(
            method = "execute",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;getEntities(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;")
    )
    private static List<Entity> redirectScan(LevelAccessor level, EntityTypeTest<Entity, Entity> test, AABB aabb, Predicate<Entity> predicate) {
        Entity owner = CURRENT_OWNER.get();
        if (level instanceof ServerLevel serverLevel && owner != null) {
            return GetoSpiritCache.getOwnedSpirits(serverLevel, owner.getUUID());
        }
        return level.getEntities(test, aabb, predicate);
    }
}