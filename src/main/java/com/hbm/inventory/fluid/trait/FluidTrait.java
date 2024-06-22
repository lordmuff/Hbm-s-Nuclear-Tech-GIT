package com.hbm.inventory.fluid.trait;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.inventory.fluid.trait.FluidTraitSimple.*;

import net.minecraft.world.World;

public abstract class FluidTrait {
	
	public static List<Class<? extends FluidTrait>> traitList = new ArrayList<Class<? extends FluidTrait>>();
	public static HashBiMap<String, Class<? extends FluidTrait>> traitNameMap = HashBiMap.create();
	
	static {
		traitNameMap.put("combustible", FT_Combustible.class);		// x
		traitNameMap.put("coolable", FT_Coolable.class);			// x
		traitNameMap.put("corrosive", FT_Corrosive.class);			// x
		traitNameMap.put("flammable", FT_Flammable.class);			// x
		traitNameMap.put("heatable", FT_Heatable.class);			// x
		traitNameMap.put("poison", FT_Poison.class);				// x
		traitNameMap.put("toxin", FT_Toxin.class);					// x
		traitNameMap.put("ventradiation", FT_VentRadiation.class);	// x
		traitNameMap.put("pwrmoderator", FT_PWRModerator.class);	// x

		traitNameMap.put("gaseous", FT_Gaseous.class);
		traitNameMap.put("gaseous_art", FT_Gaseous_ART.class);
		traitNameMap.put("liquid", FT_Liquid.class);
		traitNameMap.put("viscous", FT_Viscous.class);
		traitNameMap.put("plasma", FT_Plasma.class);
		traitNameMap.put("amat", FT_Amat.class);
		traitNameMap.put("leadcontainer", FT_LeadContainer.class);
		traitNameMap.put("delicious", FT_Delicious.class);
		traitNameMap.put("leaded", FT_Leaded.class);
		traitNameMap.put("noid", FT_NoID.class);
		traitNameMap.put("nocontainer", FT_NoContainer.class);
		traitNameMap.put("explosive", FT_EXPLOSIVE.class);	// x
		traitNameMap.put("uk", FT_ULTRAKILL.class);	// x

		//complex traits with values
		registerTrait("corrosive", FT_Corrosive.class);
		registerTrait("flammable", FT_Flammable.class);
		registerTrait("combustible", FT_Combustible.class);
		registerTrait("polluting", FT_Polluting.class);
		registerTrait("heatable", FT_Heatable.class);
		registerTrait("coolable", FT_Coolable.class);
		registerTrait("pwrmoderator", FT_PWRModerator.class);
		registerTrait("poison", FT_Poison.class);
		registerTrait("toxin", FT_Toxin.class);
		registerTrait("ventradiation", FT_VentRadiation.class);
		registerTrait("pheromone", FT_Pheromone.class);
		registerTrait("rocket", FT_Rocket.class);

		//simple traits, "tags"
		registerTrait("gaseous", FT_Gaseous.class);
		registerTrait("gaseous_art", FT_Gaseous_ART.class);
		registerTrait("liquid", FT_Liquid.class);
		registerTrait("viscous", FT_Viscous.class);
		registerTrait("plasma", FT_Plasma.class);
		registerTrait("amat", FT_Amat.class);
		registerTrait("leadcontainer", FT_LeadContainer.class);
		registerTrait("delicious", FT_Delicious.class);
		registerTrait("noid", FT_NoID.class);
		registerTrait("nocontainer", FT_NoContainer.class);
		registerTrait("unsiphonable", FT_Unsiphonable.class);
		registerTrait("uk", FT_ULTRAKILL.class);	// x
		registerTrait("explosive", FT_EXPLOSIVE.class);	// x


	}
	
	private static void registerTrait(String name, Class<? extends FluidTrait> clazz) {
		traitNameMap.put(name, clazz);
		traitList.add(clazz);
	}

	/** Important information that should always be displayed */
	public void addInfo(List<String> info) { }
	/* General names of simple traits which are displayed when holding shift */
	public void addInfoHidden(List<String> info) { }
	
	public void onFluidRelease(World world, int x, int y, int z, FluidTank tank, int overflowAmount, FluidReleaseType type) { }

	public void serializeJSON(JsonWriter writer) throws IOException { }
	public void deserializeJSON(JsonObject obj) { }
	
	public static enum FluidReleaseType {
		VOID,	//if fluid is deleted entirely, shouldn't be used
		BURN,	//if fluid is burned or combusted
		SPILL	//if fluid is spilled via leakage or the container breaking
	}
}
