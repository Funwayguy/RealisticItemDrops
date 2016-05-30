package realdrops.core.proxies;

import realdrops.client.renderers.RenderItemLootFactory;
import realdrops.entities.EntityItemLoot;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public final class ClientProxy extends CommonProxy
{
	@Override
	public final boolean isClient()
	{
		return true;
	}
	
	@Override
	public void registerHandlers()
	{
		super.registerHandlers();
		
	}
	
	@Override
	public void registerRenderers()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityItemLoot.class, new RenderItemLootFactory());
	}
}
