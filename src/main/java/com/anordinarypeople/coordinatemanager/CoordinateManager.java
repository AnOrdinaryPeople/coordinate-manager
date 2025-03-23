package com.anordinarypeople.coordinatemanager;

import com.anordinarypeople.coordinatemanager.cache.Coordinate;
import com.anordinarypeople.coordinatemanager.cache.ModConfig;
import com.anordinarypeople.coordinatemanager.cache.WorldCache;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;

public class CoordinateManager implements ModInitializer {
  private String getWorld(MinecraftClient client, String address) {
    return client.isInSingleplayer()
        ? client.getServer().getSaveProperties().getLevelName()
        : address;
  }

  @Override
  public void onInitialize() {
    ModConfig.load();
    WorldCache.load();

    ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
      WorldCache.IS_SINGLEPLAYER = client.isInSingleplayer() ? 1 : 0;
      Coordinate.load(getWorld(client, handler.getConnection().getAddress().toString()));
    });

    ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> WorldCache.save(getWorld(client, null)));
  }
}
