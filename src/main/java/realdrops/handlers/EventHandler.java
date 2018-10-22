package realdrops.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import realdrops.core.RID_Settings;
import realdrops.core.RealDrops;
import realdrops.entities.EntityItemLoot;
import realdrops.utils.AuxUtilities;

import java.util.ArrayDeque;

public class EventHandler
{
	@SubscribeEvent
	public void onPlayerClickEmpty(PlayerInteractEvent.RightClickEmpty event)
	{
		if(event.getWorld().isRemote || RID_Settings.autoPickup)
		{
			return;
		}
		
		RayTraceResult mop = AuxUtilities.RayCastEntity(event.getEntityPlayer(), RID_Settings.reach);
		Entity entity = mop == null? null: mop.entityHit;
		
		if(entity instanceof EntityItemLoot)
		{
			if(!event.getWorld().isRemote)
			{
				((EntityItemLoot)entity).pickup(event.getEntityPlayer());
			}
			
			event.setResult(Result.DENY);
		}
	}
	
	@SubscribeEvent
	public void onPlayerClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		if(event.getWorld().isRemote || RID_Settings.autoPickup)
		{
			return;
		}
		
		RayTraceResult mop = AuxUtilities.RayCastEntity(event.getEntityPlayer(), RID_Settings.reach);
		Entity entity = mop == null? null: mop.entityHit;
		
		if(entity instanceof EntityItemLoot)
		{
			if(!event.getWorld().isRemote)
			{
				((EntityItemLoot)entity).pickup(event.getEntityPlayer());
			}
			
			event.setResult(Result.DENY);
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onPlayerClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if(event.getWorld().isRemote || RID_Settings.autoPickup)
		{
			return;
		}
		
		RayTraceResult mop = AuxUtilities.RayCastEntity(event.getEntityPlayer(), RID_Settings.reach);
		Entity entity = mop == null? null: mop.entityHit;
		
		if(entity instanceof EntityItemLoot)
		{
			if(!event.getWorld().isRemote)
			{
				((EntityItemLoot)entity).pickup(event.getEntityPlayer());
			}
			
			event.setResult(Result.DENY);
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent(priority= EventPriority.LOWEST)
	public void onEntityJoin(EntityJoinWorldEvent event)
	{
		if(event.getWorld().isRemote || event.isCanceled() || event.getEntity().isDead)
		{
			return;
		}
		
		if(event.getEntity().getClass() == EntityItem.class)
		{
			EntityItem item = (EntityItem)event.getEntity();
			
			if(!item.getItem().isEmpty())
			{
				itemQueue.add(new PendingItem(item, new EntityItemLoot(item)));
			}
		}
	}
	
	private final ArrayDeque<PendingItem> itemQueue = new ArrayDeque<>();
	
	@SubscribeEvent
	public void onServerTick(ServerTickEvent event)
	{
		while(!itemQueue.isEmpty())
		{
			PendingItem pend = itemQueue.poll();
			
			if(pend == null || pend.item.isDead || pend.item.getItem().isEmpty())
			{
				continue;
			}
			
			pend.item.setDead();
			pend.item.setItem(ItemStack.EMPTY);
			pend.item.setInfinitePickupDelay();
			
			pend.item.world.spawnEntity(pend.loot);
		}
	}
	
	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event)
	{
		if(event.getWorld().isRemote || (event.getWorld().getMinecraftServer() != null && event.getWorld().getMinecraftServer().isServerRunning()))
		{
			return;
		}
		
		itemQueue.clear();
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
	
	private class PendingItem
	{
		private final EntityItem item;
		private final EntityItemLoot loot;
		
		private PendingItem(EntityItem item, EntityItemLoot loot)
		{
			this.item = item;
			this.loot = loot;
		}
	}
}
