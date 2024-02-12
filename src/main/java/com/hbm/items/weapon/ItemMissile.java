package com.hbm.items.weapon;

import java.util.List;

import com.hbm.items.ItemCustomLore;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class ItemMissile extends ItemCustomLore {
	
	public final MissileFormFactor formFactor;
	public final MissileTier tier;
	public final MissileFuel fuel;
	
	public ItemMissile(MissileFormFactor form, MissileTier tier) {
		this.formFactor = form;
		this.tier = tier;
		this.fuel = form.defaultFuel;
	public ItemMissile() {
		this.setMaxStackSize(1);
		this.setCreativeTab(MainRegistry.missileTab);
	}

	public static HashMap<Integer, ItemMissile> parts = new HashMap();

	/**
	 * == Chips ==
	 * [0]: inaccuracy
	 *
	 * == Warheads ==
	 * [0]: type
	 * [1]: strength/radius/cluster count
	 * [2]: weight
	 *
	 * == Fuselages ==
	 * [0]: type
	 * [1]: tank size
	 *
	 * == Stability ==
	 * [0]: inaccuracy mod
	 *
	 * == Thrusters ===
	 * [0]: type
	 * [1]: consumption
	 * [2]: lift strength
	 */
	public Object[] attributes;

	public enum PartType {
		CHIP,
		WARHEAD,
		FUSELAGE,
		FINS,
		THRUSTER
	}

	public enum PartSize {

		//for chips
		ANY,
		//for missile tips and thrusters
		NONE,
		//regular sizes, 1.0m, 1.5m and 2.0m
		SIZE_10,
		SIZE_15,
		SIZE_20
	}

	public enum WarheadType {

		HE,
		INC,
		BUSTER,
		CLUSTER,
		NUCLEAR,
		TX,
		N2,
		BALEFIRE,
		SCHRAB,
		TAINT,
		CLOUD,
		TURBINE
	}

	public enum FuelType {

		KEROSENE,
		SOLID,
		HYDROGEN,
		XENON,
		BALEFIRE,
		HYDRAZINE
	}

	public enum Rarity {

		COMMON(EnumChatFormatting.GRAY + "Common"),
		UNCOMMON(EnumChatFormatting.YELLOW + "Uncommon"),
		RARE(EnumChatFormatting.AQUA + "Rare"),
		EPIC(EnumChatFormatting.LIGHT_PURPLE + "Epic"),
		LEGENDARY(EnumChatFormatting.DARK_GREEN + "Legendary"),
		SEWS_CLOTHES_AND_SUCKS_HORSE_COCK(EnumChatFormatting.DARK_AQUA + "Strange");

		String name;

		Rarity(String name) {
			this.name = name;
		}
	}

	public ItemMissile makeChip(float inaccuracy) {

		this.type = PartType.CHIP;
		this.top = PartSize.ANY;
		this.bottom = PartSize.ANY;
		this.attributes = new Object[] { inaccuracy };

		parts.put(this.hashCode(), this);

		return this;
	}
	
	public ItemMissile(MissileFormFactor form, MissileTier tier, MissileFuel fuel) {
		this.formFactor = form;
		this.tier = tier;
		this.fuel = fuel;
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean bool) {
		list.add(EnumChatFormatting.ITALIC + this.tier.display);
		list.add("Fuel: " + this.fuel.display);
		super.addInformation(itemstack, player, list, bool);
	}
	
	public enum MissileFormFactor {
		ABM(MissileFuel.SOLID),
		MICRO(MissileFuel.SOLID),
		V2(MissileFuel.ETHANOL_PEROXIDE),
		STRONG(MissileFuel.KEROSENE_PEROXIDE),
		HUGE(MissileFuel.KEROSENE_LOXY),
		ATLAS(MissileFuel.JETFUEL_LOXY),
		OTHER(MissileFuel.KEROSENE_PEROXIDE);
		
		protected MissileFuel defaultFuel;
		
		private MissileFormFactor(MissileFuel defaultFuel) {
			this.defaultFuel = defaultFuel;
		}
	}
	
	public enum MissileTier {
		TIER0("Tier 0"),
		TIER1("Tier 1"),
		TIER2("Tier 2"),
		TIER3("Tier 3"),
		TIER4("Tier 4");
		
		public String display;
		
		private MissileTier(String display) {
			this.display = display;
		}
	}
	
	public enum MissileFuel {
		SOLID(EnumChatFormatting.GOLD + "Solid Fuel (pre-fueled)"),
		ETHANOL_PEROXIDE(EnumChatFormatting.AQUA + "Ethanol / Hydrogen Peroxide"),
		KEROSENE_PEROXIDE(EnumChatFormatting.BLUE + "Kerosene / Hydrogen Peroxide"),
		KEROSENE_LOXY(EnumChatFormatting.LIGHT_PURPLE + "Kerosene / Liquid Oxygen"),
		JETFUEL_LOXY(EnumChatFormatting.RED + "Jet Fuel / Liquid Oxygen");

		public String display;
		
		private MissileFuel(String display) {
			this.display = display;
		}
	}
}
