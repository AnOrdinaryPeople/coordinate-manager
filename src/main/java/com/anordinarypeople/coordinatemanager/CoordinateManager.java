package com.anordinarypeople.coordinatemanager;

import com.anordinarypeople.coordinatemanager.cache.Coordinate;
import com.anordinarypeople.coordinatemanager.cache.ModConfig;
import com.anordinarypeople.coordinatemanager.cache.WorldCache;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class CoordinateManager implements ModInitializer {
  private String getWorld(ClientPlayNetworkHandler handler, MinecraftClient client) {
    return client.isInSingleplayer()
        ? client.getServer().getSaveProperties().getLevelName()
        : handler.getConnection().getAddress().toString();
  }

  @Override
  public void onInitialize() {
    ModConfig.load();
    WorldCache.load();

    ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
      WorldCache.IS_SINGLEPLAYER = client.isInSingleplayer() ? 1 : 0;
      Coordinate.load(getWorld(handler, client));
    });

    ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> WorldCache.save(getWorld(handler, client)));
  }
}
