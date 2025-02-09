package com.science.gtnl.loader;

import com.science.gtnl.common.block.BlockRegister;
import com.science.gtnl.common.item.ItemRegister;
import com.science.gtnl.common.item.items.MilledOre;
import com.science.gtnl.common.materials.MaterialPool;

import bartworks.API.WerkstoffAdderRegistry;

/**
 * New Material Pool
 * Used Bartworks Werkstoff system
 *
 */
public class MaterialLoader {

    public static void load() {

        ItemRegister.registry();
        ItemRegister.registryOreDictionary();
        BlockRegister.registry();
        BlockRegister.registryOreDictionary();

        WerkstoffAdderRegistry.addWerkstoffAdder(new MaterialPool());

        new MilledOre();
    }
}
