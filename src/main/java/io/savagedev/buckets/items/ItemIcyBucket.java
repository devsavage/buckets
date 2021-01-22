package io.savagedev.buckets.items;

/*
 * ItemIcyBucket.java
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
import io.savagedev.buckets.items.base.BaseItem;
import io.savagedev.savagecore.item.ItemHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.function.Function;

public class ItemIcyBucket extends BaseItem
{
    public ItemIcyBucket() {
        super(p -> p.maxStackSize(1).maxDamage(256).group(Buckets.modGroup));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack bucket = playerIn.getHeldItem(handIn);
        RayTraceResult target = ItemHelper.rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
        BlockRayTraceResult targetBlock = (BlockRayTraceResult) target;
        BlockPos targetBlockPos = targetBlock.getPos();
        Direction targetBlockDirection = targetBlock.getFace();

        if(worldIn.isBlockModifiable(playerIn, targetBlockPos) && playerIn.canPlayerEdit(targetBlockPos, targetBlockDirection, bucket)) {
            BlockState targetBlockState = worldIn.getBlockState(targetBlockPos);

            if(targetBlockState.getBlock() instanceof IBucketPickupHandler) {
                Fluid fluid = ((IBucketPickupHandler)targetBlockState.getBlock()).pickupFluid(worldIn, targetBlockPos, targetBlockState);

                if(fluid != Fluids.EMPTY) {
                    if(fluid == Fluids.LAVA) {
                        if(!playerIn.inventory.addItemStackToInventory(new ItemStack(Items.OBSIDIAN, 2))) {
                            playerIn.dropItem(new ItemStack(Items.OBSIDIAN, 2), false);
                        }
                    } else if(fluid == Fluids.WATER) {
                        if(!playerIn.inventory.addItemStackToInventory(new ItemStack(Items.ICE ))) {
                            playerIn.dropItem(new ItemStack(Items.ICE ), false);
                        }
                    }

                    bucket.damageItem(1, playerIn, (playerEntity) -> {
                        playerEntity.sendBreakAnimation(Hand.MAIN_HAND);
                    });

                    return ActionResult.resultPass(bucket);
                }
            }

            return ActionResult.resultFail(bucket);
        } else {
            return ActionResult.resultFail(bucket);
        }
    }
}
