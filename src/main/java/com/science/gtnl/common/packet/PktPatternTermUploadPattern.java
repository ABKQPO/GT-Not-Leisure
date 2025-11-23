package com.science.gtnl.common.packet;

import com.science.gtnl.utils.DireCraftingPatternDetails;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;

import com.glodblock.github.client.gui.container.ContainerFluidPatternTerminal;
import com.science.gtnl.common.machine.multiblock.AssemblerMatrix;
import com.science.gtnl.mixins.late.AppliedEnergistics.assembler.AccessorContainerPatternTerm;
import com.science.gtnl.mixins.late.AppliedEnergistics.assembler.AccessorFCContainerEncodeTerminal;
import com.science.gtnl.utils.Utils;

import appeng.api.AEApi;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IMachineSet;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.implementations.ContainerPatternTerm;
import appeng.container.slot.SlotRestrictedInput;
import appeng.items.misc.ItemEncodedPattern;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PktPatternTermUploadPattern implements IMessage, IMessageHandler<PktPatternTermUploadPattern, IMessage> {

    @Override
    public void fromBytes(final ByteBuf buf) {}

    @Override
    public void toBytes(final ByteBuf buf) {}

    @Override
    public IMessage onMessage(final PktPatternTermUploadPattern message, final MessageContext ctx) {
        work(ctx.getServerHandler().playerEntity);
        return null;
    }

    private void work(final EntityPlayer player) {
        final Container container = player.openContainer;
        final SlotRestrictedInput patternSlotOUT;
        final ItemStack patternStack;
        final IMachineSet channelNodes;
        if (container instanceof ContainerPatternTerm term) {
            patternSlotOUT = ((AccessorContainerPatternTerm) term).getPatternSlotOUT();
            patternStack = patternSlotOUT.getStack();
            if (patternStack == null) {
                return;
            }

            var part = term.getNetworkNode();
            if (part != null) {
                channelNodes = part.getGrid()
                    .getMachines(AssemblerMatrix.class);
            } else return;
        } else if (container instanceof ContainerFluidPatternTerminal term) {
            patternSlotOUT = ((AccessorFCContainerEncodeTerminal) term).getPatternSlotOUT();
            patternStack = patternSlotOUT.getStack();
            if (patternStack == null) {
                return;
            }

            var part = term.getNetworkNode();
            if (part != null) {
                channelNodes = part.getGrid()
                    .getMachines(AssemblerMatrix.class);
            } else return;
        } else return;

        final IAEItemStack out;
        if (patternStack.getItem() instanceof ItemEncodedPattern item) {
            var pattern = item.getPatternForItem(patternStack, player.worldObj);
            if (pattern.isCraftable() || pattern instanceof DireCraftingPatternDetails) {
                out = pattern.getCondensedOutputs()[0];
            } else return;
        } else return;

        for (final IGridNode channelNode : channelNodes) {
            var channel = (AssemblerMatrix) channelNode.getMachine();
            if (channel.getPossibleOutputs()
                .contains(out)) {
                player.addChatMessage(new ChatComponentTranslation("text.AssemblerMatrix.tooltip.0"));
                var stack = AEApi.instance()
                    .definitions()
                    .materials()
                    .blankPattern()
                    .maybeStack(patternStack.stackSize);
                if (stack.isPresent()) {
                    Utils.placeItemBackInInventory(player, stack.get());
                }
                patternSlotOUT.putStack(null);
                return;
            }
        }

        for (final IGridNode channelNode : channelNodes) {
            var channel = (AssemblerMatrix) channelNode.getMachine();
            if (channel.getInventory()
                .insertPattern(patternStack)) {
                patternSlotOUT.putStack(null);
                break;
            }
        }
    }
}
