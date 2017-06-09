/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2017 TechReborn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package techreborn.events;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.oredict.OreDictionary;
import reborncore.common.registration.RebornRegistry;
import reborncore.common.registration.impl.ConfigRegistry;
import reborncore.common.util.ItemUtils;
import techreborn.lib.ModInfo;

import java.util.HashMap;

@RebornRegistry(modID = ModInfo.MOD_ID)
public class OreUnifier {

	@ConfigRegistry(config = "misc", category = "general", key = "OreUnifier", comment = "Convert any ore itemstacks into Tech Reborn ores")
	public static boolean oreUnifier = false;

	public static HashMap<String, ItemStack> oreHash = new HashMap<>();

	public static void registerOre(String name, ItemStack ore) {
		oreHash.put(name, ore);
		OreDictionary.registerOre(name, ore);
	}

	public static void registerOre(String name, Item ore) {
		registerOre(name, new ItemStack(ore));
	}

	public static void registerOre(String name, Block ore) {
		registerOre(name, new ItemStack(ore));
	}

	@SubscribeEvent
	public void itemTick(TickEvent.PlayerTickEvent event) {
		if (oreUnifier && !event.player.world.isRemote
			&& event.player.world.getTotalWorldTime() % 10 == 0) {
			if (event.player.getHeldItem(EnumHand.MAIN_HAND) != ItemStack.EMPTY) {
				int[] oreIds = OreDictionary.getOreIDs(event.player.getHeldItem(EnumHand.MAIN_HAND));
				for (int id : oreIds) {
					String oreName = OreDictionary.getOreName(id);
					if (oreHash.containsKey(oreName)) {
						if (ItemUtils.isItemEqual(event.player.getHeldItem(EnumHand.MAIN_HAND), oreHash.get(oreName),
							true, true, true)
							&& !ItemUtils.isItemEqual(event.player.getHeldItem(EnumHand.MAIN_HAND),
							oreHash.get(oreName), true, true, false)) {
							ItemStack stack = oreHash.get(oreName).copy();
							stack.setCount(event.player.getHeldItem(EnumHand.MAIN_HAND).getCount());
							stack.setTagCompound(event.player.getHeldItem(EnumHand.MAIN_HAND).getTagCompound());
							event.player.inventory.setInventorySlotContents(event.player.inventory.currentItem, stack);
						}
					}
				}
			}
		}
	}

}
