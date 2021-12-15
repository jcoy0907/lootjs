package com.github.llytho.lootjs;

import com.github.llytho.lootjs.core.Constants;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MODID)
public class GlobalLootJSMod {


    public GlobalLootJSMod() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    @SubscribeEvent
    public void setup(final FMLCommonSetupEvent event) {
        // MinecraftForge.EVENT_BUS.register(MyClassWithEvents.class);
    }
}
