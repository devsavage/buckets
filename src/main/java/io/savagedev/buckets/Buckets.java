package io.savagedev.buckets;

/*
 * Buckets.java
 * Copyright (C) 2020-2022 Savage - github.com/devsavage
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

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import io.savagedev.buckets.handler.TimedBucketTickHandler;
import io.savagedev.buckets.handler.UpdateMessageHandler;
import io.savagedev.buckets.init.BucketsConfig;
import io.savagedev.buckets.init.ModItems;
import io.savagedev.buckets.util.ModReference;
import io.savagedev.savagecore.util.updater.Updater;
import io.savagedev.savagecore.util.updater.UpdaterUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.versions.mcp.MCPVersion;

import java.nio.file.Path;

@Mod(ModReference.MOD_ID)
public class Buckets
{
    public Updater UPDATER;

    public static ModContainer MOD_CONTAINER;

    public static CreativeModeTab modGroup = new CreativeModeTab(ModReference.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.DIAMOND_BUCKET_EMPTY.get());
        }
    };

    public Buckets() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MOD_CONTAINER = ModLoadingContext.get().getActiveContainer();
        UPDATER = new Updater().setModId(ModReference.MOD_ID)
                .setMinecraftVersion(MCPVersion.getMCVersion())
                .setCurrentVersion(MOD_CONTAINER.getModInfo().getVersion().toString());

        modEventBus.register(this);
        modEventBus.register(new ModItems());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BucketsConfig.COMMON);

        Path configPath = FMLPaths.CONFIGDIR.get().resolve("buckets-common.toml");
        CommentedFileConfig configData = CommentedFileConfig.builder(configPath).sync().autosave().writingMode(WritingMode.REPLACE).build();

        configData.load();
        BucketsConfig.COMMON.setConfig(configData);
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new TimedBucketTickHandler());
        MinecraftForge.EVENT_BUS.register(new UpdateMessageHandler(UPDATER));

        UpdaterUtils.initializeUpdateCheck(UPDATER);
    }
}
