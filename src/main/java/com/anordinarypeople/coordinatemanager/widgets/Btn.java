package com.anordinarypeople.coordinatemanager.widgets;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.Builder;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class Btn {
  public int x;
  public int y;
  public int width;
  public int height;

  public Btn(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  private Builder builder(String translationKey, OnPress onClick) {
    return Button.builder(Component.translatable(translationKey), onClick)
        .bounds(x, y, width, height);
  }

  public Button widget(String translationKey, OnPress onClick) {
    return builder(translationKey, onClick).build();
  }

  public Button widget(String translationKey, String translationKeyTooltip, OnPress onClick) {
    return builder(translationKey, onClick)
        .tooltip(Tooltip.create(Component.translatable(translationKeyTooltip)))
        .build();
  }
}
