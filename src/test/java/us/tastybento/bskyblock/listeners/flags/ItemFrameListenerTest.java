/**
 *
 */
package us.tastybento.bskyblock.listeners.flags;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import us.tastybento.bskyblock.BSkyBlock;
import us.tastybento.bskyblock.Settings;
import us.tastybento.bskyblock.api.configuration.WorldSettings;
import us.tastybento.bskyblock.database.objects.Island;
import us.tastybento.bskyblock.lists.Flags;
import us.tastybento.bskyblock.managers.FlagsManager;
import us.tastybento.bskyblock.managers.IslandWorldManager;
import us.tastybento.bskyblock.managers.IslandsManager;
import us.tastybento.bskyblock.util.Util;

/**
 * @author tastybento
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( {BSkyBlock.class, Flags.class, Util.class, Bukkit.class} )
public class ItemFrameListenerTest {

    private static Location location;
    private static BSkyBlock plugin;
    private static IslandWorldManager iwm;
    private static IslandsManager im;
    private static World world;
    private static Enderman enderman;
    private static Slime slime;

    @Before
    public void setUp() {
        // Set up plugin
        plugin = mock(BSkyBlock.class);
        Whitebox.setInternalState(BSkyBlock.class, "instance", plugin);

        Server server = mock(Server.class);
        world = mock(World.class);
        when(server.getLogger()).thenReturn(Logger.getAnonymousLogger());
        when(server.getWorld("world")).thenReturn(world);
        when(server.getVersion()).thenReturn("BSB_Mocking");

        PluginManager pluginManager = mock(PluginManager.class);
        when(server.getPluginManager()).thenReturn(pluginManager);

        ItemFactory itemFactory = mock(ItemFactory.class);
        when(server.getItemFactory()).thenReturn(itemFactory);

        PowerMockito.mockStatic(Bukkit.class);
        when(Bukkit.getServer()).thenReturn(server);

        SkullMeta skullMeta = mock(SkullMeta.class);
        when(itemFactory.getItemMeta(any())).thenReturn(skullMeta);
        when(Bukkit.getItemFactory()).thenReturn(itemFactory);
        when(Bukkit.getLogger()).thenReturn(Logger.getAnonymousLogger());
        location = mock(Location.class);
        when(location.getWorld()).thenReturn(world);
        when(location.getBlockX()).thenReturn(0);
        when(location.getBlockY()).thenReturn(0);
        when(location.getBlockZ()).thenReturn(0);
        PowerMockito.mockStatic(Flags.class);

        FlagsManager flagsManager = new FlagsManager(plugin);
        when(plugin.getFlagsManager()).thenReturn(flagsManager);


        // Worlds
        iwm = mock(IslandWorldManager.class);
        when(iwm.getBSBIslandWorld()).thenReturn(world);
        when(iwm.getBSBNetherWorld()).thenReturn(world);
        when(iwm.getBSBEndWorld()).thenReturn(world);
        when(iwm.inWorld(any())).thenReturn(true);
        when(plugin.getIWM()).thenReturn(iwm);

        // Monsters and animals
        enderman = mock(Enderman.class);
        when(enderman.getLocation()).thenReturn(location);
        when(enderman.getWorld()).thenReturn(world);
        when(enderman.getCarriedMaterial()).thenReturn(new MaterialData(Material.STONE));
        slime = mock(Slime.class);
        when(slime.getLocation()).thenReturn(location);

        // Fake players
        Settings settings = mock(Settings.class);
        Mockito.when(plugin.getSettings()).thenReturn(settings);
        Mockito.when(settings.getFakePlayers()).thenReturn(new HashSet<String>());

        // World Settings
        WorldSettings ws = mock(WorldSettings.class);
        when(iwm.getWorldSettings(Mockito.any())).thenReturn(ws);
        Map<String, Boolean> worldFlags = new HashMap<>();
        when(ws.getWorldFlags()).thenReturn(worldFlags);

        // Island manager
        im = mock(IslandsManager.class);
        when(plugin.getIslands()).thenReturn(im);
        Island island = mock(Island.class);
        Optional<Island> optional = Optional.of(island);
        when(im.getProtectedIslandAt(Mockito.any())).thenReturn(optional);

        PowerMockito.mockStatic(Util.class);
        when(Util.getWorld(Mockito.any())).thenReturn(mock(World.class));
        // Not allowed to start
        Flags.ITEM_FRAME_DAMAGE.setSetting(world, false);

    }

    /**
     * Test method for {@link us.tastybento.bskyblock.listeners.flags.ItemFrameListener#onItemFrameDamage(org.bukkit.event.entity.EntityDamageByEntityEvent)}.
     */
    @Test
    public void testOnItemFrameDamageEntityDamageByEntityEvent() {
        ItemFrameListener ifl = new ItemFrameListener();
        Entity entity = mock(ItemFrame.class);
        DamageCause cause = DamageCause.ENTITY_ATTACK;
        @SuppressWarnings("deprecation")
        EntityDamageByEntityEvent e = new EntityDamageByEntityEvent(enderman, entity, cause , 0);
        ifl.onItemFrameDamage(e);
        assertTrue(e.isCancelled());
    }

    /**
     * Test method for {@link us.tastybento.bskyblock.listeners.flags.ItemFrameListener#onItemFrameDamage(org.bukkit.event.entity.EntityDamageByEntityEvent)}.
     */
    @Test
    public void testNotItemFrame() {
        ItemFrameListener ifl = new ItemFrameListener();
        Entity entity = mock(Monster.class);
        DamageCause cause = DamageCause.ENTITY_ATTACK;
        @SuppressWarnings("deprecation")
        EntityDamageByEntityEvent e = new EntityDamageByEntityEvent(enderman, entity, cause , 0);
        ifl.onItemFrameDamage(e);
        assertFalse(e.isCancelled());
    }

    /**
     * Test method for {@link us.tastybento.bskyblock.listeners.flags.ItemFrameListener#onItemFrameDamage(org.bukkit.event.entity.EntityDamageByEntityEvent)}.
     */
    @Test
    public void testProjectile() {
        ItemFrameListener ifl = new ItemFrameListener();
        Entity entity = mock(ItemFrame.class);
        DamageCause cause = DamageCause.ENTITY_ATTACK;
        Projectile p = mock(Projectile.class);
        when(p.getShooter()).thenReturn(enderman);
        @SuppressWarnings("deprecation")
        EntityDamageByEntityEvent e = new EntityDamageByEntityEvent(p, entity, cause , 0);
        ifl.onItemFrameDamage(e);
        assertTrue(e.isCancelled());
    }

    /**
     * Test method for {@link us.tastybento.bskyblock.listeners.flags.ItemFrameListener#onItemFrameDamage(org.bukkit.event.entity.EntityDamageByEntityEvent)}.
     */
    @Test
    public void testPlayerProjectile() {
        ItemFrameListener ifl = new ItemFrameListener();
        Entity entity = mock(ItemFrame.class);
        DamageCause cause = DamageCause.ENTITY_ATTACK;
        Projectile p = mock(Projectile.class);
        Player player = mock(Player.class);
        when(p.getShooter()).thenReturn(player);
        @SuppressWarnings("deprecation")
        EntityDamageByEntityEvent e = new EntityDamageByEntityEvent(p, entity, cause , 0);
        ifl.onItemFrameDamage(e);
        assertFalse(e.isCancelled());
    }

    /**
     * Test method for {@link us.tastybento.bskyblock.listeners.flags.ItemFrameListener#onItemFrameDamage(org.bukkit.event.hanging.HangingBreakByEntityEvent)}.
     */
    @Test
    public void testOnItemFrameDamageHangingBreakByEntityEvent() {
        ItemFrameListener ifl = new ItemFrameListener();
        Hanging hanging = mock(ItemFrame.class);
        HangingBreakByEntityEvent e = new HangingBreakByEntityEvent(hanging, enderman);
        ifl.onItemFrameDamage(e);
        assertTrue(e.isCancelled());
    }

}
