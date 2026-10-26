package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultBoolean;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultDouble;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultEnum;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultInt;
import com.gtnewhorizon.gtnhlib.config.Config.LangKey;
import com.gtnewhorizon.gtnhlib.config.Config.RangeDouble;
import com.gtnewhorizon.gtnhlib.config.Config.RangeInt;
import com.science.gtnl.utils.enums.PortalGunPortalEffect;

@Comment("Client section")
public class ClientConfig {

    @Comment("Camera and weapon shake caused by charging and firing the electromagnetic railgun")
    @LangKey("gtnl.gui.config.general.client.enable_railgun_shake")
    @DefaultBoolean(true)
    public boolean enableRailgunShake = true;

    @Comment("Soft glow and charge orb effects of the electromagnetic railgun")
    @LangKey("gtnl.gui.config.general.client.enable_railgun_bloom")
    @DefaultBoolean(true)
    public boolean enableRailgunBloom = true;

    @Comment("GLSL shaders for the railgun beam, charge orb and charge overlay")
    @LangKey("gtnl.gui.config.general.client.enable_railgun_shaders")
    @DefaultBoolean(true)
    public boolean enableRailgunShaders = true;

    @Comment("Show beams, charge orbs, arcs and impact effects of other players' railguns")
    @LangKey("gtnl.gui.config.general.client.enable_railgun_show_others_fx")
    @DefaultBoolean(true)
    public boolean enableRailgunShowOthersFx = true;

    @Comment("Railgun effects of other players are not drawn beyond this distance in blocks")
    @LangKey("gtnl.gui.config.general.client.railgun_fx_distance")
    @DefaultInt(256)
    @RangeInt(min = 0, max = 256)
    public int railgunFxDistance = 256;

    @Comment("Celestweave armor effect level: 0 off, 1 event effects only, 2 all effects")
    @LangKey("gtnl.gui.config.general.client.celestweave_fx_level")
    @DefaultInt(2)
    @RangeInt(min = 0, max = 2)
    public int celestweaveFxLevel = 2;

    @Comment("Maximum number of Celestweave arcs shown for one wearer")
    @LangKey("gtnl.gui.config.general.client.celestweave_max_arcs_per_player")
    @DefaultInt(16)
    @RangeInt(min = 0, max = 64)
    public int celestweaveMaxArcsPerPlayer = 16;

    @Comment("Maximum number of Celestweave arcs shown in total")
    @LangKey("gtnl.gui.config.general.client.celestweave_max_arcs_total")
    @DefaultInt(128)
    @RangeInt(min = 0, max = 1024)
    public int celestweaveMaxArcsTotal = 128;

    @Comment("Show arcs and event effects of other players' Celestweave armor")
    @LangKey("gtnl.gui.config.general.client.enable_celestweave_show_others_fx")
    @DefaultBoolean(true)
    public boolean enableCelestweaveShowOthersFx = true;

    @Comment("Celestweave arcs are not drawn beyond this distance in blocks")
    @LangKey("gtnl.gui.config.general.client.celestweave_fx_distance")
    @DefaultInt(32)
    @RangeInt(min = 0, max = 256)
    public int celestweaveFxDistance = 32;

    @Comment("Beyond this distance in blocks only the main body of Celestweave armor is drawn")
    @LangKey("gtnl.gui.config.general.client.celestweave_detail_distance")
    @DefaultInt(24)
    @RangeInt(min = 0, max = 256)
    public int celestweaveDetailDistance = 24;

    @Comment("Process your own idle Celestweave arcs in first person view")
    @LangKey("gtnl.gui.config.general.client.enable_celestweave_first_person_fx")
    @DefaultBoolean(false)
    public boolean enableCelestweaveFirstPersonFx = false;

    @Comment("Draw the Celestweave chestplate arm over your empty first person hand")
    @LangKey("gtnl.gui.config.general.client.enable_celestweave_first_person_arm")
    @DefaultBoolean(true)
    public boolean enableCelestweaveFirstPersonArm = true;

    @Comment("Tint the equipment screens and the HUD editor guides with the glow color of the Celestweave chestplate you wear or are editing; when off they use the colors of the chosen theme")
    @LangKey("gtnl.gui.config.general.client.enable_equipment_theme_glow_color")
    @DefaultBoolean(true)
    public boolean enableEquipmentThemeGlowColor = true;

    @Comment("Look of portal gun portals on this client, also switchable from the portal gun wheel. PIXEL_SWIRL: pixel art green swirl; ELECTRIC_DOOR: door with an electric frame and a starry inside; FLUID_VORTEX: oval opened by a swirl of portal fluid; GOLDEN_SWIRL: pixel art golden swirl")
    @LangKey("gtnl.gui.config.general.client.portal_gun_portal_effect")
    @DefaultEnum("PIXEL_SWIRL")
    public PortalGunPortalEffect portalGunPortalEffect = PortalGunPortalEffect.PIXEL_SWIRL;

    @Comment("How far portal gun trail dots spread from the flight line when they appear, in blocks")
    @LangKey("gtnl.gui.config.general.client.portal_gun_trail_width")
    @DefaultDouble(0.03)
    @RangeDouble(min = 0, max = 0.1)
    public double portalGunTrailWidth = 0.03;

    @Comment("How fast portal gun trail dots drift sideways away from the flight line when they appear, in blocks per tick")
    @LangKey("gtnl.gui.config.general.client.portal_gun_trail_spray")
    @DefaultDouble(0.0125)
    @RangeDouble(min = 0, max = 0.06)
    public double portalGunTrailSpray = 0.0125;

    @Comment("How fast portal gun trail dots fall, in blocks per tick squared")
    @LangKey("gtnl.gui.config.general.client.portal_gun_trail_gravity")
    @DefaultDouble(0.02)
    @RangeDouble(min = 0.005, max = 0.04)
    public double portalGunTrailGravity = 0.02;

    @Comment("Average radius of a portal gun trail dot, in blocks")
    @LangKey("gtnl.gui.config.general.client.portal_gun_trail_dot_size")
    @DefaultDouble(0.028)
    @RangeDouble(min = 0.01, max = 0.08)
    public double portalGunTrailDotSize = 0.028;

    @Comment("Portal gun trail dots dropped at each spot along the flight")
    @LangKey("gtnl.gui.config.general.client.portal_gun_trail_count")
    @DefaultInt(1)
    @RangeInt(min = 1, max = 4)
    public int portalGunTrailCount = 1;

    @Comment("Spots per block along the flight where portal gun trail dots are dropped")
    @LangKey("gtnl.gui.config.general.client.portal_gun_trail_density")
    @DefaultInt(12)
    @RangeInt(min = 5, max = 60)
    public int portalGunTrailDensity = 12;

    @Comment("How long portal gun trail dots float before they fall, in ticks for every block away from the gun")
    @LangKey("gtnl.gui.config.general.client.portal_gun_trail_fall_delay")
    @DefaultDouble(1.5)
    @RangeDouble(min = 0, max = 4)
    public double portalGunTrailFallDelay = 1.5;

    @Comment("Trail dots this close to where the portal gun shot lands fade out quickly, in blocks; 0 keeps them all")
    @LangKey("gtnl.gui.config.general.client.portal_gun_trail_cut_range")
    @DefaultDouble(2)
    @RangeDouble(min = 0, max = 32)
    public double portalGunTrailCutRange = 2;

    @Comment("How long trail dots near where the shot lands take to fade out, in ticks")
    @LangKey("gtnl.gui.config.general.client.portal_gun_trail_cut_ticks")
    @DefaultDouble(4)
    @RangeDouble(min = 1, max = 20)
    public double portalGunTrailCutTicks = 4;

    @Comment("Puffs of light at the portal gun muzzle when it fires")
    @LangKey("gtnl.gui.config.general.client.enable_portal_gun_puffs")
    @DefaultBoolean(true)
    public boolean enablePortalGunPuffs = true;

    @Comment("Pixel rings around the portal gun shot while it flies")
    @LangKey("gtnl.gui.config.general.client.enable_portal_gun_rings")
    @DefaultBoolean(true)
    public boolean enablePortalGunRings = true;

    @Comment("A flash and dots splashing up where the portal gun shot lands")
    @LangKey("gtnl.gui.config.general.client.enable_portal_gun_sparks")
    @DefaultBoolean(true)
    public boolean enablePortalGunSparks = true;

    @Comment("Dots splashing up where the portal gun shot lands")
    @LangKey("gtnl.gui.config.general.client.portal_gun_spark_count")
    @DefaultInt(36)
    @RangeInt(min = 0, max = 80)
    public int portalGunSparkCount = 36;

    @Comment("How far the held portal gun tips its top toward your eye in first person, in degrees")
    @LangKey("gtnl.gui.config.general.client.portal_gun_held_pitch")
    @DefaultDouble(15)
    @RangeDouble(min = -30, max = 45)
    public double portalGunHeldPitch = 15;

    @Comment("How far the held portal gun turns sideways in first person, in degrees")
    @LangKey("gtnl.gui.config.general.client.portal_gun_held_yaw")
    @DefaultDouble(15)
    @RangeDouble(min = -45, max = 45)
    public double portalGunHeldYaw = 15;

    @Comment("How far the held portal gun is pushed forward in first person, in blocks")
    @LangKey("gtnl.gui.config.general.client.portal_gun_held_forward")
    @DefaultDouble(0.15)
    @RangeDouble(min = -0.3, max = 0.5)
    public double portalGunHeldForward = 0.15;

    @Comment("Show the trail, rings, puffs and sparks of portal guns fired by other players")
    @LangKey("gtnl.gui.config.general.client.enable_portal_gun_show_others_fx")
    @DefaultBoolean(true)
    public boolean enablePortalGunShowOthersFx = true;

    @Comment("How far portal opening dots start from the edge of the portal, in blocks")
    @LangKey("gtnl.gui.config.general.client.portal_gun_open_spread")
    @DefaultDouble(0.05)
    @RangeDouble(min = 0, max = 0.1)
    public double portalGunOpenSpread = 0.05;

    @Comment("How fast portal opening dots drift outward along the portal, in blocks per tick")
    @LangKey("gtnl.gui.config.general.client.portal_gun_open_spray")
    @DefaultDouble(0.005)
    @RangeDouble(min = 0, max = 0.06)
    public double portalGunOpenSpray = 0.005;

    @Comment("How fast portal opening dots shoot out of the portal, in blocks per tick")
    @LangKey("gtnl.gui.config.general.client.portal_gun_open_jet")
    @DefaultDouble(0.17)
    @RangeDouble(min = 0, max = 0.4)
    public double portalGunOpenJet = 0.17;

    @Comment("How much speed portal opening dots keep every tick")
    @LangKey("gtnl.gui.config.general.client.portal_gun_open_drag")
    @DefaultDouble(0.96)
    @RangeDouble(min = 0.7, max = 0.99)
    public double portalGunOpenDrag = 0.96;

    @Comment("Gravity on portal opening dots, in blocks per tick squared; 0 or less makes them float")
    @LangKey("gtnl.gui.config.general.client.portal_gun_open_gravity")
    @DefaultDouble(0.021)
    @RangeDouble(min = -0.01, max = 0.04)
    public double portalGunOpenGravity = 0.021;

    @Comment("Average radius of portal opening dots, in blocks")
    @LangKey("gtnl.gui.config.general.client.portal_gun_open_dot_size")
    @DefaultDouble(0.025)
    @RangeDouble(min = 0.01, max = 0.08)
    public double portalGunOpenDotSize = 0.025;

    @Comment("How long portal opening dots wait before they move, in ticks")
    @LangKey("gtnl.gui.config.general.client.portal_gun_open_hover")
    @DefaultDouble(0)
    @RangeDouble(min = 0, max = 20)
    public double portalGunOpenHover = 0;

    @Comment("Dots thrown at each spot when a portal opens")
    @LangKey("gtnl.gui.config.general.client.portal_gun_open_count")
    @DefaultInt(1)
    @RangeInt(min = 1, max = 4)
    public int portalGunOpenCount = 1;

    @Comment("Spots per block of portal edge that throw dots when a portal opens")
    @LangKey("gtnl.gui.config.general.client.portal_gun_open_density")
    @DefaultInt(6)
    @RangeInt(min = 2, max = 40)
    public int portalGunOpenDensity = 6;

    @Comment("Throw portal opening dots from the whole portal instead of only its edge")
    @LangKey("gtnl.gui.config.general.client.enable_portal_gun_open_whole_face")
    @DefaultBoolean(false)
    public boolean enablePortalGunOpenWholeFace = false;

    @Comment("Arcs per tick while a portal opens")
    @LangKey("gtnl.gui.config.general.client.portal_gun_open_arc_rate")
    @DefaultDouble(2)
    @RangeDouble(min = 0, max = 8)
    public double portalGunOpenArcRate = 2;

    @Comment("How long arcs crackle when a portal opens, in ticks")
    @LangKey("gtnl.gui.config.general.client.portal_gun_open_arc_ticks")
    @DefaultDouble(11.5)
    @RangeDouble(min = 2, max = 20)
    public double portalGunOpenArcTicks = 11.5;

    @Comment("Chance that a portal arc has a small fork, from 0 to 1")
    @LangKey("gtnl.gui.config.general.client.portal_gun_arc_fork_chance")
    @DefaultDouble(0.75)
    @RangeDouble(min = 0, max = 1)
    public double portalGunArcForkChance = 0.75;

    @Comment("How hard opening arcs bend: long straight runs with sharp corners, 0 is a straight line")
    @LangKey("gtnl.gui.config.general.client.portal_gun_open_arc_bend")
    @DefaultDouble(0.8)
    @RangeDouble(min = 0, max = 1.5)
    public double portalGunOpenArcBend = 0.8;

    @Comment("How far opening arcs peak out of the portal, as a share of their length, 0 keeps them flat")
    @LangKey("gtnl.gui.config.general.client.portal_gun_open_arc_rise")
    @DefaultDouble(0.6)
    @RangeDouble(min = 0, max = 1.5)
    public double portalGunOpenArcRise = 0.6;

    @Comment("How long a portal arc stays before it jumps somewhere else, in ticks")
    @LangKey("gtnl.gui.config.general.client.portal_gun_arc_hop")
    @DefaultDouble(1.5)
    @RangeDouble(min = 0.5, max = 3)
    public double portalGunArcHop = 1.5;

    @Comment("Width of the white core of portal arcs, in blocks")
    @LangKey("gtnl.gui.config.general.client.portal_gun_arc_width")
    @DefaultDouble(0.006)
    @RangeDouble(min = 0.002, max = 0.04)
    public double portalGunArcWidth = 0.006;

    @Comment("Arc runs per second along the inner ring of an open portal")
    @LangKey("gtnl.gui.config.general.client.portal_gun_ring_arc_rate")
    @DefaultDouble(3)
    @RangeDouble(min = 0, max = 10)
    public double portalGunRingArcRate = 3;

    @Comment("Arc bursts per second near the middle of an open portal")
    @LangKey("gtnl.gui.config.general.client.portal_gun_center_arc_rate")
    @DefaultDouble(0.5)
    @RangeDouble(min = 0, max = 10)
    public double portalGunCenterArcRate = 0.5;

    @Comment("How much a portal squashes, stretches and rocks when something passes through it; 0 keeps it still")
    @LangKey("gtnl.gui.config.general.client.portal_gun_pass_jelly")
    @DefaultDouble(0.12)
    @RangeDouble(min = 0, max = 0.3)
    public double portalGunPassJelly = 0.12;

    @Comment("Dots thrown from a portal when something passes through it")
    @LangKey("gtnl.gui.config.general.client.portal_gun_pass_dot_count")
    @DefaultInt(24)
    @RangeInt(min = 0, max = 60)
    public int portalGunPassDotCount = 24;

    @Comment("How much a closing portal swells before it shrinks away; 0 shrinks it at once. The electric door plays its opening backwards instead")
    @LangKey("gtnl.gui.config.general.client.portal_gun_close_pop")
    @DefaultDouble(0.2)
    @RangeDouble(min = 0, max = 0.5)
    public double portalGunClosePop = 0.2;

    @Comment("How long a closing portal takes to shrink away, in ticks; the electric door always plays its opening backwards")
    @LangKey("gtnl.gui.config.general.client.portal_gun_close_ticks")
    @DefaultInt(9)
    @RangeInt(min = 4, max = 14)
    public int portalGunCloseTicks = 9;
}
