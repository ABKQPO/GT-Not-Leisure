package com.science.gtnl;

import static com.gtnltest.ContractAssertions.finish;

import com.science.gtnl.common.crafting.batch.BatchDispatchContracts;
import com.science.gtnl.common.crafting.quantum.ae2.QuantumCollectionContracts;
import com.science.gtnl.common.item.items.PortableFurnaceContracts;
import com.science.gtnl.common.machine.MachineStateContracts;
import com.science.gtnl.common.machine.hatch.HatchFluidDrainContracts;
import com.science.gtnl.common.machine.multiblock.AssemblerMatrixTasksContracts;
import com.science.gtnl.common.steam.GlobalSteamSaveContracts;

public final class MachineContractTest {

    public static void main(String[] args) throws Exception {
        GlobalSteamSaveContracts.run();
        AssemblerMatrixTasksContracts.run();
        HatchFluidDrainContracts.run();
        PortableFurnaceContracts.run();
        MachineStateContracts.run();
        BatchDispatchContracts.run();
        QuantumCollectionContracts.run();
        finish("Machines");
    }
}
