package com.anordinarypeople.coordinatemanager.enums;

import net.minecraft.text.Text;

public enum CaptureMode {
  INSTANT("config.capture_mode.instant"),
  POP_UP("config.capture_mode.pop_up");

  private final String key;

  CaptureMode(String translationKey) {
    key = translationKey;
  }

  @Override
  public String toString() {
    return Text.translatable(key).getString();
  }
}
