package com.science.gtnl.common.machine.monitor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.cleanroommc.modularui.utils.item.INBTSerializable;

public class EnergyMonitorSnapshot implements INBTSerializable<NBTTagCompound> {

    private List<EnergyMonitorRowSnapshot> rows;
    private BigInteger wiredStored;
    private BigInteger wiredCapacity;
    private BigInteger wirelessStored;

    public EnergyMonitorSnapshot(List<EnergyMonitorRowSnapshot> rows, BigInteger wiredStored, BigInteger wiredCapacity,
        BigInteger wirelessStored) {
        this.rows = rows == null ? Collections.emptyList()
            : rows.stream()
                .map(EnergyMonitorRowSnapshot::copy)
                .collect(Collectors.toList());
        this.wiredStored = wiredStored == null ? BigInteger.ZERO : wiredStored;
        this.wiredCapacity = wiredCapacity == null ? BigInteger.ZERO : wiredCapacity;
        this.wirelessStored = wirelessStored == null ? BigInteger.ZERO : wirelessStored;
    }

    public static EnergyMonitorSnapshot empty() {
        return new EnergyMonitorSnapshot(Collections.emptyList(), BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO);
    }

    public EnergyMonitorSnapshot copy() {
        return new EnergyMonitorSnapshot(rows, wiredStored, wiredCapacity, wirelessStored);
    }

    public boolean sameAs(EnergyMonitorSnapshot other) {
        if (other == null) {
            return false;
        }
        if (!wiredStored.equals(other.wiredStored) || !wiredCapacity.equals(other.wiredCapacity)
            || !wirelessStored.equals(other.wirelessStored)) {
            return false;
        }
        if (rows.size() != other.rows.size()) {
            return false;
        }
        for (int index = 0; index < rows.size(); index++) {
            if (!rows.get(index)
                .sameAs(other.rows.get(index))) {
                return false;
            }
        }
        return true;
    }

    public List<EnergyMonitorRowSnapshot> getRows() {
        return rows;
    }

    public void setRows(List<EnergyMonitorRowSnapshot> rows) {
        this.rows = rows == null ? Collections.emptyList()
            : rows.stream()
                .map(EnergyMonitorRowSnapshot::copy)
                .collect(Collectors.toList());
    }

    public BigInteger getWiredStored() {
        return wiredStored;
    }

    public void setWiredStored(BigInteger wiredStored) {
        this.wiredStored = wiredStored == null ? BigInteger.ZERO : wiredStored;
    }

    public BigInteger getWiredCapacity() {
        return wiredCapacity;
    }

    public void setWiredCapacity(BigInteger wiredCapacity) {
        this.wiredCapacity = wiredCapacity == null ? BigInteger.ZERO : wiredCapacity;
    }

    public BigInteger getWirelessStored() {
        return wirelessStored;
    }

    public void setWirelessStored(BigInteger wirelessStored) {
        this.wirelessStored = wirelessStored == null ? BigInteger.ZERO : wirelessStored;
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList rowList = new NBTTagList();
        for (EnergyMonitorRowSnapshot row : rows) {
            rowList.appendTag(row.serializeNBT());
        }
        tag.setTag("rows", rowList);
        tag.setString("wiredStored", wiredStored.toString());
        tag.setString("wiredCapacity", wiredCapacity.toString());
        tag.setString("wirelessStored", wirelessStored.toString());
        return tag;
    }

    public void deserializeNBT(NBTTagCompound tag) {
        if (tag == null) {
            setRows(Collections.emptyList());
            setWiredStored(BigInteger.ZERO);
            setWiredCapacity(BigInteger.ZERO);
            setWirelessStored(BigInteger.ZERO);
            return;
        }
        NBTTagList rowList = tag.getTagList("rows", 10);
        List<EnergyMonitorRowSnapshot> deserializedRows = new ArrayList<>(rowList.tagCount());
        for (int index = 0; index < rowList.tagCount(); index++) {
            deserializedRows.add(EnergyMonitorRowSnapshot.fromNBT(rowList.getCompoundTagAt(index)));
        }
        setRows(deserializedRows);
        setWiredStored(parseBigInteger(tag.getString("wiredStored")));
        setWiredCapacity(parseBigInteger(tag.getString("wiredCapacity")));
        setWirelessStored(parseBigInteger(tag.getString("wirelessStored")));
    }

    public static EnergyMonitorSnapshot fromNBT(NBTTagCompound tag) {
        EnergyMonitorSnapshot snapshot = EnergyMonitorSnapshot.empty();
        snapshot.deserializeNBT(tag == null ? new NBTTagCompound() : tag);
        return snapshot;
    }

    private static BigInteger parseBigInteger(String value) {
        if (value == null || value.isEmpty()) {
            return BigInteger.ZERO;
        }
        try {
            return new BigInteger(value);
        } catch (NumberFormatException ignored) {
            return BigInteger.ZERO;
        }
    }
}
