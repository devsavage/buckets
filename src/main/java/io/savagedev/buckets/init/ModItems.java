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

import io.savagedev.buckets.Buckets;
import io.savagedev.buckets.items.BaseItem;
import io.savagedev.buckets.items.ItemDiamondBucket;
import io.savagedev.buckets.util.ModNames;
import io.savagedev.buckets.util.ModReference;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModItems
{
    public static final List<Supplier<? extends Item>> ENTRIES = new ArrayList<>();

    public static final RegistryObject<BaseItem> DIAMOND_BUCKET_EMPTY = register(ModNames.Items.DIAMOND_BUCKET_EMPTY, () -> new ItemDiamondBucket(Fluids.EMPTY));
    public static final RegistryObject<BaseItem> DIAMOND_BUCKET_WATER = register(ModNames.Items.DIAMOND_BUCKET_WATER, () -> new ItemDiamondBucket(Fluids.WATER));
    public static final RegistryObject<BaseItem> DIAMOND_BUCKET_LAVA = register(ModNames.Items.DIAMOND_BUCKET_LAVA, () -> new ItemDiamondBucket(Fluids.LAVA));

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        ENTRIES.stream().map(Supplier::get).forEach(registry::register);
    }

    private static <T extends Item> RegistryObject<T> register(String name) {
        return register(name, () -> new BaseItem(p -> p.group(Buckets.modGroup)));
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<? extends Item> item) {
        ResourceLocation loc = new ResourceLocation(ModReference.MOD_ID, name);
        ENTRIES.add(() -> item.get().setRegistryName(loc));
        return RegistryObject.of(loc, ForgeRegistries.ITEMS);
    }
}
