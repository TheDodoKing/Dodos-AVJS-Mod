package com.dodo.dodosavjsmod.init;

import net.minecraft.world.level.GameRules;

public class DodosAVJSModGamerules {
    public static final GameRules.Key<GameRules.BooleanValue> EXECUTIONER_GUARD_FIX =
            GameRules.register(
                    "dodosAVJSModExecutionerGuardFix",
                    GameRules.Category.PLAYER,
                    GameRules.BooleanValue.create(true)
            );

    public static final GameRules.Key<GameRules.BooleanValue> ISOHBLACKROPE_DAMAGE_FIX =
            GameRules.register(
                    "dodosAVJSModISOHBlackropeDamageFix",
                    GameRules.Category.PLAYER,
                    GameRules.BooleanValue.create(true)
            );

    public static final GameRules.Key<GameRules.BooleanValue> DAGON_EXT_FIX =
            GameRules.register(
                    "dodosAVJSModDagonExtFix",
                    GameRules.Category.PLAYER,
                    GameRules.BooleanValue.create(true)
            );

    public static final GameRules.Key<GameRules.IntegerValue> STUN_DAMAGE_THRESHOLD_1 =
            GameRules.register("dodosAVJSModStunDamageThreshold1",
                    GameRules.Category.PLAYER,
                    GameRules.IntegerValue.create(5));

    public static final GameRules.Key<GameRules.IntegerValue> STUN_DAMAGE_THRESHOLD_2 =
            GameRules.register("dodosAVJSModstunDamageThreshold2",
                    GameRules.Category.PLAYER,
                    GameRules.IntegerValue.create(10));

    public static final GameRules.Key<GameRules.IntegerValue> STUN_DAMAGE_THRESHOLD_3 =
            GameRules.register("dodosAVJSModstunDamageThreshold3",
                    GameRules.Category.PLAYER,
                    GameRules.IntegerValue.create(20));

    public static final GameRules.Key<GameRules.BooleanValue> STOP_TSUKUMO_EXTENSION_EFFECTS_WHILE_UNSTABLE =
            GameRules.register(
                    "dodosAVJSModStopTsukumoExtensionEffectsWhileUnstable",
                    GameRules.Category.PLAYER,
                    GameRules.BooleanValue.create(true)
            );

    public static void init() {}
}