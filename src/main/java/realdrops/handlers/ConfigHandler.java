package realdrops.handlers;

import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;
import realdrops.core.RID_Settings;
import realdrops.core.RealDrops;

public class ConfigHandler
{
	public static Configuration config;
	
	public static void initConfigs()
	{
		if(config == null)
		{
			RealDrops.logger.log(Level.ERROR, "Config attempted to be loaded before it was initialised!");
			return;
		}
		
		config.load();
		
		RID_Settings.autoPickup = config.getBoolean("Auto Pickup", Configuration.CATEGORY_GENERAL, false, "Allow players to automatically pickup items");
		RID_Settings.oldItems = config.getBoolean("Old Items", Configuration.CATEGORY_GENERAL, false, "Render items the old way (Client Side Only)");
		RID_Settings.reach = config.getFloat("Pickup Reach", Configuration.CATEGORY_GENERAL, 2.5F, 1F, 5F, "Manual pickup distance in blocks");
		RID_Settings.radius = config.getFloat("Pickup Radius", Configuration.CATEGORY_GENERAL, 0.2F, 0.01F, 1F, "Affects pickup accuracy tolerance");
		RID_Settings.canFloat = config.getBoolean("Can Float", Configuration.CATEGORY_GENERAL, true, "Toggles items floating on water");
		RID_Settings.dupeWorkaround = config.getBoolean("Dupe Workaround", Configuration.CATEGORY_GENERAL, true, "A workaround for machines that check their drops before subtracting items. Disable if there are noticable side effects");
		
		config.save();
	}
}
