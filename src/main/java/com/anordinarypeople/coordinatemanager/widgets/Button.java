package com.anordinarypeople.coordinatemanager.widgets;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.Builder;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import net.minecraft.text.Text;

public class Button {
  public int x;
  public int y;
  public int width;
  public int height;

  public Button(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  private Builder builder(String translationKey, PressAction onClick) {
    return ButtonWidget.builder(Text.translatable(translationKey), onClick)
        .dimensions(x, y, width, height);
  }

  public ButtonWidget widget(String translationKey, PressAction onClick) {
    return builder(translationKey, onClick).build();
  }

  public ButtonWidget widget(String translationKey, String translationKeyTooltip, PressAction onClick) {
    return builder(translationKey, onClick)
        .tooltip(Tooltip.of(Text.translatable(translationKeyTooltip)))
        .build();
  }
}
