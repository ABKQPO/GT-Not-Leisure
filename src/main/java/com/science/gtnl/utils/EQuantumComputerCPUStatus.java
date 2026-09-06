package com.science.gtnl.utils;

public interface EQuantumComputerCPUStatus {

    byte NORMAL_CPU = 0;
    byte VIRTUAL_CPU = 1;
    byte SPLIT_CPU = 2;

    byte ec$getCPUType();
}
