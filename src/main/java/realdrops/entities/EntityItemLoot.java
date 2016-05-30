package realdrops.entities;

import realdrops.core.RID_Settings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityItemLoot extends EntityItem
{
	EntityItem orig = null;
	
	public EntityItemLoot(World world)
	{
		super(world);
		this.hoverStart = this.rand.nextFloat();
	}
	
	public EntityItemLoot(EntityItem orig)
	{
		this(orig.worldObj, orig.posX, orig.posY, orig.posZ, orig.getEntityItem());
		
		this.orig = orig;
		
		NBTTagCompound oldT = new NBTTagCompound();
		orig.writeEntityToNBT(oldT);
		this.readEntityFromNBT(oldT);
		
		String thrower = orig.getThrower();
		Entity entity = thrower == null? null : orig.worldObj.getPlayerEntityByName(thrower);
		double tossSpd = entity != null && entity.isSprinting()? 2D : 1D;
		
		this.motionX = orig.motionX * tossSpd;
		this.motionY = orig.motionY * tossSpd;
		this.motionZ = orig.motionZ * tossSpd;
		
		if(!RID_Settings.autoPickup)
		{
			this.setPickupDelay(0);
		}
	}
	
	@Override
	public void onUpdate()
	{
		if(!this.worldObj.isRemote && orig != null && orig.getAge() >= this.getEntityItem().getItem().getEntityLifespan(getEntityItem(), worldObj) - 1)
		{
			// The original item was set to despawn! ABORT EXISTENCE
			this.setDead();
			return;
		}
		
		super.onUpdate();
	}
	
	@Override
	public float getCollisionBorderSize()
	{
		return onGround? 0.1F : 0.5F; // Helps with manual pickup
	}
	
	public EntityItemLoot(World world, double x, double y, double z, ItemStack stack)
	{
		super(world, x, y, z, stack);
		this.hoverStart = this.rand.nextFloat();
	}
	
    /**
     * Called by a player entity when they collide with an entity
     */
	@Override
    public void onCollideWithPlayer(EntityPlayer player)
    {
    	if(RID_Settings.autoPickup)
    	{
    		super.onCollideWithPlayer(player);
    	}
    }
    
    public void pickup(EntityPlayer player)
    {
    	super.onCollideWithPlayer(player);
    }
}
