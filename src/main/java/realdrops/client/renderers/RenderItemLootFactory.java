package realdrops.client.renderers;

import realdrops.entities.EntityItemLoot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderItemLootFactory implements IRenderFactory<EntityItemLoot>
{
	@Override
	public Render<? super EntityItemLoot> createRenderFor(RenderManager manager)
	{
		return new RenderItemLoot(manager, Minecraft.getMinecraft().getRenderItem());
	}
}
