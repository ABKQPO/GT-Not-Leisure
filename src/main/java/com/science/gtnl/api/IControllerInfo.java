package com.science.gtnl.api;

public interface IControllerInfo {

    int MACHINE_INFO_WINDOW_ID = 9;

    boolean supportsMachineInfo();

    default int getMachineInfoWindowID() {
        return MACHINE_INFO_WINDOW_ID;
    }
}
