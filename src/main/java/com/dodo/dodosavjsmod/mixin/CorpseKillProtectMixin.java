package com.dodo.dodosavjsmod.mixin;

import com.dodo.dodosavjsmod.init.DodosAVJSModGamerules;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class CorpseKillProtectMixin {

    @Inject(method = "kill", at = @At("HEAD"), cancellable = true)
    private void protectCorpse(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (self.level().isClientSide) return;
        if (!self.level().getGameRules().getBoolean(DodosAVJSModGamerules.PROTECT_CORPSES)) return;

        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(self.getType());
        if (id == null || !id.getNamespace().equals("corpse")) return;

        boolean fromJjc = StackWalker.getInstance().walk(frames ->
                frames.anyMatch(f -> f.getClassName().startsWith("net.mcreator.jujutsucraft.")));

        if (fromJjc) ci.cancel();
    }
}