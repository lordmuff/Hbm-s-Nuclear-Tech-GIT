package com.hbm.dim.Ike;

import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.*;

import com.hbm.blocks.ModBlocks;
import com.hbm.dim.ChunkProviderCelestial;
import com.hbm.dim.mapgen.MapGenTiltedSpires;

import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraftforge.event.terraingen.TerrainGen;

public class ChunkProviderIke extends ChunkProviderCelestial {
	
	private MapGenBase caveGenerator = new MapGenCaves();
	private MapGenTiltedSpires spires = new MapGenTiltedSpires(6, 6, 0F);

	public ChunkProviderIke(World world, long seed, boolean hasMapFeatures) {
		super(world, seed, hasMapFeatures);
		caveGenerator = TerrainGen.getModdedMapGen(caveGenerator, CAVE);
		spires = (MapGenTiltedSpires) TerrainGen.getModdedMapGen(spires, CUSTOM);

		spires.rock = ModBlocks.ike_stone;
		spires.regolith = ModBlocks.ike_regolith;
		spires.mid = 86;

		stoneBlock = ModBlocks.ike_stone;
	}

	@Override
	public BlockMetaBuffer getChunkPrimer(int x, int z) {
		BlockMetaBuffer buffer = super.getChunkPrimer(x, z);

		spires.func_151539_a(this, worldObj, x, z, buffer.blocks);
		caveGenerator.func_151539_a(this, worldObj, x, z, buffer.blocks);
		
		return buffer;
	}

}
