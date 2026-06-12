package com.science.gtnl.utils.world.steam;

import com.science.gtnl.ScienceNotLeisure;
import gregtech.common.misc.spaceprojects.SpaceProjectManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class GlobalSteamWorldSavedData extends WorldSavedData {
   public static GlobalSteamWorldSavedData INSTANCE;
   public static final String DATA_NAME = "GregTech_WirelessSteamWorldSavedData";
   public static final String GLOBAL_STEAM_NBT_TAG = "GregTech_GlobalSteam_MapNBTTag";
   public static final String GLOBAL_STEAM_TEAM_NBT_TAG = "GregTech_GlobalSteamTeam_MapNBTTag";

   public static void loadInstance(World world) {
      SteamWirelessNetworkManager.GLOBAL_STEAM.clear();
      MapStorage storage = world.field_72988_C;
      INSTANCE = (GlobalSteamWorldSavedData)storage.func_75742_a(GlobalSteamWorldSavedData.class, "GregTech_WirelessSteamWorldSavedData");
      if (INSTANCE == null) {
         INSTANCE = new GlobalSteamWorldSavedData();
         storage.func_75745_a("GregTech_WirelessSteamWorldSavedData", INSTANCE);
      }

      INSTANCE.func_76185_a();
   }

   public GlobalSteamWorldSavedData() {
      super("GregTech_WirelessSteamWorldSavedData");
   }

   public GlobalSteamWorldSavedData(String name) {
      super(name);
   }

   public void func_76184_a(NBTTagCompound nbtTagCompound) {
      byte[] ba;
      ByteArrayInputStream byteArrayInputStream;
      ObjectInputStream objectInputStream;
      Object data;
      Map oldTeams;
      Iterator var7;
      Entry entry;
      try {
         ba = nbtTagCompound.func_74770_j("GregTech_GlobalSteam_MapNBTTag");
         if (ba.length == 0) {
            return;
         }

         byteArrayInputStream = new ByteArrayInputStream(ba);

         try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);

            try {
               data = objectInputStream.readObject();
               oldTeams = (Map)data;
               var7 = oldTeams.entrySet().iterator();

               while(var7.hasNext()) {
                  entry = (Entry)var7.next();

                  try {
                     SteamWirelessNetworkManager.GLOBAL_STEAM.put(UUID.fromString(entry.getKey().toString()), (BigInteger)entry.getValue());
                  } catch (RuntimeException var15) {
                     ScienceNotLeisure.LOG.warn("[GlobalSteamWorldSavedData] Skipping invalid UUID key in GlobalSteam: {}", new Object[]{entry.getKey()});
                  }
               }
            } catch (Throwable var19) {
               try {
                  objectInputStream.close();
               } catch (Throwable var14) {
                  var19.addSuppressed(var14);
               }

               throw var19;
            }

            objectInputStream.close();
         } catch (Throwable var20) {
            try {
               byteArrayInputStream.close();
            } catch (Throwable var13) {
               var20.addSuppressed(var13);
            }

            throw var20;
         }

         byteArrayInputStream.close();
      } catch (ClassNotFoundException | IOException var21) {
         ScienceNotLeisure.LOG.error("[GlobalSteamWorldSavedData] {} LOAD FAILED", new Object[]{"GregTech_GlobalSteam_MapNBTTag", var21});
      }

      try {
         if (!nbtTagCompound.func_74764_b("GregTech_GlobalSteamTeam_MapNBTTag")) {
            return;
         }

         ba = nbtTagCompound.func_74770_j("GregTech_GlobalSteamTeam_MapNBTTag");
         if (ba.length == 0) {
            return;
         }

         byteArrayInputStream = new ByteArrayInputStream(ba);

         try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);

            try {
               data = objectInputStream.readObject();
               oldTeams = (Map)data;
               var7 = oldTeams.entrySet().iterator();

               while(var7.hasNext()) {
                  entry = (Entry)var7.next();

                  try {
                     SpaceProjectManager.putInTeam(UUID.fromString((String)entry.getKey()), UUID.fromString((String)entry.getValue()));
                  } catch (RuntimeException var12) {
                     ScienceNotLeisure.LOG.warn("[GlobalSteamWorldSavedData] Skipping invalid UUID in team entry: {}", new Object[]{entry.getKey()});
                  }
               }
            } catch (Throwable var16) {
               try {
                  objectInputStream.close();
               } catch (Throwable var11) {
                  var16.addSuppressed(var11);
               }

               throw var16;
            }

            objectInputStream.close();
         } catch (Throwable var17) {
            try {
               byteArrayInputStream.close();
            } catch (Throwable var10) {
               var17.addSuppressed(var10);
            }

            throw var17;
         }

         byteArrayInputStream.close();
      } catch (ClassNotFoundException | IOException var18) {
         ScienceNotLeisure.LOG.error("[GlobalSteamWorldSavedData] {} LOAD FAILED", new Object[]{"GregTech_GlobalSteamTeam_MapNBTTag", var18});
      }

   }

   public void func_76187_b(NBTTagCompound nbtTagCompound) {
      try {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

         try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            try {
               objectOutputStream.writeObject(SteamWirelessNetworkManager.GLOBAL_STEAM);
               objectOutputStream.flush();
               nbtTagCompound.func_74773_a("GregTech_GlobalSteam_MapNBTTag", byteArrayOutputStream.toByteArray());
            } catch (Throwable var8) {
               try {
                  objectOutputStream.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }

               throw var8;
            }

            objectOutputStream.close();
         } catch (Throwable var9) {
            try {
               byteArrayOutputStream.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }

            throw var9;
         }

         byteArrayOutputStream.close();
      } catch (IOException var10) {
         ScienceNotLeisure.LOG.error("[GlobalSteamWorldSavedData] {} SAVE FAILED", new Object[]{"GregTech_GlobalSteam_MapNBTTag", var10});
      }

   }
}
