package com.hbm.render.tileentity;

import org.lwjgl.opengl.GL11;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.lib.RefStrings;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public class RenderDriveProcessor extends TileEntitySpecialRenderer implements IItemRendererProvider {

	private static final ResourceLocation computer_tex = new ResourceLocation(RefStrings.MODID, "textures/blocks/deco_computer.png");
	private static final ResourceLocation tape_tex = new ResourceLocation(RefStrings.MODID, "textures/blocks/deco_tape_recorder.png");

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float interp) {
		GL11.glPushMatrix();
		{

			GL11.glTranslated(x + 0.5D, y, z + 0.5D);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glShadeModel(GL11.GL_SMOOTH);

			GL11.glRotatef(180, 0, 1, 0);
	
			switch(te.getBlockMetadata() - BlockDummyable.offset) {
			case 2: GL11.glRotatef(0, 0F, 1F, 0F); break;
			case 4: GL11.glRotatef(90, 0F, 1F, 0F); break;
			case 3: GL11.glRotatef(180, 0F, 1F, 0F); break;
			case 5: GL11.glRotatef(270, 0F, 1F, 0F); break;
			}

			GL11.glTranslated(0.5, 0, 0);
	
			bindTexture(computer_tex);
			ResourceManager.drive_processor.renderPart("Computer");
			ResourceManager.drive_processor.renderPart("Wire");
			
			bindTexture(tape_tex);
			ResourceManager.drive_processor.renderPart("Tape");

			GL11.glShadeModel(GL11.GL_FLAT);

		}
		GL11.glPopMatrix();
	}

	@Override
	public IItemRenderer getRenderer() {
		return new ItemRenderBase() {
			public void renderInventory() {
				GL11.glTranslated(0, -0.5, 0);
				GL11.glScaled(7D, 7D, 7D);
			}
			public void renderCommon() {
				GL11.glRotated(180, 0, 1, 0);
				GL11.glShadeModel(GL11.GL_SMOOTH);
	
				bindTexture(computer_tex);
				ResourceManager.drive_processor.renderPart("Computer");
				ResourceManager.drive_processor.renderPart("Wire");
				
				bindTexture(tape_tex);
				ResourceManager.drive_processor.renderPart("Tape");

				GL11.glShadeModel(GL11.GL_FLAT);
			}
		};
	}

	@Override
	public Item getItemForRenderer() {
		return Item.getItemFromBlock(ModBlocks.machine_drive_processor);
	}
	
}
