package io.savagedev.buckets.items;

/*
 * ItemBlackHoleBucket.java
 * Copyright (C) 2014 - 2022 Savage - github.com/devsavage
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

import com.google.common.collect.Sets;
import io.savagedev.buckets.Buckets;
import io.savagedev.buckets.items.base.BaseItem;
import io.savagedev.buckets.util.LogHelper;
import io.savagedev.buckets.util.ModTooltips;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ItemBlackHoleBucket extends BaseItem
{
    private final Set<Fluid> fluids = Sets.newHashSet(Fluids.LAVA, Fluids.WATER, Fluids.FLOWING_LAVA, Fluids.FLOWING_WATER);
    private final Set<Block> blocks = Sets.newHashSet(Blocks.GRASS, Blocks.FIRE, Blocks.SOUL_FIRE);

    public ItemBlackHoleBucket() {
        super(p -> p.stacksTo(1).durability(256).tab(Buckets.modGroup));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        HitResult target = getPlayerPOVHitResult(world, player, ClipContext.Fluid.ANY);

        if(target.getType() == HitResult.Type.BLOCK) {
            BlockPos targetPos = ((BlockHitResult)target).getBlockPos();
            Fluid fluid = world.getFluidState(targetPos).getType();
            Block block = world.getBlockState(targetPos).getBlock();
            ItemStack bucket = player.getItemInHand(hand);

            if(fluids.contains(fluid)) {
                eviscerate(world, targetPos);

                int dmg = fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER ? 2 : 4;

                bucket.hurtAndBreak(dmg, player, (p) -> {
                    p.broadcastBreakEvent(hand);
                });
            } else if(blocks.contains(block) || block instanceof FlowerBlock) {
                eviscerate(world, targetPos);

                bucket.hurtAndBreak(1, player, (p) -> {
                    p.broadcastBreakEvent(hand);
                });
            }
        }

        return super.use(world, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> tooltip, TooltipFlag p_41424_) {
        if(Screen.hasShiftDown()) tooltip.add(new TextComponent(ModTooltips.BLACK_HOLE));
    }

    private void eviscerate(Level world, BlockPos targetPos) {
        world.setBlockAndUpdate(targetPos, Blocks.AIR.defaultBlockState());

        Random random = world.getRandom();

        for(int i = 0; i < 4; i++) {
            world.addParticle(ParticleTypes.REVERSE_PORTAL, targetPos.getX() + random.nextDouble(), targetPos.getY() + 0.5D, targetPos.getZ() + random.nextDouble(), 0, 0, 0);
        }
    }
}
