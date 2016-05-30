package realdrops.core;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;
import realdrops.core.proxies.CommonProxy;
import realdrops.entities.EntityItemLoot;
import realdrops.handlers.ConfigHandler;

@Mod(modid = RealDrops.MODID, version = RealDrops.VERSION, name = RealDrops.NAME, guiFactory = "realdrops.handlers.ConfigGuiFactory")
public class RealDrops
{
    public static final String MODID = "realdrops";
    public static final String VERSION = "RID_VER_KEY";
    public static final String NAME = "Realistic Item Drops";
    public static final String PROXY = "realdrops.core.proxies";
    public static final String CHANNEL = "RID_CHAN";
	
	@Instance(MODID)
	public static RealDrops instance;
	
	@SidedProxy(clientSide = PROXY + ".ClientProxy", serverSide = PROXY + ".CommonProxy")
	public static CommonProxy proxy;
	public SimpleNetworkWrapper network ;
	public static Logger logger;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	logger = event.getModLog();
    	network = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);
    	
    	ConfigHandler.config = new Configuration(event.getSuggestedConfigurationFile(), true);
    	ConfigHandler.initConfigs();
    	
    	proxy.registerHandlers();
    	EntityRegistry.registerModEntity(EntityItemLoot.class, "item_loot", 0, this, 64, 1, true);
    	proxy.registerRenderers();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
    
    public void registerBlock(Block b, String name)
    {
    	ResourceLocation res = new ResourceLocation(MODID + ":" + name);
    	GameRegistry.register(b, res);
        GameRegistry.register(new ItemBlock(b).setRegistryName(res));
    }
    
    public void registerItem(Item i, String name)
    {
    	ResourceLocation res = new ResourceLocation(MODID + ":" + name);
        GameRegistry.register(i.setRegistryName(res));
    }
}
