package com.anordinarypeople.coordinatemanager;

import com.anordinarypeople.coordinatemanager.cache.ModConfig;
import com.anordinarypeople.coordinatemanager.data.Const;
import com.anordinarypeople.coordinatemanager.screens.HistoryScreen;
import com.anordinarypeople.coordinatemanager.utils.Console;
import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

public class CoordinateManagerClient implements ClientModInitializer {
  private final Console logger = new Console("client-mod-init");
  private KeyMapping capture;
  private KeyMapping history;
  private CaptureHandler captureHandler;
  private int timeout = 0;

  @Override
  public void onInitializeClient() {
    captureHandler = new CaptureHandler();

    registerKeys();
    keyOnPressed();
  }

  private void registerKeys() {
    capture = KeyMappingHelper
        .registerKeyMapping(
            new KeyMapping("keybind.capture", InputConstants.Type.KEYSYM, InputConstants.KEY_F9, KeyMapping.Category.MISC));
    history = KeyMappingHelper
        .registerKeyMapping(
            new KeyMapping("keybind.history", InputConstants.Type.KEYSYM, InputConstants.KEY_F10, KeyMapping.Category.MISC));
  }

  private void keyOnPressed() {
    ClientTickEvents.END_CLIENT_TICK.register(client -> {
      while (capture.consumeClick()) {
        if (timeout == 0) {
          boolean hasCooldown = ModConfig.INSTANCE.captureCooldown >= Const.MIN_COOLDOWN;

          if (hasCooldown) {
            timeout = ModConfig.INSTANCE.captureCooldown;
            virtualUnlockReset();
          }

          captureHandler.store(client);
        }
      }

      while (history.consumeClick()) {
        client.setScreenAndShow(new HistoryScreen(null));
      }
    });
  }

  private void virtualUnlockReset() {
    Thread.ofVirtual().start(() -> {
      try {
        Thread.sleep(timeout);
      } catch (InterruptedException e) {
        logger.error("Unlock reset operation was interrupted", e);
      } finally {
        timeout = 0;
      }
    });
  }
}
