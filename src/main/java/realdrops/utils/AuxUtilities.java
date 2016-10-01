package realdrops.utils;

import java.util.List;
import realdrops.entities.EntityItemLoot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class AuxUtilities
{
	public static RayTraceResult RayCastEntity(EntityLivingBase src, double dist)
	{
		return RayCastEntity(src, dist, src.getLook(1F));
	}
	
	public static RayTraceResult RayCastEntity(EntityLivingBase src, double dist, Vec3d vec31)
	{
		RayTraceResult mopHit = null;
		RayTraceResult mop;
		Entity pointedEntity = null;
        double d1 = dist;
        Vec3d vec3 = new Vec3d(src.posX, src.posY, src.posZ);
        vec3 = vec3.addVector(0D, src.getEyeHeight(), 0D);
        Vec3d vec32 = vec3.addVector(vec31.xCoord * dist, vec31.yCoord * dist, vec31.zCoord * dist);
        mop = src.worldObj.rayTraceBlocks(vec3.addVector(0D, 0D, 0D), vec32, false, true, true);
        
        if (mop != null)
        {
            d1 = mop.hitVec.distanceTo(vec3) + 0.5D;
        }
        
        pointedEntity = null;
        float f1 = 1.0F;
        @SuppressWarnings("rawtypes")
		List list = src.worldObj.getEntitiesWithinAABBExcludingEntity(src, src.getEntityBoundingBox().addCoord(vec31.xCoord * dist, vec31.yCoord * dist, vec31.zCoord * dist).expand((double)f1, (double)f1, (double)f1));
        double d2 = d1 <= 0.1D? 0D : d1;
        
        for (int i = 0; i < list.size(); ++i)
        {
            Entity entity = (Entity)list.get(i);
            
            if(!(entity instanceof EntityItemLoot || entity.canBeCollidedWith()))
            {
            	continue;
            }
            
            float f2 = entity.getCollisionBorderSize();
            AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand((double)f2, (double)f2, (double)f2);
            RayTraceResult movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
            
            if (movingobjectposition != null && axisalignedbb.isVecInside(vec3))
            {
                if (0.0D < d2 || d2 == 0.0D)
                {
                	mopHit = new RayTraceResult(entity, movingobjectposition.hitVec);
                    pointedEntity = entity;
                    d2 = 0.0D;
                }
            }
            else if (movingobjectposition != null)
            {
                double d3 = vec3.distanceTo(movingobjectposition.hitVec);
                
                if (d3 < d2 || d2 == 0.0D)
                {
                    if (entity == src.getLowestRidingEntity() && !entity.canRiderInteract())
                    {
                        if (d2 == 0.0D)
                        {
                        	mopHit = new RayTraceResult(entity, movingobjectposition.hitVec);
                            pointedEntity = entity;
                        }
                    }
                    else
                    {
                    	mopHit = new RayTraceResult(entity, movingobjectposition.hitVec);
                        pointedEntity = entity;
                        d2 = d3;
                    }
                }
            }
        }

        if (pointedEntity != null && (d2 < d1 || mop == null))
        {
            return mopHit;
        } else
        {
        	return mop;
        }
	}
}
