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
import realdrops.core.RealDrops;

public class EntityItemLoot extends EntityItem
{
	private EntityItem orig = null;
	
	public EntityItemLoot(World world)
	{
		super(world);
		this.hoverStart = this.rand.nextFloat();
	}
	
	public EntityItemLoot(EntityItem orig)
	{
		this(orig.world, orig.posX, orig.posY, orig.posZ, orig.getItem());
		
		this.orig = orig;
		
		NBTTagCompound oldT = new NBTTagCompound();
		orig.writeEntityToNBT(oldT);
		this.readEntityFromNBT(oldT);
		
		String thrower = orig.getThrower();
		Entity entity = (thrower == null || thrower.length() == 0)? null : orig.world.getPlayerEntityByName(thrower);
		double tossSpd = entity != null && entity.isSprinting()? 2D : 1D;
		
		this.motionX = orig.motionX * tossSpd;
		this.motionY = orig.motionY * tossSpd;
		this.motionZ = orig.motionZ * tossSpd;
		
		if(!RID_Settings.autoPickup)
		{
			this.setPickupDelay(0);
		}
	}
	
	private Field fAge;
	private boolean failed = false;
	private boolean checked = false;
	
	@Override
	public void onUpdate()
	{
		if(failed)
		{
			super.onUpdate();
			return;
		} else if(fAge == null)
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
					RealDrops.logger.error("Unabled to hook 'age' field in EntityItem.class", e1);
					super.onUpdate();
					failed = true;
					return;
				}
			}
		}
		
		if(!checked)
		{
			int age;
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
				RealDrops.logger.error("Unabled to access 'age' field in EntityItem.class", e);
				super.onUpdate();
				failed = true;
				return;
			}
			
			if(age == 1 && !this.world.isRemote && orig != null && oAge >= this.getItem().getItem().getEntityLifespan(getItem(), world) - 1)
			{
				// The original item was set to despawn! ABORT EXISTENCE
				this.setDead();
				return;
			}
			
			checked = true;
		}
		
		super.onUpdate();
		
		int x = MathHelper.floor(posX);
		int y = MathHelper.floor(posY);
		int z = MathHelper.floor(posZ);
		
		IBlockState bsHere = this.world.getBlockState(new BlockPos(x, y, z));
		IBlockState bsAbove = this.world.getBlockState(new BlockPos(x, y + 1, z));
		
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
			
			this.motionX = MathHelper.clamp(this.motionX, -0.05D, 0.05D);
			this.motionZ = MathHelper.clamp(this.motionZ, -0.05D, 0.05D);
        }
	}
	
	@Override
	public float getCollisionBorderSize()
	{
		return MathHelper.clamp(RID_Settings.radius * (onGround? 1F : 2F), 0.01F, 1F); // Helps with pickup accuracy
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
    	if(this.isDead || player.world.isRemote)
    	{
    		return;
    	}
    	
    	super.onCollideWithPlayer(player);
    }
}
