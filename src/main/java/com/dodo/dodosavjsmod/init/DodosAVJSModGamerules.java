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
    public static void init() {}
}