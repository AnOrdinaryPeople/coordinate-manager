package com.anordinarypeople.coordinatemanager.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;

public class TextField {
  private final Component empty = Component.empty();
  public Font textRenderer;
  public MutableComponent text;
  public int x;
  public int y;
  public int width;
  public int height;

  public TextField(Font textRenderer, int x, int y, int width, int height) {
    this.textRenderer = textRenderer;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  private StringWidget textWidget() {
    return new StringWidget(x, y, width, height, text, textRenderer);
  }

  public StringWidget label(String translationKey) {
    text = Component.translatable(translationKey);
    return textWidget();
  }

  public StringWidget label() {
    return textWidget();
  }

  public EditBox input(String value) {
    EditBox field = new EditBox(textRenderer, x, y, width, height, empty);

    if (!value.isBlank()) {
      field.setValue(value);
    }

    return field;
  }
}
