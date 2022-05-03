package com.hbm.items.machine;

import java.util.List;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.IItemControlReceiver;
import com.hbm.items.ModItems;
import com.hbm.main.MainRegistry;
import com.hbm.util.I18nUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemFluidIDMulti extends Item implements IItemFluidIdentifier, IItemControlReceiver {

	IIcon overlayIcon;

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		
		if(!world.isRemote && !player.isSneaking()) {
			FluidType primary = getType(stack, true);
			FluidType secondary = getType(stack, false);
			setType(stack, secondary, true);
			setType(stack, primary, false);
			world.playSoundAtEntity(player, "random.orb", 0.25F, 1.25F);
		}
		
		if(world.isRemote && player.isSneaking()) {
			player.openGui(MainRegistry.instance, ModItems.guiID_item_fluid, world, 0, 0, 0);
		}
		
		return stack;
	}

	@Override
	public void receiveControl(ItemStack stack, NBTTagCompound data) {
		if(data.hasKey("primary")) {
			setType(stack, Fluids.fromID(data.getInteger("primary")), true);
		}
		if(data.hasKey("secondary")) {
			setType(stack, Fluids.fromID(data.getInteger("secondary")), false);
		}
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) {

		if(!(stack.getItem() instanceof ItemFluidIdentifier))
			return;

		list.add(I18nUtil.resolveKey(getUnlocalizedName() + ".info"));
		list.add("   " + I18n.format(getType(stack, true).getUnlocalizedName()));
		list.add(I18nUtil.resolveKey(getUnlocalizedName() + ".info2"));
		list.add("   " + I18n.format(getType(stack, false).getUnlocalizedName()));
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return stack.copy();
	}

	@Override
	public boolean hasContainerItem() {
		return true;
	}

	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {
		return false;
	}

	@Override
	public FluidType getType(World world, int x, int y, int z, ItemStack stack) {
		return getType(stack, true);
	}

	@Override
	public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister p_94581_1_) {
		super.registerIcons(p_94581_1_);

		this.overlayIcon = p_94581_1_.registerIcon("hbm:fluid_identifier_overlay");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int pass, int meta) {
		return pass == 1 ? this.overlayIcon : super.getIconFromDamageForRenderPass(pass, meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		if(pass == 0) {
			return 16777215;
		} else {
			int j = getType(stack, true).getColor();

			if(j < 0) {
				j = 16777215;
			}

			return j;
		}
	}
	
	public static void setType(ItemStack stack, FluidType type, boolean primary) {
		if(!stack.hasTagCompound())
			stack.stackTagCompound = new NBTTagCompound();
		
		stack.stackTagCompound.setInteger("fluid" + (primary ? 1 : 2), type.getID());
	}
	
	public static FluidType getType(ItemStack stack, boolean primary) {
		if(!stack.hasTagCompound())
			return Fluids.NONE;
		
		int type = stack.stackTagCompound.getInteger("fluid" + (primary ? 1 : 2));
		return Fluids.fromID(type);
	}
}