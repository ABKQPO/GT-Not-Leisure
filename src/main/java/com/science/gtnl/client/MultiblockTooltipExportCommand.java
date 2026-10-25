package com.science.gtnl.client;

import java.io.File;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.utils.structure.MultiblockTooltipAudit;
import com.science.gtnl.utils.structure.MultiblockTooltipAudit.ExportResult;

public class MultiblockTooltipExportCommand extends CommandBase {

    private static final String EXPORT_DIRECTORY = "gtnl-tooltip-export";
    private static final String DONE_KEY = "gtnl.tooltip_export.done";
    private static final String FAILED_KEY = "gtnl.tooltip_export.failed";

    @Override
    public String getCommandName() {
        return "gtnltooltipexport";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/gtnltooltipexport";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        File directory = new File(Minecraft.getMinecraft().mcDataDir, EXPORT_DIRECTORY);
        try {
            ExportResult result = MultiblockTooltipAudit.export(directory);
            sender.addChatMessage(
                new ChatComponentTranslation(
                    DONE_KEY,
                    result.machines(),
                    result.suspicious(),
                    result.report()
                        .getAbsolutePath()));
        } catch (IOException e) {
            ScienceNotLeisure.LOG.error("Cannot export the multiblock tooltips", e);
            sender.addChatMessage(new ChatComponentTranslation(FAILED_KEY, e.getMessage()));
        }
    }
}
