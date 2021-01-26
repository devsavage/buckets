package io.savagedev.buckets.items.base;

/*
 * BaseItemDamageableBucket.java
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

import io.savagedev.buckets.api.IBucketItem;
import io.savagedev.buckets.items.enums.DamageType;
import io.savagedev.savagecore.item.ItemHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.function.Function;

public class BaseItemDamageableBucket extends BaseItem
{
    protected DamageType damageType;
    protected final Fluid containedFluid;

    public BaseItemDamageableBucket(Function<Properties, Properties> properties, Fluid fluid, DamageType damageType) {
        super(properties);
        this.containedFluid = fluid;
        this.damageType = damageType;
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

                            return ActionResult.resultSuccess(filledBucket);
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

                        switch (this.damageType) {
                            case BIG:
                                bucket.damageItem(100, playerIn, (playerEntity) -> {
                                    playerEntity.sendBreakAnimation(handIn);
                                });

                                if(bucket.isEmpty()) {
                                    return ActionResult.resultPass(new ItemStack(((IBucketItem)this).getEmptyBucketItem()));
                                }
                                break;
                            case NORMAL:
                                bucket.damageItem(1, playerIn, (playerEntity) -> {
                                    playerEntity.sendBreakAnimation(handIn);
                                });
                            case TIMED:
                                return ActionResult.resultPass(bucket);
                            default:
                                return ActionResult.resultSuccess(bucket);
                        }

                        return ActionResult.resultPass(bucket);
                    } else {
                        return ActionResult.resultFail(bucket);
                    }
                }
            } else {
                return ActionResult.resultFail(bucket);
            }
        }
    }

    public boolean attemptPlaceFluid(PlayerEntity playerEntity, World world, BlockPos targetPos, BlockRayTraceResult blockRayTraceResult) {
        if(!(this.containedFluid instanceof FlowingFluid)) {
            return false;
        } else {
            BlockState targetState = world.getBlockState(targetPos);
            Material material = targetState.getMaterial();
            boolean targetStateReplaceable = targetState.isReplaceable(this.containedFluid);
            boolean canContainFluid = canTargetTakeFluid(world, targetPos, targetState);

            if (targetState.isAir(world, targetPos) || targetStateReplaceable || canContainFluid) {
                if (world.getDimensionType().isUltrawarm() && this.containedFluid.isIn(FluidTags.WATER)) {
                    int posX = targetPos.getX();
                    int posY = targetPos.getY();
                    int posZ = targetPos.getZ();
                    world.playSound(playerEntity, targetPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

                    for(int l = 0; l < 8; ++l) {
                        world.addParticle(ParticleTypes.LARGE_SMOKE, (double)posX + Math.random(), (double)posY + Math.random(), (double)posZ + Math.random(), 0.0D, 0.0D, 0.0D);
                    }
                } else if (canContainFluid) {
                    if (((ILiquidContainer)targetState.getBlock()).receiveFluid(world, targetPos, targetState, ((FlowingFluid)this.containedFluid).getStillFluidState(false))) {
                        this.playEmptySound(playerEntity, world, targetPos);
                    }
                } else {
                    if (!world.isRemote && targetStateReplaceable && !material.isLiquid()) {
                        world.destroyBlock(targetPos, true);
                    }

                    this.playEmptySound(playerEntity, world, targetPos);
                    world.setBlockState(targetPos, this.containedFluid.getDefaultState().getBlockState(), 11);
                }

                return true;
            } else {
                return blockRayTraceResult != null && this.attemptPlaceFluid(playerEntity, world, blockRayTraceResult.getPos().offset(blockRayTraceResult.getFace()), null);
            }
        }
    }

    public ItemStack fillBucket(ItemStack emptyBucket, PlayerEntity playerEntity, Item fullBucket) {
        if(playerEntity.isCreative()) {
            return emptyBucket;
        } else {
            emptyBucket.shrink(1);
            if(emptyBucket.isEmpty()) {
                return new ItemStack(fullBucket);
            } else {
                if(!playerEntity.inventory.addItemStackToInventory(new ItemStack(fullBucket))) {
                    playerEntity.dropItem(new ItemStack(fullBucket), false);
                }

                return emptyBucket;
            }
        }
    }

    protected ItemStack getFilledBucket(Fluid fluid) {
        if(this instanceof IBucketItem) {
            if(fluid.getFluid() == Fluids.LAVA) {
                if(((IBucketItem)this).getLavaBucketItem() != null) {
                    return new ItemStack(((IBucketItem)this).getLavaBucketItem());
                }
            } else if(fluid.getFluid() == Fluids.WATER) {
                return new ItemStack(((IBucketItem)this).getWaterBucketItem());
            }

            return new ItemStack(((IBucketItem)this).getEmptyBucketItem());
        }

        return ItemStack.EMPTY;
    }

    protected boolean canTargetTakeFluid(World world, BlockPos targetPos, BlockState targetState) {
        return targetState.getBlock() instanceof ILiquidContainer && ((ILiquidContainer)targetState.getBlock()).canContainFluid(world, targetPos, targetState, this.containedFluid);
    }

    protected void playEmptySound(PlayerEntity playerEntity, IWorld worldIn, BlockPos pos) {
        SoundEvent soundevent = this.containedFluid.getAttributes().getEmptySound();
        if(soundevent == null) soundevent = this.containedFluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
        worldIn.playSound(playerEntity, pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }
}
