package com.anordinarypeople.coordinatemanager;

import com.anordinarypeople.coordinatemanager.cache.Coordinate;
import com.anordinarypeople.coordinatemanager.cache.ModConfig;
import com.anordinarypeople.coordinatemanager.cache.WorldCache;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

public class CoordinateManager implements ModInitializer {
  private String getWorld(ClientPacketListener handler, Minecraft client) {
    return client.hasSingleplayerServer()
        ? client.getSingleplayerServer().getWorldData().getLevelName()
        : handler.getConnection().getRemoteAddress().toString();
  }

  @Override
  public void onInitialize() {
    ModConfig.load();
    WorldCache.load();

    ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
      WorldCache.IS_SINGLEPLAYER = client.hasSingleplayerServer() ? 1 : 0;
      Coordinate.load(getWorld(handler, client));
    });

    ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> WorldCache.save(getWorld(handler, client)));
  }
}
