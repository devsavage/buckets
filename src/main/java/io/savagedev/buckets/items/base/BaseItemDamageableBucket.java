package io.savagedev.buckets.items.base;

/*
 * BaseItemDamageableBucket.java
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

import io.savagedev.buckets.api.IBucketItem;
import io.savagedev.buckets.items.enums.DamageType;
import io.savagedev.buckets.util.LogHelper;
import io.savagedev.savagecore.item.ItemHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.function.Function;

public class BaseItemDamageableBucket extends BaseItem
{
    protected DamageType damageType;
    protected final Fluid containedFluid;

    public BaseItemDamageableBucket(Function<Properties, Properties> properties, Fluid fluid, DamageType damageType) {
        super(properties);
        this.containedFluid = fluid;
        this.fluidSupplier = containedFluid.delegate;
        this.damageType = damageType;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack bucket = playerIn.getItemInHand(handIn);

        if(playerIn.isShiftKeyDown()) {
            if(this.containedFluid != Fluids.EMPTY) {
                ItemStack emptyBucket = this.fillBucket(bucket, playerIn, new ItemStack(((IBucketItem)this).getEmptyBucketItem()).getItem());

                return InteractionResultHolder.success(emptyBucket);
            }
        }

        HitResult target = getPlayerPOVHitResult(worldIn, playerIn, this.containedFluid == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
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

                        switch (this.damageType) {
                            case BIG:
                                bucket.hurtAndBreak(100, playerIn, (playerEntity) -> {
                                    playerEntity.broadcastBreakEvent(handIn);
                                });

                                if(bucket.isEmpty()) {
                                    return InteractionResultHolder.pass(new ItemStack(((IBucketItem)this).getEmptyBucketItem()));
                                }
                                break;
                            case NORMAL:
                                bucket.hurtAndBreak(1, playerIn, (playerEntity) -> {
                                    playerEntity.broadcastBreakEvent(handIn);
                                });
                            case TIMED:
                                return InteractionResultHolder.pass(bucket);
                            default:
                                return InteractionResultHolder.success(bucket);
                        }

                        return InteractionResultHolder.pass(bucket);
                    } else {
                        return InteractionResultHolder.fail(bucket);
                    }
                }
            } else {
                return InteractionResultHolder.fail(bucket);
            }
        }
    }

    public boolean attemptPlaceFluid(Player playerEntity, Level world, BlockPos targetPos, BlockHitResult blockRayTraceResult) {
        if(!(this.containedFluid instanceof FlowingFluid)) {
            return false;
        } else {
            BlockState targetState = world.getBlockState(targetPos);
            Material material = targetState.getMaterial();
            boolean targetStateReplaceable = targetState.canBeReplaced(this.containedFluid);
            boolean canContainFluid = canTargetTakeFluid(world, targetPos, targetState);

            if (targetState.isAir() || targetStateReplaceable || canContainFluid) {
                if (world.dimensionType().ultraWarm() && this.containedFluid.is(FluidTags.WATER)) {
                    int posX = targetPos.getX();
                    int posY = targetPos.getY();
                    int posZ = targetPos.getZ();
                    world.playSound(playerEntity, targetPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

                    for(int l = 0; l < 8; ++l) {
                        world.addParticle(ParticleTypes.LARGE_SMOKE, (double)posX + Math.random(), (double)posY + Math.random(), (double)posZ + Math.random(), 0.0D, 0.0D, 0.0D);
                    }
                } else if (canContainFluid) {
                    if (((LiquidBlockContainer)targetState.getBlock()).placeLiquid(world, targetPos, targetState, ((FlowingFluid)this.containedFluid).getSource(false))) {
                        this.playEmptySound(playerEntity, world, targetPos);
                    }
                } else {
                    if (!world.isClientSide && targetStateReplaceable && !material.isLiquid()) {
                        world.destroyBlock(targetPos, true);
                    }

                    this.playEmptySound(playerEntity, world, targetPos);
                    world.setBlock(targetPos, this.containedFluid.defaultFluidState().createLegacyBlock(), 11);
                }

                return true;
            } else {
                return blockRayTraceResult != null && this.attemptPlaceFluid(playerEntity, world, blockRayTraceResult.getBlockPos().relative(blockRayTraceResult.getDirection()), null);
            }
        }
    }

    public ItemStack fillBucket(ItemStack emptyBucket, Player playerEntity, Item fullBucket) {
        if(playerEntity.isCreative()) {
            return emptyBucket;
        } else {
            emptyBucket.shrink(1);
            if(emptyBucket.isEmpty()) {
                return new ItemStack(fullBucket);
            } else {
                if(!playerEntity.getInventory().add(new ItemStack(fullBucket))) {
                    playerEntity.drop(new ItemStack(fullBucket), false);
                }

                return emptyBucket;
            }
        }
    }

    protected ItemStack getFilledBucket(Fluid fluid) {
        if(this instanceof IBucketItem) {
            if(fluid == Fluids.LAVA) {
                if(((IBucketItem)this).getLavaBucketItem() != null) {
                    return new ItemStack(((IBucketItem)this).getLavaBucketItem());
                }
            } else if(fluid == Fluids.WATER) {
                return new ItemStack(((IBucketItem)this).getWaterBucketItem());
            }

            return new ItemStack(((IBucketItem)this).getEmptyBucketItem());
        }

        return ItemStack.EMPTY;
    }

    protected boolean canTargetTakeFluid(Level world, BlockPos targetPos, BlockState targetState) {
        return targetState.getBlock() instanceof LiquidBlockContainer && ((LiquidBlockContainer)targetState.getBlock()).canPlaceLiquid(world, targetPos, targetState, this.containedFluid);
    }

    protected void playEmptySound(Player playerEntity, Level worldIn, BlockPos pos) {
        SoundEvent soundevent = this.containedFluid.getAttributes().getEmptySound();
        if(soundevent == null) soundevent = this.containedFluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        worldIn.playSound(playerEntity, pos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private final java.util.function.Supplier<? extends Fluid> fluidSupplier;
    public Fluid getFluid() { return fluidSupplier.get(); }
}
