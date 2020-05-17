package io.savagedev.buckets.init;

/*
 * ModItems.java
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

import com.google.gson.JsonObject;
import io.savagedev.buckets.Buckets;
import io.savagedev.buckets.items.BaseItem;
import io.savagedev.buckets.items.ItemBigBucket;
import io.savagedev.buckets.items.ItemBigBucketItem;
import io.savagedev.buckets.util.LogHelper;
import io.savagedev.buckets.util.ModNames;
import io.savagedev.buckets.util.ModReference;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModItems
{
    public static final List<Supplier<? extends Item>> ENTRIES = new ArrayList<>();

    public static final RegistryObject<BaseItem> QUARTZ_BUCKET_EMPTY = registerBigBucket(ItemBigBucketItem.QUARTZ_BUCKET_EMPTY);
    public static final RegistryObject<BaseItem> QUARTZ_BUCKET_WATER = registerBigBucket(ItemBigBucketItem.QUARTZ_BUCKET_WATER);
    public static final RegistryObject<BaseItem> QUARTZ_BUCKET_LAVA = registerBigBucket(ItemBigBucketItem.QUARTZ_BUCKET_LAVA);

    public static final RegistryObject<BaseItem> OBSIDIAN_BUCKET_EMPTY = registerBigBucket(ItemBigBucketItem.OBSIDIAN_BUCKET_EMPTY);
    public static final RegistryObject<BaseItem> OBSIDIAN_BUCKET_WATER = registerBigBucket(ItemBigBucketItem.OBSIDIAN_BUCKET_WATER);
    public static final RegistryObject<BaseItem> OBSIDIAN_BUCKET_LAVA = registerBigBucket(ItemBigBucketItem.OBSIDIAN_BUCKET_LAVA);

    public static final RegistryObject<BaseItem> EMERALD_BUCKET_EMPTY = registerBigBucket(ItemBigBucketItem.EMERALD_BUCKET_EMPTY);
    public static final RegistryObject<BaseItem> EMERALD_BUCKET_WATER = registerBigBucket(ItemBigBucketItem.EMERALD_BUCKET_WATER);
    public static final RegistryObject<BaseItem> EMERALD_BUCKET_LAVA = registerBigBucket(ItemBigBucketItem.EMERALD_BUCKET_LAVA);

    public static final RegistryObject<BaseItem> GOLD_BUCKET_EMPTY = registerBigBucket(ItemBigBucketItem.GOLD_BUCKET_EMPTY);
    public static final RegistryObject<BaseItem> GOLD_BUCKET_WATER = registerBigBucket(ItemBigBucketItem.GOLD_BUCKET_WATER);
    public static final RegistryObject<BaseItem> GOLD_BUCKET_LAVA = registerBigBucket(ItemBigBucketItem.GOLD_BUCKET_LAVA);

    public static final RegistryObject<BaseItem> DIAMOND_BUCKET_EMPTY = registerBigBucket(ItemBigBucketItem.DIAMOND_BUCKET_EMPTY);
    public static final RegistryObject<BaseItem> DIAMOND_BUCKET_WATER = registerBigBucket(ItemBigBucketItem.DIAMOND_BUCKET_WATER);
    public static final RegistryObject<BaseItem> DIAMOND_BUCKET_LAVA = registerBigBucket(ItemBigBucketItem.DIAMOND_BUCKET_LAVA);

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        ENTRIES.stream().map(Supplier::get).forEach(registry::register);
    }

    private static <T extends Item> RegistryObject<T> register(String name) {
        return register(name, () -> new BaseItem(p -> p.group(Buckets.modGroup)));
    }

    private static <T extends Item> RegistryObject<T> registerBigBucket(ItemBigBucketItem bucketItem) {
        generateModelFile(bucketItem.getEmptyBucket());
        generateModelFile(bucketItem.getWaterBucket());
        generateModelFile(bucketItem.getLavaBucket());

        return register(bucketItem.getName(), () -> new ItemBigBucket(bucketItem));
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<? extends Item> item) {
        ResourceLocation loc = new ResourceLocation(ModReference.MOD_ID, name);
        ENTRIES.add(() -> item.get().setRegistryName(loc));
        return RegistryObject.of(loc, ForgeRegistries.ITEMS);
    }

    private static void generateModelFile(String itemName) {
        JsonObject parent = new JsonObject();
        JsonObject layers = new JsonObject();

        parent.addProperty("parent", "item/generated");
        layers.addProperty("layer0", ModReference.MOD_DOMAIN + "item/" + itemName);
        parent.add("textures", layers);

        try {
            File file = new File("../src/main/resources/assets/buckets/models/item/" + itemName + ".json");

            if(!file.exists()) {
                file.createNewFile();

                FileWriter writer = new FileWriter(file);
                writer.write(parent.toString());
                writer.flush();
                writer.close();
                LogHelper.info("Generated model file for " + itemName);
            }
        } catch (Exception e) {
            LogHelper.error("Error generating model file for " + itemName);
            e.printStackTrace();
        }
    }
}
