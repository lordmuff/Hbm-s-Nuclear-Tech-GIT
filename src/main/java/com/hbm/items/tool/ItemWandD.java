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
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.BlockAllocatorStandard;
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
import com.hbm.particle.helper.ExplosionCreator;

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
			
			/*ExplosionVNT vnt = new ExplosionVNT(world, pos.hitVec.xCoord, pos.hitVec.yCoord, pos.hitVec.zCoord, 7);
			vnt.setBlockAllocator(new BlockAllocatorBulkie(60));
			vnt.setBlockProcessor(new BlockProcessorStandard().withBlockEffect(new BlockMutatorBulkie(ModBlocks.block_slag)).setNoDrop());
			vnt.setEntityProcessor(new EntityProcessorStandard());
			vnt.setPlayerProcessor(new PlayerProcessorStandard());
			vnt.setSFX(new ExplosionEffectStandard());
			vnt.explode();*/
			
			//PollutionHandler.incrementPollution(world, pos.blockX, pos.blockY, pos.blockZ, PollutionType.SOOT, 15);
			
			/*int i = pos.blockX >> 4;
			int j = pos.blockZ >> 4;
			
			i = (i << 4) + 8;
			j = (j << 4) + 8;
			Component comp = new RuralHouse1(world.rand, i, j);
			comp.addComponentParts(world, world.rand, new StructureBoundingBox(i, j, i + 32, j + 32));*/
			
			ExplosionVNT vnt = new ExplosionVNT(world, pos.blockX + 0.5, pos.blockY + 1, pos.blockZ + 0.5, 15F);
			vnt.makeStandard();
			vnt.setSFX();
			vnt.setBlockAllocator(new BlockAllocatorStandard(32));
			vnt.explode();

			ExplosionCreator.composeEffectStandard(world, pos.blockX + 0.5, pos.blockY + 0.5, pos.blockZ + 0.5);

			/*for(int i = 0; i < 10; i++) {
				NBTTagCompound data = new NBTTagCompound();
				data.setString("type", "debris");
				PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacketNT(data, pos.blockX + world.rand.nextGaussian() * 3, pos.blockY - 2, pos.blockZ + world.rand.nextGaussian() * 3), new TargetPoint(world.provider.dimensionId, pos.blockX, pos.blockY, pos.blockZ, 100));
			}
			NBTTagCompound data = new NBTTagCompound();
			data.setString("type", "oomph");
			PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacketNT(data, pos.blockX, pos.blockY, pos.blockZ), new TargetPoint(world.provider.dimensionId, pos.blockX, pos.blockY, pos.blockZ, 100));*/

			/*TimeAnalyzer.startCount("setBlock");
			world.setBlock(pos.blockX, pos.blockY, pos.blockZ, Blocks.dirt);
			TimeAnalyzer.startEndCount("getBlock");
			world.getBlock(pos.blockX, pos.blockY, pos.blockZ);
			TimeAnalyzer.endCount();
			TimeAnalyzer.dump();*/
			
			/*TomSaveData data = TomSaveData.forWorld(world);
			data.impact = true;
			data.fire = 0F;
			data.dust = 0F;
			data.markDirty();*/
			
			/*for(int i = -5; i <= 5; i++) {
				for(int j = -5; j <= 5; j++) {
					WorldUtil.setBiome(world, pos.blockX + i, pos.blockZ + j, BiomeGenCraterBase.craterBiome);
				}
			}

			WorldUtil.syncBiomeChange(world, pos.blockX, pos.blockZ);*/
			
			/*EntityTomBlast tom = new EntityTomBlast(world);
			tom.posX = pos.blockX;
			tom.posY = pos.blockY;
			tom.posZ = pos.blockZ;
			tom.destructionRange = 600;
			world.spawnEntityInWorld(tom);*/
			
			/*List<EntityNukeTorex> del = world.getEntitiesWithinAABB(EntityNukeTorex.class, AxisAlignedBB.getBoundingBox(pos.blockX, pos.blockY + 1, pos.blockZ, pos.blockX, pos.blockY + 1, pos.blockZ).expand(50, 50, 50));
			
			if(!del.isEmpty()) {
				for(EntityNukeTorex torex : del) torex.setDead();

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