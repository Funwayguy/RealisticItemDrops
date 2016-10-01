package realdrops.entities;

import java.lang.reflect.Field;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import realdrops.core.RID_Settings;

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
	
	Field fAge;
	
	@Override
	public void onUpdate()
	{
		if(fAge == null)
		{
			try
			{
				fAge = EntityItem.class.getDeclaredField("field_70292_b");
				fAge.setAccessible(true);
			} catch(Exception e1)
			{
				try
				{
					fAge = EntityItem.class.getDeclaredField("age");
					fAge.setAccessible(true);
				} catch(Exception e2)
				{
					System.out.println("Unabled to obtain field");
					super.onUpdate();
					return;
				}
			}
		}
		
		int age = 0;
		int oAge = 0;
		
		try
		{
			age = fAge.getInt(this);
			
			if(orig != null)
			{
				oAge = fAge.getInt(orig);
			}
		} catch(Exception e)
		{
			System.out.println("Unabled to access field");
			super.onUpdate();
			return;
		}
		
		if(age == 1 && !this.worldObj.isRemote && orig != null && oAge >= this.getEntityItem().getItem().getEntityLifespan(getEntityItem(), worldObj) - 1)
		{
			// The original item was set to despawn! ABORT EXISTENCE
			this.setDead();
			return;
		}
		
		super.onUpdate();
		
		int x = MathHelper.floor_double(posX);
		int y = MathHelper.floor_double(posY);
		int z = MathHelper.floor_double(posZ);
		
		IBlockState bsHere = this.worldObj.getBlockState(new BlockPos(x, y, z));
		IBlockState bsAbove = this.worldObj.getBlockState(new BlockPos(x, y + 1, z));
		
		boolean liqHere = bsHere.getBlock() instanceof BlockLiquid || bsHere.getBlock() instanceof IFluidBlock;
		boolean liqAbove = bsAbove.getBlock() instanceof BlockLiquid || bsAbove.getBlock() instanceof IFluidBlock;
		
		if(RID_Settings.canFloat && liqHere)
        {
			this.onGround = false;
			this.inWater = true;
			
			if(this.motionY < 0.05D && (liqAbove || this.posY%1D < 0.9F))
			{
				this.motionY += Math.min(0.075D, 0.075D - this.motionY);
			}
			
			this.motionX = MathHelper.clamp_double(this.motionX, -0.05D, 0.05D);
			this.motionZ = MathHelper.clamp_double(this.motionZ, -0.05D, 0.05D);
        }
	}
	
	@Override
	public float getCollisionBorderSize()
	{
		return MathHelper.clamp_float(RID_Settings.radius * (onGround? 1F : 2F), 0.01F, 1F); // Helps with pickup accuracy
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
