package com.science.gtnl.common.wireless;

import static com.science.gtnl.common.wireless.WirelessCardActions.APPLY_FILTER;
import static com.science.gtnl.common.wireless.WirelessCardActions.BACK;
import static com.science.gtnl.common.wireless.WirelessCardActions.CLOSE_DETAILS;
import static com.science.gtnl.common.wireless.WirelessCardActions.CYCLE_VISUALISATION;
import static com.science.gtnl.common.wireless.WirelessCardActions.HIGHLIGHT;
import static com.science.gtnl.common.wireless.WirelessCardActions.NEXT_PAGE;
import static com.science.gtnl.common.wireless.WirelessCardActions.OPEN_DETAILS;
import static com.science.gtnl.common.wireless.WirelessCardActions.PREVIOUS_PAGE;
import static com.science.gtnl.common.wireless.WirelessCardActions.REFRESH;
import static com.science.gtnl.common.wireless.WirelessCardActions.RENAME;
import static com.science.gtnl.common.wireless.WirelessCardActions.SET_PAGE_SIZE;
import static com.science.gtnl.common.wireless.WirelessCardActions.TELEPORT;
import static com.science.gtnl.common.wireless.WirelessCardActions.TOGGLE_AUTO;
import static com.science.gtnl.common.wireless.WirelessCardActions.TOGGLE_GROUPING;
import static com.science.gtnl.common.wireless.WirelessCardActions.TOGGLE_PAUSE;
import static com.science.gtnl.common.wireless.WirelessCardActions.isRowAction;
import static com.science.gtnl.common.wireless.WirelessCardActions.rowIndex;
import static com.science.gtnl.common.wireless.WirelessCardActions.type;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;

import com.science.gtnl.common.wireless.WirelessCardViewBuilder.Navigation;
import com.science.gtnl.common.wireless.WirelessMachineGroups.Row;

final class WirelessCardActionHandler {

    private final WirelessCardContainer session;
    private long nextRequestTick = Long.MIN_VALUE;

    WirelessCardActionHandler(WirelessCardContainer session) {
        this.session = session;
    }

    void execute(long revision, int action, String text, String dimension, int filterState, long nameRevision) {
        EntityPlayer player = session.player;
        ItemStack card = session.card;
        WirelessCardBinding binding = session.binding;
        Navigation navigation = session.view.navigation;
        List<Row> rows = session.snapshot == null ? List.of() : session.snapshot.rows();
        // Closing a client panel must not be lost to throttling or a changing row revision.
        if (action == CLOSE_DETAILS) {
            if (navigation.closeDetails()) session.update(true);
            return;
        }
        int actionType = type(action);
        int index = rowIndex(action);
        boolean teleport = actionType == TELEPORT && index < rows.size();
        if (!acceptRequest(player.worldObj.getTotalWorldTime())) return;
        if (actionType == OPEN_DETAILS) {
            session.update(false);
            rows = session.snapshot.rows();
        }
        if (session.revision != revision && action != APPLY_FILTER && action != SET_PAGE_SIZE) {
            session.update(true);
            return;
        }
        // Group rows are navigation only, even if a client submits a hidden mutation button.
        if (isRowAction(action) && index < rows.size()
            && rows.get(index)
                .group()
            && actionType != OPEN_DETAILS) return;
        if (action == CYCLE_VISUALISATION) {
            WirelessCardVisualisation.cycle(card, player);
        } else if (action == SET_PAGE_SIZE) {
            if (!navigation.resize(filterState)) return;
        } else if (action == APPLY_FILTER) {
            try {
                navigation.filter(text, dimension, filterState);
            } catch (IllegalArgumentException invalid) {
                message("filter_invalid");
            }
        } else if (action == RENAME && navigation.detailNode != null && !rows.isEmpty() && binding != null) {
            var row = rows.get(0)
                .link();
            if (session.view.nameRevision() != nameRevision) {
                message("stale");
            } else {
                try {
                    if (!WirelessOperations.rename(player, binding.source(), row, text)) message("denied");
                } catch (WirelessOperations.Denied denied) {
                    message("denied");
                } catch (IllegalStateException stale) {
                    message("stale");
                }
            }
        } else if (teleport && binding != null && player instanceof EntityPlayerMP serverPlayer) {
            var row = rows.get(index)
                .link();
            try {
                switch (WirelessOperations.teleport(serverPlayer, binding.source(), row)) {
                    case STALE -> message("stale");
                    case UNAVAILABLE -> message("teleport_unavailable");
                    case DENIED -> message("denied");
                    case APPLIED -> {
                        return;
                    }
                    default -> {}
                }
            } catch (PhysicalClusterTracker.ScanLimitException limit) {
                message("stale");
            }
        } else if (actionType == HIGHLIGHT && index < rows.size()) {
            var row = rows.get(index)
                .link();
            if (row.target()
                .dimension() != player.dimension) {
                message(
                    "highlight_dimension",
                    row.target()
                        .dimension());
            } else {
                session.pendingHighlight = new NBTTagList();
                var targets = WirelessClusterManager.highlight(row)
                    .stream()
                    .filter(target -> target.dimension() == player.dimension)
                    .sorted(
                        java.util.Comparator
                            .comparingDouble(target -> player.getDistanceSq(target.x(), target.y(), target.z())))
                    .limit(512)
                    .toList();
                for (var target : targets) {
                    NBTTagCompound pos = new NBTTagCompound();
                    pos.setInteger("dimension", target.dimension());
                    pos.setInteger("x", target.x());
                    pos.setInteger("y", target.y());
                    pos.setInteger("z", target.z());
                    session.pendingHighlight.appendTag(pos);
                }
                message("highlight_started", targets.size());
            }
        } else if (action == PREVIOUS_PAGE) navigation.page--;
        else if (action == NEXT_PAGE) navigation.page++;
        else if (action == TOGGLE_AUTO) {
            WirelessAutoConnect.toggleAutomatic(player, card);
        } else if (action == TOGGLE_GROUPING && navigation.detailNode == null) {
            navigation.collapseTypes = WirelessCardBinding.toggleGrouping(card);
            navigation.resetGroups();
            WirelessCardInventory.sync(player, card);
        } else if (action == BACK) {
            navigation.back();
        } else if (actionType == OPEN_DETAILS && index < rows.size() && navigation.detailNode == null) {
            navigation.open(rows.get(index));
        } else if (actionType == TOGGLE_PAUSE && index < rows.size() && binding != null) {
            var row = rows.get(index)
                .link();
            try {
                WirelessOperations.setPaused(player, binding.source(), row);
            } catch (WirelessOperations.Denied denied) {
                message("denied");
            } catch (IllegalStateException stale) {
                message("stale");
            }
        } else if (action != REFRESH) return;
        session.update(true);
    }

    /** Per-open-container request limit, independent of action type or snapshot revision. */
    private boolean acceptRequest(long tick) {
        if (tick < nextRequestTick) return false;
        nextRequestTick = tick + 4;
        return true;
    }

    private void message(String key, Object... args) {
        session.player.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui." + key, args));
    }
}
