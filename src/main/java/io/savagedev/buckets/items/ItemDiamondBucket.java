package io.savagedev.buckets.items;

/*
 * ItemDiamondBucket.java
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
import io.savagedev.buckets.api.IBucketItem;
import io.savagedev.buckets.init.ModItems;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;

public class ItemDiamondBucket extends BaseItemDamageableBucket implements IBucketItem
{
    public ItemDiamondBucket(Fluid fluid) {
        super(p -> (p.group(Buckets.modGroup).maxStackSize(1).maxDamage(256)), fluid);
    }

    @Override
    public Item getEmptyBucketItem() {
        return ModItems.DIAMOND_BUCKET_EMPTY.get();
    }

    @Override
    public Item getLavaBucketItem() {
        return ModItems.DIAMOND_BUCKET_LAVA.get();
    }

    @Override
    public Item getWaterBucketItem() {
        return ModItems.DIAMOND_BUCKET_WATER.get();
    }
}
