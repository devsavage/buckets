package io.savagedev.buckets;

/*
 * Buckets.java
 * Copyright (C) 2020 Savage - github.com/devsavage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import io.savagedev.buckets.handler.TimedBucketTickHandler;
import io.savagedev.buckets.handler.UpdateMessageHandler;
import io.savagedev.buckets.init.ModItems;
import io.savagedev.buckets.util.ModReference;
import io.savagedev.savagecore.util.updater.Updater;
import io.savagedev.savagecore.util.updater.UpdaterUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModReference.MOD_ID)
public class Buckets
{
    public Updater UPDATER;

    public static ModContainer MOD_CONTAINER;

    public static ItemGroup modGroup = new ItemGroup(ModReference.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.DIAMOND_BUCKET_EMPTY.get());
        }
    };

    public Buckets() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MOD_CONTAINER = ModLoadingContext.get().getActiveContainer();
        UPDATER = new Updater().setModId(ModReference.MOD_ID)
                .setMinecraftVersion(Minecraft.getInstance().getVersion())
                .setCurrentVersion(MOD_CONTAINER.getModInfo().getVersion().toString());

        modEventBus.register(this);
        modEventBus.register(new ModItems());
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new TimedBucketTickHandler());
        MinecraftForge.EVENT_BUS.register(new UpdateMessageHandler(UPDATER));

        UpdaterUtils.initializeUpdateCheck(UPDATER);
    }
}
