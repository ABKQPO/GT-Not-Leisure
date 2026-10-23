package com.science.gtnl.utils;

import com.gtnewhorizon.gtnhlib.color.ColorResource;

public class ColorUtils {

    private static final ColorResource.Factory color = new ColorResource.Factory("sciencenotleisure");

    public static final ColorResource guiTextColorGray = color.rgb("guiTextColorGray", "0x404040"),
        guiTextColorBlack = color.rgb("guiTextColorBlack", "0x000000"),
        guiTextColorWhite = color.rgb("guiTextColorWhite", "0xFFFFFF"),
        guiTextColorMuted = color.rgb("guiTextColorMuted", "0xB0B0B0"),
        guiTextColorDisabled = color.rgb("guiTextColorDisabled", "0x707070"),
        guiTextColorGold = color.rgb("guiTextColorGold", "0xFFAA00"),
        guiTextColorAqua = color.rgb("guiTextColorAqua", "0x00FFFF"),
        guiTextColorGreen = color.rgb("guiTextColorGreen", "0x00FF00"),
        guiTextColorRed = color.rgb("guiTextColorRed", "0xFF0000"),
        guiTextColorSoftYellow = color.rgb("guiTextColorSoftYellow", "0xFFFFA0"),
        guiTextColorLight = color.rgb("guiTextColorLight", "0xE0E0E0"),
        searchboxText = color.rgb("searchboxText", "0xFFFFFF"),
        craftingTreeRequest = color.rgb("craftingTreeRequest", "0xAAAAAA"),
        craftingTreeTask = color.rgb("craftingTreeTask", "0x777777"),
        craftConfirmPercent25 = color.rgb("craftConfirmPercent25", "0x1C4CA6"),
        craftConfirmPercent50 = color.rgb("craftConfirmPercent50", "0x1A751E"),
        reshuffleStatusFailed = color.rgb("reshuffleStatusFailed", "0xCC0000"),
        craftingDiagnosticTerminalLine = color.argb("craftingDiagnosticTerminalLine", "0x668C8C8C"),
        guiPanelBackground = color.argb("guiPanelBackground", "0x80101012"),
        guiPanelBorder = color.argb("guiPanelBorder", "0xFF404040"),
        guiOverlayBlack = color.argb("guiOverlayBlack", "0x80000000"),
        guiOverlayWhite = color.argb("guiOverlayWhite", "0x80FFFFFF"),
        opaqueBlack = color.argb("opaqueBlack", "0xFF000000"), opaqueWhite = color.argb("opaqueWhite", "0xFFFFFFFF"),
        guiBarInput = color.argb("guiBarInput", "0xFF4D7BFF"), guiBarOutput = color.argb("guiBarOutput", "0xFFFF8A1E"),
        guiStructureFill = color.argb("guiStructureFill", "0xFF2E2E2E"),
        guiStructureEdge = color.argb("guiStructureEdge", "0xFF707070"),
        guiFrameBorder = color.argb("guiFrameBorder", "0xFFC8C8C8"),
        guiButtonDisabled = color.argb("guiButtonDisabled", "0xFFA0A0A0"),
        guiButtonNormal = color.argb("guiButtonNormal", "0xFFE0E0E0"),
        guiButtonHover = color.argb("guiButtonHover", "0xFFFFFFA0"),
        itemGhostWhite = color.argb("itemGhostWhite", "0xE6FFFFFF"),
        textEffectInfernumRed = color.rgb("textEffectInfernumRed", "0xFF4500"),
        textEffectGenesisPurple = color.rgb("textEffectGenesisPurple", "0x7F51FF"),
        textEffectPulsePink = color.rgb("textEffectPulsePink", "0xFF309A"),
        textEffectPulseGreen = color.rgb("textEffectPulseGreen", "0x1CDE98"),
        textEffectCalamityRed = color.rgb("textEffectCalamityRed", "0xF21B1B"),
        textEffectNebulaPurple = color.rgb("textEffectNebulaPurple", "0x7455A9"),
        textEffectNebulaCyan = color.rgb("textEffectNebulaCyan", "0x18D2F0"),
        textEffectMoltenRed = color.rgb("textEffectMoltenRed", "0xBA3A16"),
        textEffectMoltenOrange = color.rgb("textEffectMoltenOrange", "0xFF3B12"),
        textEffectMoltenGold = color.rgb("textEffectMoltenGold", "0xFFB11A"),
        textEffectPixelTeal = color.rgb("textEffectPixelTeal", "0x35E6D0"),
        textEffectPixelLime = color.rgb("textEffectPixelLime", "0xE8FF96"),
        textEffectFrostBlue = color.rgb("textEffectFrostBlue", "0x73CDE0"),
        textEffectFrostWhite = color.rgb("textEffectFrostWhite", "0xF3FBFF"),
        stellarBackground = color.argb("stellarBackground", "0xFF03040C"),
        stellarNebulaBlue = color.argb("stellarNebulaBlue", "0x4F174D75"),
        stellarNebulaPurple = color.argb("stellarNebulaPurple", "0x4F57368A"),
        stellarAccent = color.rgb("stellarAccent", "0xFFCC44"),
        stellarPositive = color.rgb("stellarPositive", "0x44FF44"),
        stellarWarning = color.rgb("stellarWarning", "0xFF8844"),
        stellarLocked = color.rgb("stellarLocked", "0x505050"), stellarPanel = color.rgb("stellarPanel", "0x0C0C14"),
        stellarCoreLocked = color.rgb("stellarCoreLocked", "0x282828"),
        stellarCoreEdge = color.rgb("stellarCoreEdge", "0x404050"),
        stellarTextMuted = color.rgb("stellarTextMuted", "0x606060"),
        stellarPanelText = color.rgb("stellarPanelText", "0x9090A0"),
        stellarHeader = color.rgb("stellarHeader", "0xE0E0F0"),
        stellarDeepGray = color.rgb("stellarDeepGray", "0x606070"),
        stellarNeutral = color.rgb("stellarNeutral", "0x888888"),
        stellarBrightPositive = color.rgb("stellarBrightPositive", "0x88FF88"),
        stellarDanger = color.rgb("stellarDanger", "0xFF5544"),
        stellarSoftGray = color.rgb("stellarSoftGray", "0x808080"),
        stellarBlueGray = color.rgb("stellarBlueGray", "0x707080"),
        stellarParticleBlue = color.rgb("stellarParticleBlue", "0xB7DFFF"),
        stellarParticlePurple = color.rgb("stellarParticlePurple", "0x9B8FFF"),
        workshopAccentCyan = color.rgb("workshopAccentCyan", "0x55FFFF"),
        workshopTextSilver = color.rgb("workshopTextSilver", "0xBBBDBD"),
        workshopTextGray = color.rgb("workshopTextGray", "0x9C9C9C"),
        detravPanel = color.argb("detravPanel", "0xFFC6C6C6"),
        detravTextMuted = color.argb("detravTextMuted", "0xFF808080"),
        detravHint = color.argb("detravHint", "0xFF5E5E5E"),
        detravScrollbar = color.argb("detravScrollbar", "0xFFB0B0B0"),
        detravHighlight = color.argb("detravHighlight", "0xFFFFD700"),
        detravOreFill = color.rgb("detravOreFill", "0xEDD8FF"),
        detravOreOutline = color.rgb("detravOreOutline", "0xC071FF"),
        statusOnline = color.rgb("statusOnline", "0x00FF00"), statusWaiting = color.rgb("statusWaiting", "0x00AAFF"),
        statusWarning = color.rgb("statusWarning", "0xFFAA00"), statusError = color.rgb("statusError", "0xFF0000");

    public static final ColorResource effectInfernumRed = color.rgb("effectInfernumRed", "0xFF4500"),
        effectInfernumRedShadow = color.rgb("effectInfernumRedShadow", "0xC80000"),
        effectGenesisPurple = color.rgb("effectGenesisPurple", "0x7F51FF"),
        effectGenesisGold = color.rgb("effectGenesisGold", "0xFFEC47"),
        effectGenesisPink = color.rgb("effectGenesisPink", "0xF06DE4"),
        effectPulsePink = color.rgb("effectPulsePink", "0xFF309A"),
        effectPulsePinkHighlight = color.rgb("effectPulsePinkHighlight", "0xFFA9F0"),
        effectPulseGreen = color.rgb("effectPulseGreen", "0x1CDE98"),
        effectPulseGreenHighlight = color.rgb("effectPulseGreenHighlight", "0xA8F5E4"),
        effectCalamityRed = color.rgb("effectCalamityRed", "0xF21B1B"),
        effectCalamityRedShadow = color.rgb("effectCalamityRedShadow", "0xB4144B"),
        effectExoticRed = color.rgb("effectExoticRed", "0xFF6B6B"),
        effectExoticBlue = color.rgb("effectExoticBlue", "0x7DC4E1"),
        effectExoticGreen = color.rgb("effectExoticGreen", "0xD3EB6C"),
        effectSuperbossBlue = color.rgb("effectSuperbossBlue", "0x5F6F96"),
        effectSuperbossNavy = color.rgb("effectSuperbossNavy", "0x00213C"),
        effectCyan = color.rgb("effectCyan", "0x00FFFF"), effectSkyBlue = color.rgb("effectSkyBlue", "0x87CEEB"),
        effectYellow = color.rgb("effectYellow", "0xFFFF00"),
        effectAuricBrown = color.rgb("effectAuricBrown", "0x9D6E0B"),
        effectAuricWine = color.rgb("effectAuricWine", "0x4D0021"),
        effectAuricGold = color.rgb("effectAuricGold", "0xFEE775"),
        effectAuricBlue = color.rgb("effectAuricBlue", "0x00B7F1"),
        effectAuricCyan = color.rgb("effectAuricCyan", "0x5ACFFF"),
        effectEvercoldBlue = color.rgb("effectEvercoldBlue", "0x44678B"),
        effectEvercoldNavy = color.rgb("effectEvercoldNavy", "0x26435F"),
        effectStarsilverWhite = color.rgb("effectStarsilverWhite", "0xDEE6F4"),
        effectStarsilverNavy = color.rgb("effectStarsilverNavy", "0x282C5A"),
        effectNebulaPurple = color.rgb("effectNebulaPurple", "0x7455A9"),
        effectNebulaCyan = color.rgb("effectNebulaCyan", "0x18D2F0"),
        effectNebulaPink = color.rgb("effectNebulaPink", "0xD95FFF"),
        effectPrismaticRed = color.rgb("effectPrismaticRed", "0xFF3B6B"),
        effectPrismaticCyan = color.rgb("effectPrismaticCyan", "0x35E5FF"),
        effectPrismaticYellow = color.rgb("effectPrismaticYellow", "0xF8F55A"),
        effectQuantumCyan = color.rgb("effectQuantumCyan", "0x00F0FF"),
        effectQuantumPink = color.rgb("effectQuantumPink", "0xFF2C8A"),
        effectQuantumYellow = color.rgb("effectQuantumYellow", "0xF6FF65"),
        effectMoltenBrown = color.rgb("effectMoltenBrown", "0xBA3A16"),
        effectMoltenRed = color.rgb("effectMoltenRed", "0xFF3B12"),
        effectMoltenGold = color.rgb("effectMoltenGold", "0xFFB11A"),
        effectMoltenWhite = color.rgb("effectMoltenWhite", "0xFFF4A3"),
        effectPixelNavy = color.rgb("effectPixelNavy", "0x192C39"),
        effectPixelTeal = color.rgb("effectPixelTeal", "0x35E6D0"),
        effectPixelLime = color.rgb("effectPixelLime", "0xE8FF96"),
        effectRefractedNavy = color.rgb("effectRefractedNavy", "0x23384C"),
        effectRefractedCyan = color.rgb("effectRefractedCyan", "0x67D6E6"),
        effectRefractedPink = color.rgb("effectRefractedPink", "0xF6C6ED"),
        effectRuneTeal = color.rgb("effectRuneTeal", "0x315A54"),
        effectRuneGreen = color.rgb("effectRuneGreen", "0x68CFA7"),
        effectRuneWhite = color.rgb("effectRuneWhite", "0xE8F7D4"),
        effectCircuitTeal = color.rgb("effectCircuitTeal", "0x4B777B"),
        effectCircuitGreen = color.rgb("effectCircuitGreen", "0x31B7AD"),
        effectCircuitGold = color.rgb("effectCircuitGold", "0xF4DD83"),
        effectGhostBlue = color.rgb("effectGhostBlue", "0xA8DAEF"),
        effectGhostCyan = color.rgb("effectGhostCyan", "0x51B4D2"),
        effectGhostPurple = color.rgb("effectGhostPurple", "0xDABDF7"),
        effectPaperNavy = color.rgb("effectPaperNavy", "0x30323B"),
        effectPaperSilver = color.rgb("effectPaperSilver", "0xBBC1C9"),
        effectPaperCream = color.rgb("effectPaperCream", "0xF5E1C0"),
        effectFrostNavy = color.rgb("effectFrostNavy", "0x24455B"),
        effectFrostBlue = color.rgb("effectFrostBlue", "0x73CDE0"),
        effectFrostWhite = color.rgb("effectFrostWhite", "0xF3FBFF"),
        effectWhite = color.rgb("effectWhite", "0xFFFFFF"), vanillaBlack = color.rgb("vanillaBlack", "0x000000"),
        vanillaDarkBlue = color.rgb("vanillaDarkBlue", "0x0000AA"),
        vanillaDarkGreen = color.rgb("vanillaDarkGreen", "0x00AA00"),
        vanillaDarkAqua = color.rgb("vanillaDarkAqua", "0x00AAAA"),
        vanillaDarkRed = color.rgb("vanillaDarkRed", "0xAA0000"),
        vanillaDarkPurple = color.rgb("vanillaDarkPurple", "0xAA00AA"),
        vanillaGold = color.rgb("vanillaGold", "0xFFAA00"), vanillaGray = color.rgb("vanillaGray", "0xAAAAAA"),
        vanillaDarkGray = color.rgb("vanillaDarkGray", "0x555555"), vanillaBlue = color.rgb("vanillaBlue", "0x5555FF"),
        vanillaGreen = color.rgb("vanillaGreen", "0x55FF55"), vanillaAqua = color.rgb("vanillaAqua", "0x55FFFF"),
        vanillaRed = color.rgb("vanillaRed", "0xFF5555"),
        vanillaLightPurple = color.rgb("vanillaLightPurple", "0xFF55FF"),
        vanillaYellow = color.rgb("vanillaYellow", "0xFFFF55"), vanillaWhite = color.rgb("vanillaWhite", "0xFFFFFF");

    public static final ColorResource effectShimmering = color.rgb("effectShimmering", "0x8868C3"),
        effectGhostlyShape = color.rgb("effectGhostlyShape", "0x99CCFF"),
        effectPerfectPhysique = color.rgb("effectPerfectPhysique", "0xFFD700"),
        effectBattle = color.rgb("effectBattle", "0x7F60B8"), effectAwe = color.rgb("effectAwe", "0xFF00FF"),
        sporeDefault = color.rgb("sporeDefault", "0x66CCFF"), sporeOrange = color.rgb("sporeOrange", "0xFF9933"),
        sporeGreen = color.rgb("sporeGreen", "0x66FF66"),
        stellarIgnitionBranch = color.rgb("stellarIgnitionBranch", "0xFF783C"),
        stellarFusionBranch = color.rgb("stellarFusionBranch", "0x50B4FF"),
        stellarCollapseBranch = color.rgb("stellarCollapseBranch", "0xB450DC"),
        stellarVoidBranch = color.rgb("stellarVoidBranch", "0x50FFB4"),
        stellarRepeatableBranch = color.rgb("stellarRepeatableBranch", "0xDCC878");

    public static final ColorResource prospectorScanGray = color.argb("prospectorScanGray", "0xFF7D7D7D"),
        recipeOverlayYellow = color.rgb("recipeOverlayYellow", "0xFDD835");

    public static final ColorResource particleOrange = color.rgb("particleOrange", "0xFF6600");

    private static final ColorResource[] ENDER_ELEVATOR_COLORS = { color.rgb("enderElevatorWhite", "0xFFFFFF"),
        color.rgb("enderElevatorOrange", "0xD87F33"), color.rgb("enderElevatorMagenta", "0xB24CA5"),
        color.rgb("enderElevatorLightBlue", "0x6699D8"), color.rgb("enderElevatorYellow", "0xE5E533"),
        color.rgb("enderElevatorLime", "0x7FCC19"), color.rgb("enderElevatorPink", "0xF27FA5"),
        color.rgb("enderElevatorGray", "0x4C4C4C"), color.rgb("enderElevatorLightGray", "0x999999"),
        color.rgb("enderElevatorCyan", "0x4C7F99"), color.rgb("enderElevatorPurple", "0x7F3FB2"),
        color.rgb("enderElevatorBlue", "0x334CB2"), color.rgb("enderElevatorBrown", "0x664C33"),
        color.rgb("enderElevatorGreen", "0x667F33"), color.rgb("enderElevatorRed", "0x993333"),
        color.rgb("enderElevatorBlack", "0x191919") };

    public static int rgb(ColorResource resource) {
        return resource.getColor() & 0xFFFFFF;
    }

    public static int vanillaColor(int index) {
        switch (index) {
            case 0:
                return rgb(vanillaBlack);
            case 1:
                return rgb(vanillaDarkBlue);
            case 2:
                return rgb(vanillaDarkGreen);
            case 3:
                return rgb(vanillaDarkAqua);
            case 4:
                return rgb(vanillaDarkRed);
            case 5:
                return rgb(vanillaDarkPurple);
            case 6:
                return rgb(vanillaGold);
            case 7:
                return rgb(vanillaGray);
            case 8:
                return rgb(vanillaDarkGray);
            case 9:
                return rgb(vanillaBlue);
            case 10:
                return rgb(vanillaGreen);
            case 11:
                return rgb(vanillaAqua);
            case 12:
                return rgb(vanillaRed);
            case 13:
                return rgb(vanillaLightPurple);
            case 14:
                return rgb(vanillaYellow);
            case 15:
                return rgb(vanillaWhite);
            default:
                throw new IllegalArgumentException("Unknown vanilla color index: " + index);
        }
    }

    public static int enderElevatorColorCount() {
        return ENDER_ELEVATOR_COLORS.length;
    }

    public static int enderElevatorColor(int index) {
        return rgb(ENDER_ELEVATOR_COLORS[index]);
    }

}
