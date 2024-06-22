package com.hbm.items.tool;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.WorldConfig;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.mob.EntityDoner;
import com.hbm.entity.mob.EntityGhost;
import com.hbm.handler.ImpactWorldHandler;
import com.hbm.lib.Library;
import com.hbm.main.ModEventHandlerClient;
import com.hbm.saveddata.TomSaveData;
import com.hbm.util.TrackerUtil;
import com.hbm.world.feature.OilBubble;
import com.hbm.world.generator.DungeonToolbox;
import com.hbm.config.SpaceConfig;
import com.hbm.dim.CelestialBody;
import com.hbm.dim.DebugTeleporter;
import com.hbm.dim.SolarSystem;
import com.hbm.dim.trait.CBT_Atmosphere;
import com.hbm.dim.trait.CBT_Atmosphere.FluidEntry;
import com.hbm.entity.mob.EntityWarBehemoth;
import com.hbm.lib.Library;
import com.hbm.saveddata.TomSaveData;
import com.hbm.world.dungeon.Silo;

import cpw.mods.fml.common.eventhandler.Event.Result;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import com.hbm.entity.mob.EntityDoner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.ForgeEventFactory;

public class ItemWandD extends Item {


	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(world.isRemote)
			return stack;
		
		MovingObjectPosition pos = Library.rayTrace(player, 500, 1, false, true, false);
		
		if(pos != null) {

			if(stack.stackTagCompound == null)
				stack.stackTagCompound = new NBTTagCompound();

			if(!player.isSneaking()) {
				int targetId = stack.stackTagCompound.getInteger("dim");
				if(targetId == 0) targetId++; // skip blank

				SolarSystem.Body target = SolarSystem.Body.values()[targetId];

				DebugTeleporter.teleport(player, target.getBody().dimensionId, player.posX, 300, player.posZ, true);
				player.addChatMessage(new ChatComponentText("Teleported to: " + target.getBody().getUnlocalizedName()));

			} else {
				int targetId = stack.stackTagCompound.getInteger("dim");
				targetId++;

				if(targetId >= SolarSystem.Body.values().length) {
					targetId = 1;
				}

				stack.stackTagCompound.setInteger("dim", targetId);

				SolarSystem.Body target = SolarSystem.Body.values()[targetId];

				player.addChatMessage(new ChatComponentText("Set teleport target to: " + target.getBody().getUnlocalizedName()));
			}
		} else {
			// TESTING: View atmospheric data
			CBT_Atmosphere atmosphere = CelestialBody.getTrait(world, CBT_Atmosphere.class);

			boolean isVacuum = true;
			if(atmosphere != null) {
				for(FluidEntry entry : atmosphere.fluids) {
					// if(entry.pressure > 0.001) {
						player.addChatMessage(new ChatComponentText("Atmosphere: " + entry.fluid.getUnlocalizedName() + " - " + entry.pressure + "bar"));
						isVacuum = false;
					// }
				}
			}

			if(isVacuum)
				player.addChatMessage(new ChatComponentText("Atmosphere: NEAR VACUUM"));
		}

		return stack;
	}


	public void generateVein(World world, int startX, int startY, int startZ, Block oreBlock, int veinSize) {
	    Random rand = new Random();

	    LinkedList<int[]> blocksToCheck = new LinkedList<>();
	    blocksToCheck.add(new int[]{ startX, startY, startZ });

	    int blocksChanged = 0;

	    while (!blocksToCheck.isEmpty() && blocksChanged < veinSize) {
	        int[] coords = blocksToCheck.poll();
	        int x = coords[0], y = coords[1], z = coords[2];

	        if (world.getBlock(x, y, z) == Blocks.stone) {
	            world.setBlock(x, y, z, oreBlock);
	            blocksChanged++;

	            for (int dx = -1; dx <= 1; dx++) {
	                for (int dy = -1; dy <= 1; dy++) {
	                    for (int dz = -1; dz <= 1; dz++) {
	                        if (rand.nextInt(100) < 20) {  // Reduced to 20% chance to continue the vein
	                            blocksToCheck.add(new int[]{ x + dx, y + dy, z + dz });
	                        }
	                    }
	                }
	            }
	        }
	    }
	}
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool)
	{
		list.add("Used for debugging purposes.");

		if(stack.stackTagCompound != null) {
			int targetId = stack.stackTagCompound.getInteger("dim");
			SolarSystem.Body target = SolarSystem.Body.values()[targetId];

			list.add("Teleportation target: " + target.getBody().getUnlocalizedName());
		}
	}
}