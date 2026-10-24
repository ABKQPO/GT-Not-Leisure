/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2. If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package com.science.gtnl.common.crafting.quantum.crafting;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;
import com.science.gtnl.common.crafting.quantum.core.localization.PlayerMessages;

public class CraftBranchFailure extends Exception {

    private final AEKey what;
    @Nullable
    private final PlayerMessages messageKey;
    @Nullable
    private final NoNetOutputCycle cycle;

    public CraftBranchFailure(AEKey what, long howMany) {
        super("Failed: " + what + " x " + howMany);
        this.what = what;
        this.messageKey = null;
        this.cycle = null;
    }

    public CraftBranchFailure(AEKey what, long howMany, PlayerMessages messageKey) {
        this(what, howMany, messageKey, null);
    }

    public CraftBranchFailure(AEKey what, long howMany, PlayerMessages messageKey, @Nullable NoNetOutputCycle cycle) {
        super("Failed: " + what + " x " + howMany + " (" + messageKey.getTranslationKey() + ")");
        this.what = what;
        this.messageKey = messageKey;
        this.cycle = cycle;
    }

    @Nullable
    public NoNetOutputCycle getCycle() {
        return this.cycle;
    }

    public AEKey getWhat() {
        return this.what;
    }

    public PlayerMessages getLocalizedMessageKey() {
        return this.messageKey != null ? this.messageKey : PlayerMessages.CraftingNoPlan;
    }

    public boolean hasExplicitMessageKey() {
        return this.messageKey != null;
    }

}
