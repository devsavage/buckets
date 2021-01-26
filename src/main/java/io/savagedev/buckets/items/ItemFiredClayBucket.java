package io.savagedev.buckets.items;

/*
 * ItemFiredClayBucket.java
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
import io.savagedev.buckets.items.base.BaseItemDamageableBucket;
import io.savagedev.buckets.items.enums.DamageType;
import io.savagedev.savagecore.item.ItemHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class ItemFiredClayBucket extends BaseItemDamageableBucket implements IBucketItem
{
    public ItemFiredClayBucket(Fluid fluid) {
        super(p -> (p.group(Buckets.modGroup).maxStackSize(1).maxDamage(20)), fluid.getFluid(), DamageType.NORMAL);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack bucket = playerIn.getHeldItem(handIn);
        RayTraceResult target = ItemHelper.rayTrace(worldIn, playerIn, this.containedFluid == Fluids.EMPTY ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE);
        ActionResult<ItemStack> bucketUseResult = ForgeEventFactory.onBucketUse(playerIn, worldIn, bucket, target);

        if(bucketUseResult != null) {
            return bucketUseResult;
        }

        if(target.getType() == RayTraceResult.Type.MISS) {
            return ActionResult.resultPass(bucket);
        } else if(target.getType() != RayTraceResult.Type.BLOCK) {
            return ActionResult.resultPass(bucket);
        } else {
            BlockRayTraceResult targetBlock = (BlockRayTraceResult) target;
            BlockPos targetBlockPos = targetBlock.getPos();
            Direction targetBlockDirection = targetBlock.getFace();
            BlockPos targetBlockPosOffset = targetBlockPos.offset(targetBlockDirection);

            if(worldIn.isBlockModifiable(playerIn, targetBlockPos) && playerIn.canPlayerEdit(targetBlockPos, targetBlockDirection, bucket)) {
                if(this.containedFluid == Fluids.EMPTY) {
                    BlockState targetBlockState = worldIn.getBlockState(targetBlockPos);

                    if(targetBlockState.getBlock() instanceof IBucketPickupHandler) {
                        Fluid fluid = ((IBucketPickupHandler)targetBlockState.getBlock()).pickupFluid(worldIn, targetBlockPos, targetBlockState);

                        if(fluid != Fluids.EMPTY) {
                            SoundEvent soundevent = this.containedFluid.getAttributes().getEmptySound();
                            if (soundevent == null) {
                                soundevent = fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL;
                            }

                            playerIn.playSound(soundevent, 1.0F, 1.0F);

                            ItemStack filledBucket = this.fillBucket(bucket, playerIn, getFilledBucket(fluid.getFluid()).getItem());

                            return ActionResult.resultPass(filledBucket);
                        }
                    }

                    return ActionResult.resultFail(bucket);
                } else {
                    BlockState targetBlockState = worldIn.getBlockState(targetBlockPos);
                    BlockPos getBlockPos = canTargetTakeFluid(worldIn, targetBlockPos, targetBlockState) ? targetBlockPos : targetBlockPosOffset;
                    if (this.attemptPlaceFluid(playerIn, worldIn, targetBlockPosOffset, targetBlock)) {
                        if (playerIn instanceof ServerPlayerEntity) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)playerIn, getBlockPos, bucket);
                        }

                        if(bucket.getDamage() + 1 < bucket.getMaxDamage()) {
                            bucket.damageItem(1, playerIn, (playerEntity) -> {
                                playerEntity.sendBreakAnimation(Hand.MAIN_HAND);
                            });

                            ItemStack emptyBucket = new ItemStack(this.getEmptyBucketItem());
                            emptyBucket.setDamage(bucket.getDamage());

                            bucket.shrink(1);

                            return ActionResult.resultSuccess(emptyBucket);
                        } else{
                            bucket.damageItem(1, playerIn, (playerEntity) -> {
                                playerEntity.sendBreakAnimation(Hand.MAIN_HAND);
                            });

                            return ActionResult.resultConsume(bucket);
                        }
                    } else {
                        return ActionResult.resultFail(bucket);
                    }
                }
            } else {
                return ActionResult.resultFail(bucket);
            }
        }
    }

    @Override
    public ItemStack fillBucket(ItemStack emptyBucket, PlayerEntity playerEntity, Item fullBucket) {
        if(playerEntity.isCreative()) {
            return emptyBucket;
        } else {
            if(emptyBucket.getDamage() + 1 < emptyBucket.getMaxDamage()) {
                emptyBucket.damageItem(1, playerEntity, (event) -> {
                    event.sendBreakAnimation(Hand.MAIN_HAND);
                });

                ItemStack newFullBucket = new ItemStack(fullBucket);
                newFullBucket.setDamage(emptyBucket.getDamage());

                emptyBucket.shrink(1);

                if(emptyBucket.isEmpty()) {
                    return newFullBucket;
                } else {
                    if(!playerEntity.inventory.addItemStackToInventory(newFullBucket)) {
                        playerEntity.dropItem(newFullBucket, false);
                    }

                    return emptyBucket;
                }
            } else {
                return ItemStack.EMPTY;
            }
        }
    }

    @Override
    public Item getEmptyBucketItem() {
        return ModItems.FIRED_CLAY_BUCKET.get();
    }

    @Override
    public Item getLavaBucketItem() {
        return ModItems.FIRED_CLAY_BUCKET_LAVA.get();
    }

    @Override
    public Item getWaterBucketItem() {
        return ModItems.FIRED_CLAY_BUCKET_WATER.get();
    }
}
