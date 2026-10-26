package com.science.gtnl.common.machine.purification;

import java.math.BigInteger;
import java.util.UUID;

import com.gtnllib.utils.MathUtils;

public class PurificationUnitState {

    public long maxParallelLong = 1;
    public long effectiveParallelLong = 1;
    public BigInteger costingEU = BigInteger.ZERO;
    public String costingEUText = MathUtils.ZERO_STRING;
    public boolean wirelessMode;
    public UUID ownerUUID;
}
