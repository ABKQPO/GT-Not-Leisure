package com.science.gtnl.common.item.items.bauble;

import lombok.Getter;

@Getter
public enum DraconicArmorProjectionType {

    WYVERN("wyvern"),
    DRACONIC("draconic");

    private final String id;

    DraconicArmorProjectionType(String id) {
        this.id = id;
    }

}
