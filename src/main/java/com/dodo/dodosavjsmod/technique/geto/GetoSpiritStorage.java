package com.dodo.dodosavjsmod.technique.geto;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;
import java.util.UUID;

public class GetoSpiritStorage {

    private static final String KEY = "dodosavjsmodspiritstorage";

    public static double ensureFriendNum(Player owner) {
        double friendNum = owner.getPersistentData().getDouble("friend_num");
        if (friendNum == 0.0) {
            friendNum = Math.random() + 0.0000001;
            owner.getPersistentData().putDouble("friend_num", friendNum);
        }
        return friendNum;
    }

    public static String registryKeyFor(Entity spirit) {
        var id = ForgeRegistries.ENTITY_TYPES.getKey(spirit.getType());
        return id == null ? spirit.getType().toString() : id.toString();
    }

    public static void store(Player owner, Entity spirit) {
        CompoundTag template = new CompoundTag();
        if (!spirit.save(template)) {
            return;
        }
        template.remove("UUID");
        template.remove("Pos");

        String registryKey = registryKeyFor(spirit);
        String displayName = spirit.getDisplayName().getString();
        if (displayName.isEmpty()) displayName = registryKey;

        addToBank(owner, registryKey, displayName, template);
        spirit.discard();
        resyncSlotCounts(owner);
    }

    private static void addToBank(Player owner, String registryKey, String displayName, CompoundTag template) {
        CompoundTag bank = owner.getPersistentData().getCompound(KEY);
        CompoundTag entry;

        if (bank.contains(registryKey)) {
            entry = bank.getCompound(registryKey);
            entry.putInt("count", entry.getInt("count") + 1);
        } else {
            entry = new CompoundTag();
            entry.put("template", template);
            entry.putInt("count", 1);
            entry.putString("displayName", displayName);
        }

        bank.put(registryKey, entry);
        owner.getPersistentData().put(KEY, bank);
    }

    public static Entity recallOneByRegistryKey(ServerLevel level, Player owner, String registryKey, double x, double y, double z) {
        CompoundTag bank = owner.getPersistentData().getCompound(KEY);
        if (!bank.contains(registryKey)) return null;

        CompoundTag entry = bank.getCompound(registryKey);
        int count = entry.getInt("count");
        if (count <= 0) return null;

        CompoundTag template = entry.getCompound("template").copy();

        if (count <= 1) {
            bank.remove(registryKey);
        } else {
            entry.putInt("count", count - 1);
            bank.put(registryKey, entry);
        }
        owner.getPersistentData().put(KEY, bank);

        Entity revived = reviveFromData(level, owner, template, x, y, z);
        resyncSlotCounts(owner);
        return revived;
    }

    public static int countByRegistryKey(Player owner, String registryKey) {
        CompoundTag bank = owner.getPersistentData().getCompound(KEY);
        if (!bank.contains(registryKey)) return 0;
        return bank.getCompound(registryKey).getInt("count");
    }

    public static void resyncSlotCounts(Player owner) {
        for (int i = 1; i < 10000; i++) {
            String key = "data_cursed_spirit_manipulation" + i;
            if (!owner.getPersistentData().contains(key)) break;
            owner.getPersistentData().remove(key);
            owner.getPersistentData().remove(key + "_name");
            owner.getPersistentData().remove(key + "_num");
            owner.getPersistentData().remove(key + "_registry");
        }

        CompoundTag bank = owner.getPersistentData().getCompound(KEY);
        int slot = 1;
        for (String registryKey : bank.getAllKeys()) {
            CompoundTag entry = bank.getCompound(registryKey);
            int count = entry.getInt("count");
            if (count <= 0) continue;

            String baseName = entry.getString("displayName");
            if (baseName.isEmpty()) baseName = registryKey;
            String suffix = registryKey.contains(":") ? registryKey.substring(registryKey.indexOf(':') + 1) : registryKey;
            String displayName = baseName + " [" + suffix + "]";

            String key = "data_cursed_spirit_manipulation" + slot;
            owner.getPersistentData().putDouble(key, 64.0 + slot);
            owner.getPersistentData().putString(key + "_name", displayName);
            owner.getPersistentData().putString(key + "_registry", registryKey);
            owner.getPersistentData().putDouble(key + "_num", count);
            slot++;
        }
    }

    private static Entity reviveFromData(ServerLevel level, Player owner, CompoundTag data, double x, double y, double z) {
        Optional<Entity> maybeRevived = EntityType.create(data, level);
        if (maybeRevived.isEmpty()) {
            return null;
        }

        Entity revived = maybeRevived.get();
        revived.setUUID(UUID.randomUUID());
        revived.moveTo(x, y, z, revived.getYRot(), revived.getXRot());
        revived.setInvulnerable(false);
        if (revived instanceof Mob mob) {
            mob.setNoAi(false);
        }

        double friendNum = ensureFriendNum(owner);

        revived.getPersistentData().putDouble("cnt_target", 0.0);
        revived.getPersistentData().putDouble("modement_mode", 0.0);
        revived.getPersistentData().putDouble("cnt_settarget", 0.0);
        revived.getPersistentData().putBoolean("JujutsuSorcerer", owner.getPersistentData().getBoolean("JujutsuSorcerer"));
        revived.getPersistentData().putBoolean("CurseUser", owner.getPersistentData().getBoolean("CurseUser"));
        revived.getPersistentData().putBoolean("Player", false);
        revived.getPersistentData().putString("OWNER_UUID", owner.getStringUUID());
        revived.getPersistentData().putString("TARGET_UUID", "");
        revived.getPersistentData().putDouble("friend_num", friendNum);
        revived.getPersistentData().putDouble("friend_num_worker", friendNum);

        level.addFreshEntity(revived);
        return revived;
    }
}