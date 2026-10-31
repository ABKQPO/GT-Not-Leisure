package com.science.gtnl.common.packet;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import com.gtnllib.network.ClientboundPacket;
import com.science.gtnl.client.quantum.ICraftingCycleHost;
import com.science.gtnl.client.quantum.RecursivePatternReport;
import com.science.gtnl.common.crafting.cycle.RecursivePatternFinder.Cycle;
import com.science.gtnl.common.crafting.cycle.RecursivePatternFinder.PatternLocation;

import appeng.api.storage.data.IAEStack;
import appeng.api.util.DimensionalCoord;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class RecursivePatternResultPacket extends ClientboundPacket {

    private static final int MAX_CYCLES = 32;
    private static final int MAX_ITEMS = 24;
    private static final int MAX_PATTERNS = 64;

    private IAEStack<?> request;
    private List<Cycle> cycles;
    private boolean manual;

    public RecursivePatternResultPacket() {}

    public RecursivePatternResultPacket(IAEStack<?> request, List<Cycle> cycles, boolean manual) {
        this.request = request;
        this.cycles = cycles;
        this.manual = manual;
    }

    @Override
    protected void write(ByteBuf buf) {
        var data = new NBTTagCompound();
        data.setTag("request", this.request.toNBTGeneric());
        data.setBoolean("manual", this.manual);
        var list = new NBTTagList();
        for (var cycle : this.cycles.subList(0, Math.min(MAX_CYCLES, this.cycles.size()))) {
            var tag = new NBTTagCompound();
            tag.setBoolean("net", cycle.netOutput());
            var items = new NBTTagList();
            for (var item : cycle.items()
                .subList(
                    0,
                    Math.min(
                        MAX_ITEMS,
                        cycle.items()
                            .size()))) {
                items.appendTag(item.toNBTGeneric());
            }
            tag.setTag("items", items);
            var patterns = new NBTTagList();
            for (var pattern : cycle.patterns()
                .subList(
                    0,
                    Math.min(
                        MAX_PATTERNS,
                        cycle.patterns()
                            .size()))) {
                var patternTag = new NBTTagCompound();
                if (pattern.icon() != null) {
                    patternTag.setTag(
                        "icon",
                        pattern.icon()
                            .writeToNBT(new NBTTagCompound()));
                }
                if (pattern.pattern() != null) {
                    patternTag.setTag(
                        "pattern",
                        pattern.pattern()
                            .writeToNBT(new NBTTagCompound()));
                }
                patternTag.setTag(
                    "output",
                    pattern.output()
                        .toNBTGeneric());
                var machines = new NBTTagList();
                for (var machine : pattern.machines()) {
                    var coord = new NBTTagCompound();
                    machine.writeToNBT(coord);
                    machines.appendTag(coord);
                }
                patternTag.setTag("machines", machines);
                patterns.appendTag(patternTag);
            }
            tag.setTag("patterns", patterns);
            list.appendTag(tag);
        }
        data.setTag("cycles", list);
        ByteBufUtils.writeTag(buf, data);
    }

    @Override
    protected void read(ByteBuf buf) {
        var data = ByteBufUtils.readTag(buf);
        this.request = IAEStack.fromNBTGeneric(data.getCompoundTag("request"));
        this.manual = data.getBoolean("manual");
        this.cycles = new ObjectArrayList<>();
        var list = data.getTagList("cycles", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            var tag = list.getCompoundTagAt(i);
            var items = new ObjectArrayList<IAEStack<?>>();
            var itemTags = tag.getTagList("items", Constants.NBT.TAG_COMPOUND);
            for (int j = 0; j < itemTags.tagCount(); j++) {
                var item = IAEStack.fromNBTGeneric(itemTags.getCompoundTagAt(j));
                if (item != null) items.add(item);
            }
            var patterns = new ObjectArrayList<PatternLocation>();
            var patternTags = tag.getTagList("patterns", Constants.NBT.TAG_COMPOUND);
            for (int j = 0; j < patternTags.tagCount(); j++) {
                var patternTag = patternTags.getCompoundTagAt(j);
                var output = IAEStack.fromNBTGeneric(patternTag.getCompoundTag("output"));
                var machines = new ObjectArrayList<DimensionalCoord>();
                var machineTags = patternTag.getTagList("machines", Constants.NBT.TAG_COMPOUND);
                for (int k = 0; k < machineTags.tagCount(); k++) {
                    machines.add(DimensionalCoord.readFromNBT(machineTags.getCompoundTagAt(k)));
                }
                var encoded = patternTag.hasKey("pattern")
                    ? ItemStack.loadItemStackFromNBT(patternTag.getCompoundTag("pattern"))
                    : null;
                var icon = patternTag.hasKey("icon") ? ItemStack.loadItemStackFromNBT(patternTag.getCompoundTag("icon"))
                    : null;
                if (output != null) patterns.add(new PatternLocation(encoded, output, machines, icon));
            }
            this.cycles.add(new Cycle(tag.getBoolean("net"), items, patterns));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        minecraft.func_152344_a(() -> {
            if (minecraft.currentScreen instanceof ICraftingCycleHost host) {
                host.gtnl$cyclesFound(this.request, this.cycles, this.manual);
            } else if (this.manual) {
                RecursivePatternReport.show(minecraft, this.request, this.cycles);
            }
        });
    }
}
