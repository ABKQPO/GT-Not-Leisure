package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultBoolean;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultDouble;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultInt;
import com.gtnewhorizon.gtnhlib.config.Config.LangKey;
import com.gtnewhorizon.gtnhlib.config.Config.Name;
import com.gtnewhorizon.gtnhlib.config.Config.RangeInt;
import com.gtnewhorizon.gtnhlib.config.Config.RequiresMcRestart;
import com.gtnewhorizon.gtnhlib.config.Config.Sync;
import com.science.gtnl.ScienceNotLeisure;

@Config(
    modid = ScienceNotLeisure.MODID,
    category = "machine",
    configSubDirectory = ConfigRegistry.DIRECTORY,
    filename = ConfigRegistry.MACHINE_FILE)
@Config.LangKeyPattern(pattern = ConfigRegistry.LANG_PATTERN, fullyQualified = true)
@Sync
@Comment("Machine section")
public class MachineConfig {

    @Comment("Default energy consumption per operation (in AE/t)")
    @LangKey("gtnl.gui.config.machine.beam_former_energy_consume")
    @DefaultDouble(10)
    public static double beamFormerEnergyConsume = 10;

    @Comment("Default maximum beam length (in blocks)")
    @LangKey("gtnl.gui.config.machine.beam_former_length")
    @DefaultInt(256)
    public static int beamFormerLength = 256;

    @Comment("Allow interaction with machines from their main facing (similar to GTM behavior)")
    @LangKey("gtnl.gui.config.machine.allow_main_facing_interaction")
    @DefaultBoolean(false)
    public static boolean allowMainFacingInteraction = false;

    @Comment("Set Machine Can Use Laser Hatch")
    @DefaultBoolean(false)
    public static boolean enableLaserHatch = false;

    @Comment("Enable Purification Plant Buff, add Wireless mode and max long parallels")
    @DefaultBoolean(false)
    @RequiresMcRestart
    public static boolean enablePurificationPlantBuff = false;

    @Comment("Enable Output Change Function")
    @DefaultBoolean(true)
    @RequiresMcRestart
    public static boolean enableRecipeOutputChance = true;

    @Comment("Enable Void Miner Tweak, allows you to override target dimension that Void Miner mines")
    @DefaultBoolean(true)
    public static boolean enableVoidMinerTweak = true;

    @Comment("Change Recipe Item Output, like QFT")
    @DefaultDouble(2.5)
    public static double recipeOutputChance = 2.5;

    @Name("bee_breeder")
    public static final BeeBreeder bee_breeder = new BeeBreeder();

    @Sync
    @Comment("Large Bee Breeder")
    public static class BeeBreeder {

        @Comment({
            "Required voltage per species: species UID=HV, EV, IV, LuV, ZPM, UV or UHV. Unlisted species require UHV.",
            "Missing or disabled species are ignored. Duplicate UIDs use the last valid entry. Restart to apply." })
        @LangKey("gtnl.gui.config.machine.bee_breeder.species_voltage_tiers")
        @RequiresMcRestart
        public String[] speciesVoltageTiers = { "forestry.speciesForest=HV", "forestry.speciesMeadows=HV",
            "forestry.speciesCommon=HV", "forestry.speciesCultivated=HV", "forestry.speciesNoble=HV",
            "forestry.speciesMajestic=EV", "forestry.speciesImperial=EV", "forestry.speciesDiligent=HV",
            "forestry.speciesUnweary=EV", "forestry.speciesIndustrious=EV", "forestry.speciesSteadfast=EV",
            "forestry.speciesValiant=EV", "forestry.speciesHeroic=HV", "forestry.speciesSinister=HV",
            "forestry.speciesFiendish=EV", "forestry.speciesDemonic=EV", "forestry.speciesModest=HV",
            "forestry.speciesFrugal=EV", "forestry.speciesAustere=EV", "forestry.speciesTropical=HV",
            "forestry.speciesExotic=IV", "forestry.speciesEdenic=IV", "forestry.speciesEnded=EV",
            "forestry.speciesSpectral=IV", "forestry.speciesPhantasmal=IV", "forestry.speciesWintry=HV",
            "forestry.speciesIcy=IV", "forestry.speciesGlacial=IV", "forestry.speciesVindictive=IV",
            "forestry.speciesVengeful=IV", "forestry.speciesAvenging=IV", "forestry.speciesLeporine=HV",
            "forestry.speciesMerry=HV", "forestry.speciesTipsy=HV", "forestry.speciesTricky=EV",
            "forestry.speciesRural=EV", "forestry.speciesFarmerly=EV", "forestry.speciesAgrarian=IV",
            "forestry.speciesMarshy=HV", "forestry.speciesMiry=EV", "forestry.speciesBoggy=EV",
            "forestry.speciesMonastic=EV", "forestry.speciesSecluded=IV", "forestry.speciesHermitic=IV",
            "gregtech.bee.speciesClay=IV", "gregtech.bee.speciesSlimeball=IV", "gregtech.bee.speciesPeat=IV",
            "gregtech.bee.speciesStickyresin=IV", "gregtech.bee.speciesCoal=IV", "gregtech.bee.speciesOil=IV",
            "gregtech.bee.speciesSandwich=IV", "gregtech.bee.speciesAsh=IV", "gregtech.bee.speciesApatite=IV",
            "gregtech.bee.speciesFertilizer=IV", "gregtech.bee.speciesPhosphorus=IV", "gregtech.bee.speciesTea=IV",
            "gregtech.bee.speciesMica=LuV", "gregtech.bee.speciesRedstone=IV", "gregtech.bee.speciesLapis=IV",
            "gregtech.bee.speciesCertus=IV", "gregtech.bee.speciesFluix=IV", "gregtech.bee.speciesDiamond=IV",
            "gregtech.bee.speciesRuby=IV", "gregtech.bee.speciesSapphire=IV", "gregtech.bee.speciesOlivine=IV",
            "gregtech.bee.speciesEmerald=IV", "gregtech.bee.speciesRedgarnet=IV", "gregtech.bee.speciesYellowgarnet=IV",
            "gregtech.bee.speciesFirestone=LuV", "gregtech.bee.speciesPrismatic=LuV", "gregtech.bee.speciesCopper=IV",
            "gregtech.bee.speciesTin=IV", "gregtech.bee.speciesLead=IV", "gregtech.bee.speciesIron=IV",
            "gregtech.bee.speciesSteel=IV", "gregtech.bee.speciesNickel=IV", "gregtech.bee.speciesZinc=IV",
            "gregtech.bee.speciesSilver=IV", "gregtech.bee.speciesCryolite=IV", "gregtech.bee.speciesGold=IV",
            "gregtech.bee.speciesArsenic=IV", "gregtech.bee.speciesAluminium=IV", "gregtech.bee.speciesTitanium=LuV",
            "gregtech.bee.speciesGlowstone=IV", "gregtech.bee.speciesSunnarium=LuV", "gregtech.bee.speciesChrome=LuV",
            "gregtech.bee.speciesManganese=LuV", "gregtech.bee.speciesTungsten=ZPM", "gregtech.bee.speciesPlatinum=ZPM",
            "gregtech.bee.speciesIridium=ZPM", "gregtech.bee.speciesOsmium=ZPM", "gregtech.bee.speciesSalty=IV",
            "gregtech.bee.speciesLithium=IV", "gregtech.bee.speciesElectrotine=IV", "gregtech.bee.speciesSulfur=IV",
            "gregtech.bee.speciesIndium=ZPM", "gregtech.bee.speciesCoolant=IV", "gregtech.bee.speciesEnergy=IV",
            "gregtech.bee.speciesLapotron=IV", "gregtech.bee.speciesPyrotheum=IV", "gregtech.bee.speciesCryotheum=LuV",
            "gregtech.bee.speciesRedalloy=IV", "gregtech.bee.speciesRedstonealloy=IV",
            "gregtech.bee.speciesConductiveiron=IV", "gregtech.bee.speciesEnergeticalloy=IV",
            "gregtech.bee.speciesVibrantalloy=LuV", "gregtech.bee.speciesElectricalsteel=IV",
            "gregtech.bee.speciesDarksteel=IV", "gregtech.bee.speciesPulsatingiron=IV",
            "gregtech.bee.speciesStainlesssteel=IV", "gregtech.bee.speciesEnderium=ZPM",
            "gregtech.bee.speciesBedrockium=ZPM", "gregtech.bee.speciesThaumiumdust=IV",
            "gregtech.bee.speciesThaumiumshard=LuV", "gregtech.bee.speciesAmber=IV",
            "gregtech.bee.speciesQuicksilver=IV", "gregtech.bee.speciesSalismundus=IV",
            "gregtech.bee.speciesTainted=LuV", "gregtech.bee.speciesMithril=ZPM",
            "gregtech.bee.speciesAstralsilver=LuV", "gregtech.bee.speciesThauminite=LuV",
            "gregtech.bee.speciesShadowmetal=ZPM", "gregtech.bee.speciesDivided=LuV",
            "gregtech.bee.speciesCaelestis=LuV", "gregtech.bee.speciesSparkeling=LuV",
            "gregtech.bee.speciesEssentia=ZPM", "gregtech.bee.speciesDrake=ZPM", "gregtech.bee.speciesUranium=ZPM",
            "gregtech.bee.speciesPlutonium=ZPM", "gregtech.bee.speciesNaquadah=ZPM",
            "gregtech.bee.speciesNaquadria=ZPM", "gregtech.bee.speciesDob=ZPM", "gregtech.bee.speciesThorium=ZPM",
            "gregtech.bee.speciesLutetium=ZPM", "gregtech.bee.speciesAmericium=ZPM",
            "gregtech.bee.speciesNeutronium=UV", "gregtech.bee.speciesNaga=IV", "gregtech.bee.speciesLich=IV",
            "gregtech.bee.speciesHydra=LuV", "gregtech.bee.speciesUrghast=LuV", "gregtech.bee.speciesSnowqueen=ZPM",
            "gregtech.bee.speciesEnddust=IV", "gregtech.bee.speciesEndium=IV", "gregtech.bee.speciesStardust=LuV",
            "gregtech.bee.speciesEctoplasma=ZPM", "gregtech.bee.speciesArcaneshards=ZPM",
            "gregtech.bee.speciesDragonessence=ZPM", "gregtech.bee.speciesFireessence=ZPM",
            "gregtech.bee.speciesEndermanhead=ZPM", "gregtech.bee.speciesSilverfish=IV", "gregtech.bee.speciesRune=ZPM",
            "gregtech.bee.speciesWalrus=IV", "gregtech.bee.speciesMachinist=IV", "gregtech.bee.speciesSpace=IV",
            "gregtech.bee.speciesMeteoriciron=IV", "gregtech.bee.speciesDesh=IV", "gregtech.bee.speciesLedox=ZPM",
            "gregtech.bee.speciesCallistoice=ZPM", "gregtech.bee.speciesMytryl=ZPM", "gregtech.bee.speciesQuantium=ZPM",
            "gregtech.bee.speciesOriharukon=ZPM", "gregtech.bee.speciesInfusedgold=ZPM",
            "gregtech.bee.speciesMysteriouscrystal=UV", "gregtech.bee.speciesBlackplutonium=UV",
            "gregtech.bee.speciesTrinium=UV", "gregtech.bee.speciesMoon=IV", "gregtech.bee.speciesMars=IV",
            "gregtech.bee.speciesPhobos=IV", "gregtech.bee.speciesDeimos=IV", "gregtech.bee.speciesCeres=LuV",
            "gregtech.bee.speciesJupiter=ZPM", "gregtech.bee.speciesIo=ZPM", "gregtech.bee.speciesEuropa=ZPM",
            "gregtech.bee.speciesGanymede=ZPM", "gregtech.bee.speciesCallisto=ZPM", "gregtech.bee.speciesSaturn=ZPM",
            "gregtech.bee.speciesEnceladus=ZPM", "gregtech.bee.speciesTitan=ZPM", "gregtech.bee.speciesUranus=ZPM",
            "gregtech.bee.speciesMiranda=ZPM", "gregtech.bee.speciesOberon=ZPM", "gregtech.bee.speciesNeptune=ZPM",
            "gregtech.bee.speciesProteus=ZPM", "gregtech.bee.speciesTriton=ZPM", "gregtech.bee.speciesPluto=UV",
            "gregtech.bee.speciesHaumea=UV", "gregtech.bee.speciesMakemake=UV", "gregtech.bee.speciesCentauri=UHV",
            "gregtech.bee.speciesAcentauri=UHV", "gregtech.bee.speciesTceti=UHV", "gregtech.bee.speciesTcetie=UHV",
            "gregtech.bee.speciesSeaweed=UHV", "gregtech.bee.speciesBarnarda=UHV", "gregtech.bee.speciesBarnardac=UHV",
            "gregtech.bee.speciesBarnardae=UHV", "gregtech.bee.speciesBarnardaf=UHV", "gregtech.bee.speciesVega=UHV",
            "gregtech.bee.speciesVegab=UHV", "gregtech.bee.speciesMercury=IV", "gregtech.bee.speciesVenus=IV",
            "gregtech.bee.speciesCosmicneutronium=UHV", "gregtech.bee.speciesInfinitycatalyst=UHV",
            "gregtech.bee.speciesInfinity=UHV", "gregtech.bee.speciesKevlar=UHV", "gregtech.bee.speciesHelium=IV",
            "gregtech.bee.speciesArgon=ZPM", "gregtech.bee.speciesNeon=ZPM", "gregtech.bee.speciesKrypton=ZPM",
            "gregtech.bee.speciesXenon=ZPM", "gregtech.bee.speciesOxygen=IV", "gregtech.bee.speciesHydrogen=IV",
            "gregtech.bee.speciesNitrogen=IV", "gregtech.bee.speciesFluorine=IV", "gregtech.bee.speciesRareearth=IV",
            "gregtech.bee.speciesNeodymium=ZPM", "gregtech.bee.speciesEuropium=UV", "gregtech.bee.speciesAir=IV",
            "gregtech.bee.speciesFire=IV", "gregtech.bee.speciesWater=IV", "gregtech.bee.speciesEarth=IV",
            "gregtech.bee.speciesOrder=IV", "gregtech.bee.speciesChaos=IV", "gregtech.bee.speciesNethershard=ZPM",
            "gregtech.bee.speciesEndshard=ZPM", "gregtech.bee.speciesUnknownwater=ZPM",
            "gregtech.bee.speciesManasteel=IV", "gregtech.bee.speciesMmm=LuV", "gregtech.bee.speciesElven=ZPM",
            "gregtech.bee.speciesTerrasteel=ZPM", "gregtech.bee.speciesGaiaspirit=ZPM",
            "gregtech.bee.speciesJaegermeister=ZPM", "gtpp.bee.speciesDragon Blood=UHV", "gtpp.bee.speciesForce=IV",
            "magicbees.speciesMystical=HV", "magicbees.speciesSorcerous=HV", "magicbees.speciesUnusual=HV",
            "magicbees.speciesAttuned=HV", "magicbees.speciesEldritch=HV", "magicbees.speciesEsoteric=EV",
            "magicbees.speciesMysterious=EV", "magicbees.speciesArcane=IV", "magicbees.speciesCharmed=EV",
            "magicbees.speciesEnchanted=EV", "magicbees.speciesSupernatural=IV", "magicbees.speciesEthereal=IV",
            "magicbees.speciesWatery=IV", "magicbees.speciesEarthen=IV", "magicbees.speciesFirey=IV",
            "magicbees.speciesWindy=IV", "magicbees.speciesPupil=IV", "magicbees.speciesScholarly=IV",
            "magicbees.speciesSavant=LuV", "magicbees.speciesAware=IV", "magicbees.speciesSpirit=IV",
            "magicbees.speciesSoul=LuV", "magicbees.speciesSkulking=EV", "magicbees.speciesGhastly=IV",
            "magicbees.speciesSpidery=EV", "magicbees.speciesSmouldering=IV", "magicbees.speciesTCBrainy=EV",
            "magicbees.speciesBigBad=IV", "magicbees.speciesTCChicken=EV", "magicbees.speciesTCBeef=EV",
            "magicbees.speciesTCPork=EV", "magicbees.speciesTCBatty=IV", "magicbees.speciesSheepish=IV",
            "magicbees.speciesHorse=IV", "magicbees.speciesCatty=IV", "magicbees.speciesTimely=IV",
            "magicbees.speciesLordly=LuV", "magicbees.speciesDoctoral=LuV", "magicbees.speciesInfernal=EV",
            "magicbees.speciesHateful=EV", "magicbees.speciesSpiteful=EV", "magicbees.speciesWithering=IV",
            "magicbees.speciesOblivion=EV", "magicbees.speciesNameless=IV", "magicbees.speciesAbandoned=LuV",
            "magicbees.speciesForlorn=LuV", "magicbees.speciesDraconic=LuV", "magicbees.speciesIron=IV",
            "magicbees.speciesGold=IV", "magicbees.speciesCopper=IV", "magicbees.speciesTin=IV",
            "magicbees.speciesSilver=IV", "magicbees.speciesLead=IV", "magicbees.speciesAluminum=IV",
            "magicbees.speciesArdite=IV", "magicbees.speciesCobalt=IV", "magicbees.speciesManyullyn=IV",
            "magicbees.speciesDiamond=IV", "magicbees.speciesEmerald=IV", "magicbees.speciesApatite=IV",
            "magicbees.speciesSilicon=IV", "magicbees.speciesCertus=IV", "magicbees.speciesFluix=IV",
            "magicbees.speciesMutable=EV", "magicbees.speciesTransmuting=EV", "magicbees.speciesCrumbling=EV",
            "magicbees.speciesInvisible=EV", "magicbees.speciesTCAir=IV", "magicbees.speciesTCFire=IV",
            "magicbees.speciesTCWater=IV", "magicbees.speciesTCEarth=IV", "magicbees.speciesTCOrder=IV",
            "magicbees.speciesTCChaos=IV", "magicbees.speciesTCEssentia=IV", "magicbees.speciesTCVis=IV",
            "magicbees.speciesTCRejuvenating=LuV", "magicbees.speciesTCEmpowering=LuV", "magicbees.speciesTCNexus=LuV",
            "magicbees.speciesTCFlux=LuV", "magicbees.speciesTCPure=LuV", "magicbees.speciesTCHungry=LuV",
            "magicbees.speciesTCWispy=LuV", "magicbees.speciesTCVoid=LuV", "magicbees.speciesEEMinium=EV",
            "magicbees.speciesAMEssence=IV", "magicbees.speciesAMQuintessence=LuV", "magicbees.speciesAMEarth=LuV",
            "magicbees.speciesAMAir=LuV", "magicbees.speciesAMFire=LuV", "magicbees.speciesAMWater=LuV",
            "magicbees.speciesAMLightning=LuV", "magicbees.speciesAMPlant=LuV", "magicbees.speciesAMIce=LuV",
            "magicbees.speciesAMArcane=LuV", "magicbees.speciesAMVortex=LuV", "magicbees.speciesAMWight=LuV",
            "magicbees.speciesTEBlizzy=EV", "magicbees.speciesTEGelid=IV", "magicbees.speciesTEDante=LuV",
            "magicbees.speciesTEPyro=ZPM", "magicbees.speciesTEShocking=LuV", "magicbees.speciesTEAmped=ZPM",
            "magicbees.speciesTEGrounded=LuV", "magicbees.speciesTERocking=ZPM", "magicbees.speciesTEElectrum=IV",
            "magicbees.speciesTEPlatinum=LuV", "magicbees.speciesTENickel=IV", "magicbees.speciesTEInvar=IV",
            "magicbees.speciesTEBronze=IV", "magicbees.speciesTECoal=IV", "magicbees.speciesTEDestabilized=IV",
            "magicbees.speciesTELux=LuV", "magicbees.speciesTEWinsome=LuV", "magicbees.speciesTEEndearing=LuV",
            "magicbees.speciesTESignalus=IV", "magicbees.speciesTELumius=ZPM", "magicbees.speciesRSAFluxed=LuV",
            "magicbees.speciesBotRooted=EV", "magicbees.speciesBotBotanic=EV", "magicbees.speciesBotBlossom=IV",
            "magicbees.speciesBotFloral=IV", "magicbees.speciesBotVazbee=EV", "magicbees.speciesBotSomnolent=LuV",
            "magicbees.speciesBotDreaming=LuV", "magicbees.speciesBotAlfheim=IV", "magicbees.speciesAESkystone=IV",
            "extrabees.species.arid=EV", "extrabees.species.barren=IV", "extrabees.species.desolate=IV",
            "extrabees.species.decomposing=IV", "extrabees.species.gnawing=IV", "extrabees.species.rotten=IV",
            "extrabees.species.bone=IV", "extrabees.species.creeper=IV", "extrabees.species.rock=HV",
            "extrabees.species.stone=EV", "extrabees.species.granite=EV", "extrabees.species.mineral=IV",
            "extrabees.species.copper=IV", "extrabees.species.tin=IV", "extrabees.species.iron=IV",
            "extrabees.species.lead=IV", "extrabees.species.zinc=IV", "extrabees.species.titanium=IV",
            "extrabees.species.tungstate=IV", "extrabees.species.nickel=IV", "extrabees.species.gold=IV",
            "extrabees.species.silver=IV", "extrabees.species.platinum=LuV", "extrabees.species.lapis=IV",
            "extrabees.species.emerald=IV", "extrabees.species.ruby=IV", "extrabees.species.sapphire=IV",
            "extrabees.species.diamond=IV", "extrabees.species.unstable=LuV", "extrabees.species.nuclear=LuV",
            "extrabees.species.radioactive=LuV", "extrabees.species.yellorium=LuV", "extrabees.species.cyanite=LuV",
            "extrabees.species.blutonium=ZPM", "extrabees.species.ancient=EV", "extrabees.species.primeval=IV",
            "extrabees.species.prehistoric=IV", "extrabees.species.relic=LuV", "extrabees.species.coal=IV",
            "extrabees.species.resin=IV", "extrabees.species.oil=IV", "extrabees.species.distilled=LuV",
            "extrabees.species.fuel=LuV", "extrabees.species.creosote=IV", "extrabees.species.latex=IV",
            "extrabees.species.water=HV", "extrabees.species.river=EV", "extrabees.species.ocean=EV",
            "extrabees.species.ink=EV", "extrabees.species.growing=EV", "extrabees.species.farm=IV",
            "extrabees.species.thriving=EV", "extrabees.species.blooming=IV", "extrabees.species.sweet=EV",
            "extrabees.species.sugar=EV", "extrabees.species.ripening=EV", "extrabees.species.fruit=IV",
            "extrabees.species.alcohol=IV", "extrabees.species.milk=IV", "extrabees.species.coffee=IV",
            "extrabees.species.swamp=EV", "extrabees.species.boggy=IV", "extrabees.species.fungal=IV",
            "extrabees.species.basalt=HV", "extrabees.species.tempered=EV", "extrabees.species.volcanic=IV",
            "extrabees.species.glowstone=IV", "extrabees.species.malicious=EV", "extrabees.species.infectious=EV",
            "extrabees.species.virulent=IV", "extrabees.species.viscous=IV", "extrabees.species.glutinous=IV",
            "extrabees.species.sticky=IV", "extrabees.species.corrosive=IV", "extrabees.species.caustic=IV",
            "extrabees.species.acidic=LuV", "extrabees.species.excited=HV", "extrabees.species.energetic=EV",
            "extrabees.species.ecstatic=EV", "extrabees.species.artic=EV", "extrabees.species.freezing=EV",
            "extrabees.species.shadow=EV", "extrabees.species.darkened=EV", "extrabees.species.abyss=IV",
            "extrabees.species.red=HV", "extrabees.species.yellow=HV", "extrabees.species.blue=HV",
            "extrabees.species.green=HV", "extrabees.species.black=HV", "extrabees.species.white=HV",
            "extrabees.species.brown=HV", "extrabees.species.orange=HV", "extrabees.species.cyan=HV",
            "extrabees.species.purple=HV", "extrabees.species.gray=HV", "extrabees.species.lightblue=HV",
            "extrabees.species.pink=HV", "extrabees.species.limegreen=HV", "extrabees.species.magenta=HV",
            "extrabees.species.lightgray=HV", "extrabees.species.celebratory=IV", "extrabees.species.jaded=IV",
            "extrabees.species.chad=IV", "extrabees.species.hazardous=IV", "extrabees.species.quantum=LuV",
            "extrabees.species.unusual=IV", "extrabees.species.spatial=IV", "extrabees.species.mystical=EV",
            "gregtech.bee.speciesExplosive=LuV", "gregtech.bee.speciesNetherite=LuV" };

    }

    public static final QuantumComputer quantum_computer = new QuantumComputer();

    @Comment("Quantum Computer")
    public static class QuantumComputer {

        @Comment("Maximum size allowed for a Quantum Computer multiblock structure")
        @RangeInt(min = 3)
        @DefaultInt(7)
        public int maxMultiblockSize = 7;

        @Comment("Maximum number of multi threader allowed in a Quantum Computer")
        @RangeInt(min = 1)
        @DefaultInt(1)
        public int maxMultiThreader = 1;

        @Comment("Maximum number of data entangler allowed in a Quantum Computer")
        @RangeInt(min = 1)
        @DefaultInt(1)
        public int maxDataEntangler = 1;

        @Comment("Log the step breakdown of every quantum crafting calculation")
        @DefaultBoolean(false)
        public boolean enableCalculationLog = false;

        @Comment("Log stage timings and counters of every quantum crafting calculation")
        @DefaultBoolean(false)
        public boolean enableCalculationPerformanceLog = false;

        @Comment("Seconds of calculation time a quantum crafting plan with missing items may spend on trying alternative patterns one by one, after which the best plan found so far is kept. 0 never tries them")
        @RangeInt(min = 0)
        @DefaultInt(5)
        public int calculationFallbackSeconds = 5;

        @Comment("Seconds of calculation time one quantum crafting calculation may take on a dedicated server before it fails, like MaxCraftingSteps of AE2. 0 disables the limit")
        @RangeInt(min = 0)
        @DefaultInt(30)
        public int calculationLimitSeconds = 30;
    }

    public static final MeteorMiner meteor_miner = new MeteorMiner();

    @Comment("Meteor Miner")
    public static class MeteorMiner {

        @Comment("Set the Meteor Miner how many every cycle break a block")
        @DefaultInt(1)
        @RequiresMcRestart
        public int meteorMinerMaxBlockPerCycle = 1;

        @Comment("Set the Meteor Miner how many every cycle break row blocks")
        @DefaultInt(1)
        @RequiresMcRestart
        public int meteorMinerMaxRowPerCycle = 1;
    }

    public static final ArtificialStar artificial_star = new ArtificialStar();

    @Comment("Artificial Star")
    public static class ArtificialStar {

        @Comment("Set the power generation of EU Every Depleted Excited Naquadah FuelRod")
        @DefaultInt(4125000)
        @RequiresMcRestart
        public int euEveryDepletedExcitedNaquadahFuelRod = 4125000;

        @Comment("Open RenderDefaultArtificialStar rendering")
        @Name("EnableDefaultRender")
        @DefaultBoolean(true)
        public boolean enableRenderDefaultArtificialStar = true;

        @Comment("Set the power generation of EU Every Enhancement Core")
        @DefaultInt(100)
        @RequiresMcRestart
        public int euEveryEnhancementCore = 100;

        @Comment("Set secondsOfArtificialStarProgressCycleTime running time")
        @DefaultDouble(6.4)
        public double secondsOfArtificialStarProgressCycleTime = 6.4;
    }

    public static final EternalWorkshop eternal_gregtech_workshop = new EternalWorkshop();

    @Comment("Eternal Workshop")
    public static class EternalWorkshop {

        @Comment("Enable Eternal GregTech Workshop Spiral Render, like DNA")
        @DefaultBoolean(false)
        public boolean spiralRender = false;
    }

    public static final PortalAlfheim portal_to_alfheim = new PortalAlfheim();

    @Comment("Portal Alfheim")
    public static class PortalAlfheim {

        @Comment("Setting this to false will reduce the Portal To Alfheim explosion to little more then a tnt blast")
        @DefaultBoolean(true)
        public boolean bigBoom = true;
    }
}
