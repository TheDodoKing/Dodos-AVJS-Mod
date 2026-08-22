package com.dodo.dodosavjsmod;

import com.dodo.dodosavjsmod.init.DodosAVJSModGamerules;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DodosAVJSMod.MODID)
public class DodosAVJSMod {
    public static final String MODID = "dodosavjsmod";

    public DodosAVJSMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        DodosAVJSModGamerules.init();

    }
}