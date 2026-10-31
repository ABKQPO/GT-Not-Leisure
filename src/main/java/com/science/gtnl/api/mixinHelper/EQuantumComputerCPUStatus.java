package com.science.gtnl.api.mixinHelper;

public interface EQuantumComputerCPUStatus {

    byte NORMAL_CPU = 0;
    byte VIRTUAL_CPU = 1;
    byte SPLIT_CPU = 2;

    byte ec$getCPUType();
}
