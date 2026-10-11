package com.science.gtnl.common.entity;

import net.minecraftforge.event.entity.EntityEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class MajoBroomUpdateHandler {

    @SubscribeEvent
    public void onCanUpdate(EntityEvent.CanUpdate event) {
        if (event.entity instanceof EntityMajoBroom broom && broom.riddenByEntity != null) event.canUpdate = true;
    }
}
