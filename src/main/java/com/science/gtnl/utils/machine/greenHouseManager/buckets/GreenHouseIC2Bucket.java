package com.science.gtnl.utils.machine.greenHouseManager.buckets;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.api.IGreenHouse;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseBucket;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseBucketFactory;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseDropTable;

import gregtech.api.enums.ItemList;
import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;
import ic2.core.Ic2Items;
import ic2.core.crop.TileEntityCrop;

public class GreenHouseIC2Bucket extends GreenHouseBucket {

    public final static GreenHouseBucketFactory factory = new Factory();
    public static final String NBT_IDENTIFIER = "IC2";
    public static final int REVISION_NUMBER = 0;

    public static class Factory implements GreenHouseBucketFactory {

        @Override
        public String getNBTIdentifier() {
            return NBT_IDENTIFIER;
        }

        @Override
        public GreenHouseBucket tryCreateBucket(IGreenHouse greenhouse, ItemStack input) {
            if (!ItemList.IC2_Crop_Seeds.isStackEqual(input, true, true)) return null;
            if (!input.hasTagCompound()) return null;

            CropCard cc = Crops.instance.getCropCard(input);
            if (cc == null) return null;
            return new GreenHouseIC2Bucket(greenhouse, input);
        }

        @Override
        public GreenHouseBucket restore(NBTTagCompound nbt) {
            return new GreenHouseIC2Bucket(nbt);
        }
    }

    public double growthTime = 0;
    public GreenHouseDropTable drops = new GreenHouseDropTable();
    public boolean isValid = false;

    public GreenHouseIC2Bucket(IGreenHouse greenhouse, ItemStack seed) {
        super(seed, 1, null);
        this.recalculateDrops(greenhouse);
    }

    public GreenHouseIC2Bucket(NBTTagCompound nbt) {
        super(nbt);
        if (!nbt.hasKey("invalid")) {
            this.drops = new GreenHouseDropTable(nbt, "drops");
            this.growthTime = nbt.getDouble("growthTime");
            this.isValid = nbt.getInteger("version") == REVISION_NUMBER && this.growthTime > 0 && !this.drops.isEmpty();
        }
    }

    @Override
    public NBTTagCompound save() {
        NBTTagCompound nbt = super.save();
        if (this.isValid) {
            nbt.setTag("drops", this.drops.save());
            nbt.setDouble("growthTime", this.growthTime);
        } else {
            nbt.setBoolean("invalid", true);
        }
        nbt.setInteger("version", REVISION_NUMBER);
        return nbt;
    }

    @Override
    public String getNBTIdentifier() {
        return NBT_IDENTIFIER;
    }

    @Override
    public void addProgress(double multiplier, GreenHouseDropTable tracker) {
        if (!this.isValid()) return;
        double growthPercent = multiplier / this.growthTime;
        if (this.drops != null) {
            this.drops.addTo(tracker, this.seedCount * growthPercent);
        }
    }

    @Override
    public boolean revalidate(IGreenHouse greenhouse) {
        this.recalculateDrops(greenhouse);
        return this.isValid();
    }

    @Override
    public boolean isValid() {
        return super.isValid() && this.isValid;
    }

    public void recalculateDrops(IGreenHouse greenhouse) {
        this.isValid = false;
        int[] xyz = new int[] { greenhouse.getBaseMetaTileEntity()
            .getXCoord(),
            greenhouse.getBaseMetaTileEntity()
                .getYCoord(),
            greenhouse.getBaseMetaTileEntity()
                .getZCoord() };

        try {
            FakeTileEntityCrop crop = new FakeTileEntityCrop(this, greenhouse, xyz);
            if (!crop.isValid) return;

            CropCard cc = crop.getCrop();
            crop.setSize((byte) cc.maxSize());

            GreenHouseDropTable drops = new GreenHouseDropTable();
            for (int i = 0; i < NUMBER_OF_DROPS_TO_SIMULATE; i++) {
                ItemStack drop = cc.getGain(crop);
                if (drop == null || drop.stackSize <= 0) continue;
                drops.addDrop(drop, drop.stackSize / (double) NUMBER_OF_DROPS_TO_SIMULATE);
            }

            if (drops.isEmpty()) return;

            this.growthTime = 1.0;
            this.drops = drops;
            this.isValid = true;

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static class FakeTileEntityCrop extends TileEntityCrop {

        public boolean isValid;

        public FakeTileEntityCrop(GreenHouseIC2Bucket bucket, IGreenHouse greenhouse, int[] xyz) {
            super();
            this.isValid = false;
            this.ticker = 1;

            CropCard cc = Crops.instance.getCropCard(bucket.seed);
            this.setCrop(cc);
            this.setGrowth((byte) 31);
            this.setGain((byte) 31);
            this.setResistance((byte) 0);
            this.setWorldObj(
                greenhouse.getBaseMetaTileEntity()
                    .getWorld());

            this.xCoord = xyz[0];
            this.yCoord = xyz[1];
            this.zCoord = xyz[2];
            this.blockType = Block.getBlockFromItem(Ic2Items.crop.getItem());
            this.blockMetadata = 0;

            this.isValid = true;
        }

        @Override
        public int getLightLevel() {
            return 15;
        }

        @Override
        public byte getHumidity() {
            return 100;
        }

        @Override
        public byte updateHumidity() {
            return 100;
        }

        @Override
        public byte getNutrients() {
            return 100;
        }

        @Override
        public byte updateNutrients() {
            return 100;
        }

        @Override
        public byte getAirQuality() {
            return 100;
        }

        @Override
        public byte updateAirQuality() {
            return 100;
        }

        @Override
        public boolean isBlockBelow(Block reqBlock) {
            return true;
        }

        @Override
        public boolean isBlockBelow(String oreDictionaryName) {
            return true;
        }
    }
}
