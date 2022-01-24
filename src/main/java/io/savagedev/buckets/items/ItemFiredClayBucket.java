package io.savagedev.buckets.items;

/*
 * ItemFiredClayBucket.java
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
import io.savagedev.buckets.init.ModItems;
import io.savagedev.buckets.items.base.BaseItemDamageableBucket;
import io.savagedev.buckets.items.enums.DamageType;
import io.savagedev.savagecore.item.ItemHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.ForgeEventFactory;

public class ItemFiredClayBucket extends BaseItemDamageableBucket implements IBucketItem
{
    public ItemFiredClayBucket(Fluid fluid) {
        super(p -> (p.tab(Buckets.modGroup).stacksTo(1).durability(20)), fluid, DamageType.NORMAL);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack bucket = playerIn.getItemInHand(handIn);
        HitResult target = ItemHelper.rayTrace(worldIn, playerIn, this.containedFluid == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
        InteractionResultHolder<ItemStack> bucketUseResult = ForgeEventFactory.onBucketUse(playerIn, worldIn, bucket, target);

        if(bucketUseResult != null) {
            return bucketUseResult;
        }

        if(target.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(bucket);
        } else if(target.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(bucket);
        } else {
            BlockHitResult targetBlock = (BlockHitResult) target;
            BlockPos targetBlockPos = targetBlock.getBlockPos();
            Direction targetBlockDirection = targetBlock.getDirection();
            BlockPos targetBlockPosOffset = targetBlockPos.relative(targetBlockDirection);

            if(worldIn.mayInteract(playerIn, targetBlockPos) && playerIn.mayUseItemAt(targetBlockPos, targetBlockDirection, bucket)) {
                BlockState targetBlockState = worldIn.getBlockState(targetBlockPos);

                if(this.containedFluid == Fluids.EMPTY) {
                    if(targetBlockState.getBlock() instanceof BucketPickup targetFluidPickup) {
                        ItemStack fluidBucketStack = targetFluidPickup.pickupBlock(worldIn, targetBlockPos, targetBlockState);
                        Fluid fluid = targetBlockState.getFluidState().getType();

                        if(fluid != Fluids.EMPTY) {
                            targetFluidPickup.getPickupSound().ifPresent((event) -> {
                                playerIn.playSound(event, 1.0F, 1.0F);
                            });

                            ItemStack filledBucket = this.fillBucket(bucket, playerIn, getFilledBucket(fluid).getItem());

                            return InteractionResultHolder.success(filledBucket);
                        }
                    }

                    return InteractionResultHolder.fail(bucket);
                } else {
                    BlockPos getBlockPos = canTargetTakeFluid(worldIn, targetBlockPos, targetBlockState) ? targetBlockPos : targetBlockPosOffset;
                    if (this.attemptPlaceFluid(playerIn, worldIn, targetBlockPosOffset, targetBlock)) {
                        if (playerIn instanceof ServerPlayer) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)playerIn, getBlockPos, bucket);
                        }

                        if(bucket.getDamageValue() + 1 < bucket.getMaxDamage()) {
                            bucket.hurtAndBreak(1, playerIn, (playerEntity) -> {
                                playerEntity.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                            });

                            ItemStack emptyBucket = new ItemStack(this.getEmptyBucketItem());
                            emptyBucket.setDamageValue(bucket.getDamageValue());

                            bucket.shrink(1);

                            return InteractionResultHolder.success(emptyBucket);
                        } else{
                            bucket.hurtAndBreak(1, playerIn, (playerEntity) -> {
                                playerEntity.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                            });

                            return InteractionResultHolder.consume(bucket);
                        }
                    } else {
                        return InteractionResultHolder.fail(bucket);
                    }
                }
            } else {
                return InteractionResultHolder.fail(bucket);
            }
        }
    }

    @Override
    public ItemStack fillBucket(ItemStack emptyBucket, Player playerEntity, Item fullBucket) {
        if(playerEntity.isCreative()) {
            return emptyBucket;
        } else {
            if(emptyBucket.getDamageValue() + 1 < emptyBucket.getMaxDamage()) {
                emptyBucket.hurtAndBreak(1, playerEntity, (event) -> {
                    event.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                });

                ItemStack newFullBucket = new ItemStack(fullBucket);
                newFullBucket.setDamageValue(emptyBucket.getDamageValue());

                emptyBucket.shrink(1);

                if(emptyBucket.isEmpty()) {
                    return newFullBucket;
                } else {
                    if(!playerEntity.getInventory().add(newFullBucket)) {
                        playerEntity.drop(newFullBucket, false);
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
