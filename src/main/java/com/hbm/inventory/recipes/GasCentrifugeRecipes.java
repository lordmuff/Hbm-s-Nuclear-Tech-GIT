package com.hbm.inventory.recipes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.hbm.inventory.FluidStack;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemFluidIcon;
import com.hbm.util.I18nUtil;

import gregapi.data.MT;
import net.minecraft.item.ItemStack;


import net.minecraftforge.oredict.OreDictionary;

import gregapi.util.OM.*;
import static gregapi.data.AM.*;
import static gregapi.data.OP.*;

public class GasCentrifugeRecipes {

	public static class PseudoFluidType {

		public static HashMap<String, PseudoFluidType> types = new HashMap();
		public static PseudoFluidType NONE		= new PseudoFluidType("NONE",		0,		0,		null,		false,	(ItemStack[])null);
		
		public static PseudoFluidType HEUF6		= new PseudoFluidType("HEUF6",		300,	0,		NONE,		true,	new ItemStack(ModItems.nugget_u238, 2), new ItemStack(ModItems.nugget_u235, 1), new ItemStack(ModItems.fluorite, 1));
		public static PseudoFluidType MEUF6		= new PseudoFluidType("MEUF6",		200,	100,	HEUF6,		false,	new ItemStack(ModItems.nugget_u238, 1));
		public static PseudoFluidType LEUF6 	= new PseudoFluidType("LEUF6",		300,	200,	MEUF6,		false,	new ItemStack(ModItems.nugget_u238, 1), new ItemStack(ModItems.fluorite, 1));
		public static PseudoFluidType NUF6 		= new PseudoFluidType("NUF6",		400,	300,	LEUF6,		false,	new ItemStack(ModItems.nugget_u238, 1));

		public static PseudoFluidType PF6		= new PseudoFluidType("PF6",		300,	0,		NONE,		false,	new ItemStack(ModItems.nugget_pu238, 1), new ItemStack(ModItems.nugget_pu_mix, 2), new ItemStack(ModItems.fluorite, 1));

		public static PseudoFluidType MUD_HEAVY 		= new PseudoFluidType("MUD_HEAVY",	500,	0,		NONE,		false,	new ItemStack(ModItems.powder_iron, 1), new ItemStack(ModItems.dust, 1), new ItemStack(ModItems.nuclear_waste_tiny, 1));
		public static PseudoFluidType MUD 		= new PseudoFluidType("MUD"	,		1000,	500,	MUD_HEAVY,	false,	new ItemStack(ModItems.powder_lead, 1), new ItemStack(ModItems.dust, 1));


		public static PseudoFluidType MINSOLEE 		= new PseudoFluidType("MINSOLEE",	1000, 0,		NONE,		false,	new ItemStack(ModItems.crystal_cleaned, 1));
		public static PseudoFluidType MINSOLE 		= new PseudoFluidType("MINSOLE",		1000, 1000,	MINSOLEE,	false,	new ItemStack(ModItems.powder_iron, 1));



		public static PseudoFluidType NQVBSCOMP2 		= new PseudoFluidType("NQVBSCOMP2",		600, 0,NONE,	false, nugget.mat(MT.Nq_528, 17), nugget.mat(MT.Nq_522, 5), new ItemStack(ModItems.fluorite, 12));

		public static PseudoFluidType NQVBSCOMP 		= new PseudoFluidType("NQVBSCOMP",		1000, 200,	NQVBSCOMP2,	false, nugget.mat(MT.Nq_528, 17), nugget.mat(MT.Nq_522, 5), new ItemStack(ModItems.powder_boron, 8));


		//TODO for bob: consider more fluid types
		//Schraranium Trisulfide for more schrab-containing, pre-SILEX processing using the crystals?
		//Gaseous Nuclear Waste: because why not? Large inputs could output Xe-135 and maybe some other fun stuff...
		//

		public String name;
		int fluidConsumed;
		int fluidProduced;
		PseudoFluidType outputFluid;
		boolean isHighSpeed;
		ItemStack[] output;
		
		PseudoFluidType(String name, int fluidConsumed, int fluidProduced, PseudoFluidType outputFluid, boolean isHighSpeed, ItemStack... output) {
			this.name = name;
			this.fluidConsumed = fluidConsumed;
			this.fluidProduced = fluidProduced;
			this.outputFluid = outputFluid;
			this.isHighSpeed = isHighSpeed; 
			this.output = output;
			types.put(name, this);
		}
		
		public int getFluidConsumed() {				return this.fluidConsumed; }
		public int getFluidProduced() {				return this.fluidProduced; }
		public PseudoFluidType getOutputType() {	return this.outputFluid; }
		public ItemStack[] getOutput() {			return this.output; }
		public boolean getIfHighSpeed() {			return this.isHighSpeed; }
		public String getName() {					return I18nUtil.resolveKey("hbmpseudofluid.".concat(this.name.toLowerCase(Locale.US))); }
		
	}
		
	/* Recipe NEI Handler */
	//Fluid input; ItemStack[] outputs, isHighSpeed, # of centrifuges
	private static Map<FluidStack, Object[]> gasCent = new HashMap();
	
	//Iterators are lots of fun
	public static Map<Object, Object[]> getGasCentrifugeRecipes() {
		Map<Object, Object[]> recipes = new HashMap<Object, Object[]>();
		Iterator itr = gasCent.entrySet().iterator();
		
		while(itr.hasNext()) {
			Map.Entry<Object, Object[]> entry = (Entry) itr.next();
			FluidStack input = (FluidStack) entry.getKey();
			ItemStack[] out = new ItemStack[4];
			ItemStack[] outputs = (ItemStack[]) entry.getValue()[0];
			
			for(int j = 0; j < outputs.length; j++) {
				out[j] = outputs[j].copy();
			}
			for(int j = 0; j < 4; j++)
				if(out[j] == null)
					out[j] = new ItemStack(ModItems.nothing);
			
			recipes.put(ItemFluidIcon.make(input.type, input.fill), new Object[] { out, entry.getValue()[1], entry.getValue()[2] });
		}
		
		return recipes;
	}
	
	public static HashMap<FluidType, PseudoFluidType> fluidConversions = new HashMap();
	public static void register() {

		fluidConversions.put(Fluids.UF6, PseudoFluidType.NUF6);
		fluidConversions.put(Fluids.PUF6, PseudoFluidType.PF6);
		fluidConversions.put(Fluids.WATZ, PseudoFluidType.MUD);

		if(OreDictionary.doesOreNameExist("ingotNaquadah"))
		{
			gasCent.put(new FluidStack(1000, Fluids.NQVBSCOMPLEX), new Object[]{new ItemStack[]
					{nugget.mat(MT.Nq_528, 17), nugget.mat(MT.Nq_522, 5), new ItemStack(ModItems.fluorite, 12), new ItemStack(ModItems.powder_boron, 8)}, true, 2});
		}
	}
}
