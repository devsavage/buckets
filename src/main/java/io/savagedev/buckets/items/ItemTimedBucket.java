package io.savagedev.buckets.items;

/*
 * ItemTimedBucket.java
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

import io.savagedev.buckets.Buckets;
import io.savagedev.buckets.api.IBucketItem;
import io.savagedev.buckets.items.base.BaseItemDamageableBucket;
import io.savagedev.buckets.items.enums.DamageType;
import io.savagedev.buckets.items.enums.ItemTimedBucketItem;
import io.savagedev.buckets.util.ModReference;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ItemTimedBucket extends BaseItemDamageableBucket implements IBucketItem
{
    public final ItemTimedBucketItem bucketItem;

    public ItemTimedBucket(ItemTimedBucketItem bucketItem) {
        super(p -> (p.tab(Buckets.modGroup).stacksTo(1).durability(bucketItem.getBucketMaxTime())), bucketItem.getFluidType(), DamageType.NORMAL);
        this.bucketItem = bucketItem;
    }

    @Override
    public Item getEmptyBucketItem() {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModReference.MOD_ID, this.bucketItem.getEmptyBucket()))).asItem();
    }

    @Override
    public Item getLavaBucketItem() {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModReference.MOD_ID, this.bucketItem.getLavaBucket()))).asItem();
    }

    @Override
    public Item getWaterBucketItem() {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModReference.MOD_ID, this.bucketItem.getWaterBucket()))).asItem();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TextComponent("Seconds Remaining: " + (this.bucketItem.getBucketMaxTime() - this.getDamage(stack))));
    }
}
