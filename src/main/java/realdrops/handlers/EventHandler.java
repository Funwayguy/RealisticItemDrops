package realdrops.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import realdrops.core.RID_Settings;
import realdrops.core.RealDrops;
import realdrops.entities.EntityItemLoot;
import realdrops.utils.AuxUtilities;

public class EventHandler
{
	@SubscribeEvent
	public void onPlayerClickEmpty(PlayerInteractEvent.RightClickEmpty event)
	{
		if(event.getWorld().isRemote)
		{
			return;
		}
		
		RayTraceResult mop = AuxUtilities.RayCastEntity(event.getEntityPlayer(), RID_Settings.reach);
		Entity entity = mop == null? null: mop.entityHit;
		
		if(entity != null && entity instanceof EntityItemLoot)
		{
			if(!event.getWorld().isRemote)
			{
				((EntityItemLoot)entity).pickup(event.getEntityPlayer());
			}
			
			event.setResult(Result.DENY);
			return;
		}
	}
	
	@SubscribeEvent
	public void onPlayerClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		if(event.getWorld().isRemote)
		{
			return;
		}
		
		RayTraceResult mop = AuxUtilities.RayCastEntity(event.getEntityPlayer(), RID_Settings.reach);
		Entity entity = mop == null? null: mop.entityHit;
		
		if(entity != null && entity instanceof EntityItemLoot)
		{
			if(!event.getWorld().isRemote)
			{
				((EntityItemLoot)entity).pickup(event.getEntityPlayer());
			}
			
			event.setResult(Result.DENY);
			event.setCanceled(true);
			return;
		}
	}
	
	@SubscribeEvent
	public void onPlayerClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if(event.getWorld().isRemote)
		{
			return;
		}
		
		RayTraceResult mop = AuxUtilities.RayCastEntity(event.getEntityPlayer(), RID_Settings.reach);
		Entity entity = mop == null? null: mop.entityHit;
		
		if(entity != null && entity instanceof EntityItemLoot)
		{
			if(!event.getWorld().isRemote)
			{
				((EntityItemLoot)entity).pickup(event.getEntityPlayer());
			}
			
			event.setResult(Result.DENY);
			event.setCanceled(true);
			return;
		}
	}
	
	@SubscribeEvent
	public void onEntityJoin(EntityJoinWorldEvent event)
	{
		if(event.getWorld().isRemote)
		{
			return;
		}
		
		if(event.getEntity().getClass() == EntityItem.class)
		{
			EntityItem item = (EntityItem)event.getEntity();
			
			if(!item.getEntityItem().isEmpty())
			{
				event.setResult(Result.DENY);
				event.setCanceled(true);
				EntityItemLoot loot = new EntityItemLoot((EntityItem)event.getEntity());
				event.getEntity().setDead();
				((EntityItem)event.getEntity()).setInfinitePickupDelay();
				event.getWorld().spawnEntity(loot);
				return;
			}
		}
	}
	
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if(event.getModID().equals(RealDrops.MODID))
		{
			ConfigHandler.config.save();
			ConfigHandler.initConfigs();
		}
	}
}
