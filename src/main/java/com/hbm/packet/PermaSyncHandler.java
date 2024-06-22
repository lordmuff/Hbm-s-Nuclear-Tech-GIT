package com.hbm.packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.hbm.dim.CelestialBodyWorldSavedData;
import com.hbm.dim.WorldProviderCelestial;
import com.hbm.dim.trait.CelestialBodyTrait;
import com.hbm.handler.ImpactWorldHandler;
import com.hbm.handler.pollution.PollutionHandler;
import com.hbm.handler.pollution.PollutionHandler.PollutionData;
import com.hbm.handler.pollution.PollutionHandler.PollutionType;
import com.hbm.main.MainRegistry;
import com.hbm.potion.HbmPotion;
import com.hbm.saveddata.SatelliteSavedData;
import com.hbm.saveddata.TomSaveData;
import com.hbm.saveddata.satellites.Satellite;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

/**
 * Utility for permanently synchronizing values every tick with a player in the given context of a world.
 * Uses the Byte Buffer directly instead of NBT to cut back on unnecessary data.
 * @author hbm
 */
public class PermaSyncHandler {
	
	public static HashSet<Integer> boykissers = new HashSet<Integer>();
	public static float[] pollution = new float[PollutionType.values().length];

	public static void writePacket(ByteBuf buf, World world, EntityPlayerMP player) {
		
		/// TOM IMPACT DATA ///
		TomSaveData data = TomSaveData.forWorld(world);
		buf.writeFloat(data.fire);
		buf.writeFloat(data.dust);
		buf.writeBoolean(data.impact);
		buf.writeLong(data.time);
		buf.writeFloat(data.flash);
		buf.writeBoolean(data.divinity);
		/// TOM IMPACT DATA ///

		/// SHITTY MEMES ///
		List<Integer> ids = new ArrayList<Integer>();
		for(Object o : world.playerEntities) {
			EntityPlayer p = (EntityPlayer) o;
			if(p.isPotionActive(HbmPotion.death.id)) {
				ids.add(p.getEntityId());
			}
		}
		buf.writeShort((short) ids.size());
		for(Integer i : ids) buf.writeInt(i);
		/// SHITTY MEMES ///

		/// POLLUTION ///
		PollutionData pollution = PollutionHandler.getPollutionData(world, (int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ));
		if(pollution == null) pollution = new PollutionData();
		for(int i = 0; i < PollutionType.values().length; i++) {
			buf.writeFloat(pollution.pollution[i]);
		}
		/// POLLUTION ///

		/// CBT ///
		HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> traits = CelestialBodyWorldSavedData.getTraits(world);
		if(traits != null) {
			buf.writeBoolean(true); // Has traits marker (since we can have an empty list)
			buf.writeInt(traits.size());

			for(int i = 0; i < CelestialBodyTrait.traitList.size(); i++) {
				Class<? extends CelestialBodyTrait> traitClass = CelestialBodyTrait.traitList.get(i);
				CelestialBodyTrait trait = traits.get(traitClass);

				if(trait != null) {
					buf.writeInt(i); // ID of the trait, in order registered
					trait.writeToBytes(buf);
				}
			}
		} else {
			buf.writeBoolean(false);
		}
		/// CBT ///

		/// SATELLITES ///
		// Only syncs data required for rendering satellites on the client
		HashMap<Integer, Satellite> sats = SatelliteSavedData.getData(world).sats;
		buf.writeInt(sats.size());
		for(Map.Entry<Integer, Satellite> entry : sats.entrySet()) {
			buf.writeInt(entry.getKey());
			buf.writeInt(entry.getValue().getID());
		}
		/// SATELLITES ///

		/// TIME OF DAY ///
		if(world.provider instanceof WorldProviderCelestial) {
			buf.writeBoolean(true);
			buf.writeLong(world.provider.getWorldTime());
		} else {
			buf.writeBoolean(false);
		}
		/// TIME OF DAY ///
	}
	
	public static void readPacket(ByteBuf buf, World world, EntityPlayer player) {

		/// TOM IMPACT DATA ///
		ImpactWorldHandler.lastSyncWorld = player.worldObj;
		ImpactWorldHandler.fire = buf.readFloat();
		ImpactWorldHandler.dust = buf.readFloat();
		ImpactWorldHandler.impact = buf.readBoolean();
		ImpactWorldHandler.time = buf.readLong();
		ImpactWorldHandler.flash = buf.readFloat();
		ImpactWorldHandler.divinity = buf.readBoolean();

		/// TOM IMPACT DATA ///

		//System.out.println("read: " + ImpactWorldHandler.flash);
		//System.out.println("read: " + ImpactWorldHandler.divinity);
		/// SHITTY MEMES ///
		boykissers.clear();
		int ids = buf.readShort();
		for(int i = 0; i < ids; i++) boykissers.add(buf.readInt());
		/// SHITTY MEMES ///

		/// POLLUTION ///
		for(int i = 0; i < PollutionType.values().length; i++) {
			pollution[i] = buf.readFloat();
		}
		/// POLLUTION ///

		/// CBT ///
		try {
			if(buf.readBoolean()) {
				HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> traits = new HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait>();

				int cbtSize = buf.readInt();
				for(int i = 0; i < cbtSize; i++) {
					CelestialBodyTrait trait = CelestialBodyTrait.traitList.get(buf.readInt()).newInstance();
					trait.readFromBytes(buf);

					traits.put(trait.getClass(), trait);
				}

				CelestialBodyWorldSavedData.updateClientTraits(traits);
			} else {
				CelestialBodyWorldSavedData.updateClientTraits(null);
			}

		} catch (Exception ex) {
			// If any exception occurs, stop parsing any more bytes, they'll be unaligned
			// We'll unset the client trait set to prevent any issues

			MainRegistry.logger.catching(ex);
			CelestialBodyWorldSavedData.updateClientTraits(null);

			return;
		}
		/// CBT ///

		/// SATELLITES ///
		int satSize = buf.readInt();
		HashMap<Integer, Satellite> sats = new HashMap<Integer, Satellite>();
		for(int i = 0; i < satSize; i++) {
			sats.put(buf.readInt(), Satellite.create(buf.readInt()));
		}
		SatelliteSavedData.setClientSats(sats);
		/// SATELLITES ///

		/// TIME OF DAY ///
		if(buf.readBoolean()) {
			long localTime = buf.readLong();
			world.provider.setWorldTime(localTime);
		}
		/// TIME OF DAY ///
	}
}
