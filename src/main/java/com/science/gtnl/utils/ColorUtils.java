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

    public static int enderElevatorColorCount() {
        return ENDER_ELEVATOR_COLORS.length;
    }

    public static int enderElevatorColor(int index) {
        return rgb(ENDER_ELEVATOR_COLORS[index]);
    }

}
