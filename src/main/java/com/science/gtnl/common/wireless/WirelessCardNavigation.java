package com.science.gtnl.common.wireless;

import com.science.gtnl.common.wireless.WirelessMachineGroups.Row;
import com.science.gtnl.common.wireless.WirelessMachineGroups.TypeKey;

import appeng.api.networking.IGridNode;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;

final class WirelessCardNavigation {

    int page, overviewPage, typeParentPage, machineParentPage;
    int pageSize = WirelessCardLayout.MIN_ROWS;
    IGridNode detailNode;
    MTEMultiBlockBase groupMachine;
    TypeKey groupType;
    boolean collapseTypes = true;
    WirelessViewFilter filter = WirelessViewFilter.parse("", "", 0);

    boolean resize(int rows) {
        if (!WirelessCardLayout.validRows(rows)) return false;
        page = page * pageSize / rows;
        overviewPage = overviewPage * pageSize / rows;
        typeParentPage = typeParentPage * pageSize / rows;
        machineParentPage = machineParentPage * pageSize / rows;
        pageSize = rows;
        return true;
    }

    void filter(String text, String dimension, int state) {
        filter = WirelessViewFilter.parse(text, dimension, state);
        detailNode = null;
        resetGroups();
    }

    void resetGroups() {
        groupMachine = null;
        groupType = null;
        page = 0;
    }

    boolean closeDetails() {
        if (detailNode == null) return false;
        detailNode = null;
        page = overviewPage;
        return true;
    }

    void back() {
        if (closeDetails()) return;
        if (groupMachine != null) {
            groupMachine = null;
            page = machineParentPage;
        } else if (groupType != null) {
            groupType = null;
            page = typeParentPage;
        }
    }

    void open(Row row) {
        if (row.category()) {
            typeParentPage = page;
            groupType = row.type();
        } else if (row.machine() != null) {
            machineParentPage = page;
            groupMachine = row.machine();
        } else {
            overviewPage = page;
            detailNode = row.link()
                .node();
        }
        page = 0;
    }
}
