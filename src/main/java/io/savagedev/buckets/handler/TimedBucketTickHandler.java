package io.savagedev.buckets.handler;

/*
 * TimedBucketTickHandler.java
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

import io.savagedev.buckets.items.ItemTimedBucket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TimedBucketTickHandler
{
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(event.phase == TickEvent.Phase.START && event.player.level.getGameTime() % 20 == 0) {
            Player player = event.player;
            Inventory playerInv = player.getInventory();
            int invSize = playerInv.getContainerSize();

            for(int i = 0; i < invSize; i++) {
                ItemStack stackInSlot = playerInv.getItem(i);

                // Removes the visual update of the bucket
                if(stackInSlot.getItem() instanceof ItemTimedBucket) {
                   stackInSlot.hurt(1, event.player.getRandom(), event.player instanceof ServerPlayer ? (ServerPlayer) event.player : null);

                   if(stackInSlot.getDamageValue() >= stackInSlot.getMaxDamage()) {
                       stackInSlot.shrink(1);
                   }
                }
            }
        }
    }
}
