package com.science.gtnl.loader;

import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.utils.enums.GTNLItemList;

public class AchievementsLoader implements IContentLoader {

    public static AchievementPage gtnlAchievementsPage;
    public static Achievement welcome;
    public static Achievement installAllCommunityMod;

    public static void registry() {
        welcome = new Achievement("gtnl.welcome", "gtnl.welcome", 0, 0, GTNLItemList.TestItem.get(1), null)
            .registerStat();

        installAllCommunityMod = new Achievement(
            "gtnl.installAllCommunityMod",
            "gtnl.installAllCommunityMod",
            2,
            1,
            GTNLItemList.NewHorizonsCoil.get(1),
            welcome).setSpecial()
                .registerStat();

        gtnlAchievementsPage = new AchievementPage(ScienceNotLeisure.MODNAME, welcome);
        AchievementPage.registerAchievementPage(gtnlAchievementsPage);
    }

    @Override
    public String name() {
        return "achievements";
    }

    @Override
    public void postInit() {
        registry();
    }
}
