package com.dodo.dodosavjsmod.technique.geto;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "dodosavjsmod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GetoCaptureScheduler {

    private static class PendingCapture {
        final Player owner;
        final UUID spiritId;
        int ticksRemaining = 2;
        int attemptsLeft = 100;

        PendingCapture(Player owner, UUID spiritId) {
            this.owner = owner;
            this.spiritId = spiritId;
        }
    }

    private static final List<PendingCapture> pending = new ArrayList<>();

    public static void schedule(Player owner, UUID spiritId) {
        pending.add(new PendingCapture(owner, spiritId));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || pending.isEmpty()) return;

        pending.removeIf(p -> {
            if (p.ticksRemaining > 0) {
                p.ticksRemaining--;
                return false;
            }

            MinecraftServer server = p.owner.getServer();
            if (server == null) return true;

            ServerLevel curseLevel = server.getLevel(
                    ResourceKey.create(Registries.DIMENSION, new ResourceLocation("jujutsucraft", "cursed_spirit_manipulation_dimension")));
            if (curseLevel == null) return true;

            Entity liveSpirit = curseLevel.getEntity(p.spiritId);
            boolean done;

            if (liveSpirit != null) {
                GetoSpiritStorage.store(p.owner, liveSpirit);
                done = true;
            } else {
                p.attemptsLeft--;
                done = p.attemptsLeft <= 0;
                if (done) {
                    p.owner.displayClientMessage(
                            Component.literal("The curse is too strong! Try weakening it first."),
                            false);
                }
            }

            if (done) {
                CommandSourceStack source = new CommandSourceStack(
                        CommandSource.NULL,
                        p.owner.position(), p.owner.getRotationVector(),
                        curseLevel, 4, p.owner.getName().getString(), p.owner.getDisplayName(),
                        server, p.owner);
                server.getCommands().performPrefixedCommand(source,
                        "execute in jujutsucraft:cursed_spirit_manipulation_dimension run forceload remove 0 0 0 0");
            }

            return done;
        });
    }
}