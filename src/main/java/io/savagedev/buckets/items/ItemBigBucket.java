package io.savagedev.buckets.items;

/*
 * ItemBigBucket.java
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

import com.google.common.collect.Maps;
import io.savagedev.buckets.Buckets;
import io.savagedev.buckets.api.IBucketItem;
import io.savagedev.buckets.init.ModItems;
import io.savagedev.buckets.util.ModReference;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ItemBigBucket extends BaseItemDamageableBucket implements IBucketItem
{
    public static final Map<ItemBigBucketItem, ItemBigBucket> BIG_BUCKETS_MAP = Maps.newEnumMap(ItemBigBucketItem.class);
    public final ItemBigBucketItem bucketItem;

    public ItemBigBucket(ItemBigBucketItem bucketItem) {
        super(p -> (p.group(Buckets.modGroup).maxStackSize(1).maxDamage(bucketItem.getBucketDamage())), bucketItem.getFluidType());
        this.bucketItem = bucketItem;
        BIG_BUCKETS_MAP.put(bucketItem, this);
    }

    @Override
    public Item getEmptyBucketItem() {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModReference.MOD_ID, this.bucketItem.getEmptyBucket()))).getItem();
    }

    @Override
    public Item getLavaBucketItem() {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModReference.MOD_ID, this.bucketItem.getLavaBucket()))).getItem();
    }

    @Override
    public Item getWaterBucketItem() {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModReference.MOD_ID, this.bucketItem.getWaterBucket()))).getItem();
    }
}
