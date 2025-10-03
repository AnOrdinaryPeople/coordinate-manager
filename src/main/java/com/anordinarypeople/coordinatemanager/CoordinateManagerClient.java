package com.anordinarypeople.coordinatemanager;

import org.lwjgl.glfw.GLFW;

import com.anordinarypeople.coordinatemanager.cache.ModConfig;
import com.anordinarypeople.coordinatemanager.data.Const;
import com.anordinarypeople.coordinatemanager.screens.HistoryScreen;
import com.anordinarypeople.coordinatemanager.utils.Console;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class CoordinateManagerClient implements ClientModInitializer {
  private final Console logger = new Console("client-mod-init");
  private KeyBinding capture;
  private KeyBinding history;
  private CaptureHandler captureHandler;
  private int timeout = 0;

  @Override
  public void onInitializeClient() {
    captureHandler = new CaptureHandler();

    registerKeys();
    keyOnPressed();
  }

  private void registerKeys() {
    capture = KeyBindingHelper
        .registerKeyBinding(
            new KeyBinding("keybind.capture", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F9, KeyBinding.Category.MISC));
    history = KeyBindingHelper
        .registerKeyBinding(
            new KeyBinding("keybind.history", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F10, KeyBinding.Category.MISC));
  }

  private void keyOnPressed() {
    ClientTickEvents.END_CLIENT_TICK.register(client -> {
      while (capture.wasPressed()) {
        if (timeout == 0) {
          boolean hasCooldown = ModConfig.INSTANCE.captureCooldown >= Const.MIN_COOLDOWN;

          if (hasCooldown) {
            timeout = ModConfig.INSTANCE.captureCooldown;
            virtualUnlockReset();
          }

          captureHandler.store(client);
        }
      }

      while (history.wasPressed()) {
        client.setScreen(new HistoryScreen(null));
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
