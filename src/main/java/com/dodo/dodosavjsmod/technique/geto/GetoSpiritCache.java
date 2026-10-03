package com.dodo.dodosavjsmod.technique.geto;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "dodosavjsmod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GetoSpiritCache {

    private static final Map<UUID, Set<UUID>> ownedSpirits = new HashMap<>();

    public static void register(UUID ownerUuid, UUID spiritUuid) {
        ownedSpirits.computeIfAbsent(ownerUuid, k -> new HashSet<>()).add(spiritUuid);
    }

    public static void unregister(UUID ownerUuid, UUID spiritUuid) {
        Set<UUID> set = ownedSpirits.get(ownerUuid);
        if (set != null) {
            set.remove(spiritUuid);
            if (set.isEmpty()) {
                ownedSpirits.remove(ownerUuid);
            }
        }
    }

    public static List<Entity> getOwnedSpirits(ServerLevel level, UUID ownerUuid) {
        Set<UUID> ids = ownedSpirits.get(ownerUuid);
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<Entity> result = new ArrayList<>(ids.size());
        for (UUID id : ids) {
            Entity e = level.getEntity(id);
            if (e != null && e.isAlive()) {
                result.add(e);
            }
        }
        return result;
    }

    public static void clear() {
        ownedSpirits.clear();
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!entity.getPersistentData().getBoolean("CursedSpirit")) {
            return;
        }
        String ownerUuidStr = entity.getPersistentData().getString("OWNER_UUID");
        if (ownerUuidStr.isEmpty()) {
            return;
        }
        try {
            register(UUID.fromString(ownerUuidStr), entity.getUUID());
        } catch (IllegalArgumentException ignored) {
        }
    }

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        if (!entity.getPersistentData().getBoolean("CursedSpirit")) {
            return;
        }
        String ownerUuidStr = entity.getPersistentData().getString("OWNER_UUID");
        if (ownerUuidStr.isEmpty()) {
            return;
        }
        try {
            unregister(UUID.fromString(ownerUuidStr), entity.getUUID());
        } catch (IllegalArgumentException ignored) {
        }
    }
}