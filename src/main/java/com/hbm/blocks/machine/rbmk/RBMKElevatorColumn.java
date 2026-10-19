package com.hbm.blocks.machine.rbmk;

import com.hbm.tileentity.machine.rbmk.TileEntityRBMKElevatorColumn;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class RBMKElevatorColumn extends RBMKBase {

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {

		if(meta >= this.offset)
			return new TileEntityRBMKElevatorColumn();
		return null;
	}

	@Override
	public int getRenderType() {
		return this.renderIDPassive;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		this.blockIcon = reg.registerIcon(this.getTextureName() + "_side");
		this.textureTop = reg.registerIcon(this.getTextureName() + "_top");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return side == 0 || side == 1 ? this.textureTop : this.blockIcon;
	}
}
