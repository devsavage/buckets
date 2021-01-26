package io.savagedev.buckets.items;

/*
 * ItemShimmeringBucket.java
 * Copyright (C) 2021 Savage - github.com/devsavage
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
import io.savagedev.buckets.items.base.BaseItem;
import io.savagedev.savagecore.item.ItemHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemShimmeringBucket extends BaseItem
{
    public ItemShimmeringBucket() {
        super(p -> p.maxStackSize(1).maxDamage(256).group(Buckets.modGroup));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack bucket = playerIn.getHeldItem(handIn);
        RayTraceResult target = ItemHelper.rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
        BlockRayTraceResult targetBlock = (BlockRayTraceResult) target;
        BlockPos targetBlockPos = targetBlock.getPos();
        BlockState targetBlockState = worldIn.getBlockState(targetBlock.getPos());

        if(targetBlockState.getBlock() instanceof IBucketPickupHandler) {
            Fluid fluid = ((IBucketPickupHandler)targetBlockState.getBlock()).pickupFluid(worldIn, targetBlockPos, targetBlockState);

            if(fluid != Fluids.EMPTY) {
                SoundEvent soundevent = Fluids.LAVA.getAttributes().getEmptySound();
                if (soundevent == null) {
                    soundevent = SoundEvents.ITEM_BUCKET_FILL_LAVA;
                }

                playerIn.playSound(soundevent, 1.0F, 1.0F);

                int minExp = 3;
                int maxExp = 6;
                int finalExp = minExp + worldIn.rand.nextInt(maxExp - minExp + 1);

                if(!worldIn.isRemote) {
                    int expSplit = ExperienceOrbEntity.getXPSplit(finalExp);
                    finalExp -= expSplit;
                    worldIn.addEntity(new ExperienceOrbEntity(worldIn, playerIn.getPosX() + 0.5D, playerIn.getPosY() + 0.5D, playerIn.getPosZ() + 0.5D, expSplit));
                }

                bucket.damageItem(1, playerIn, (playerEntity -> {
                    playerEntity.sendBreakAnimation(handIn);
                }));

                return ActionResult.resultSuccess(bucket);
            }
        }

        return ActionResult.resultPass(bucket);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }
}
