package com.anordinarypeople.coordinatemanager.widgets;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class TextField {
  private final Text empty = Text.empty();
  public TextRenderer textRenderer;
  public MutableText text;
  public int x;
  public int y;
  public int width;
  public int height;

  public TextField(TextRenderer textRenderer, int x, int y, int width, int height) {
    this.textRenderer = textRenderer;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  private TextWidget textWidget() {
    return new TextWidget(x, y, width, height, text, textRenderer);
  }

  public TextWidget label(String translationKey) {
    text = Text.translatable(translationKey);
    return textWidget();
  }

  public TextWidget label() {
    return textWidget();
  }

  public TextFieldWidget input(String value) {
    TextFieldWidget field = new TextFieldWidget(textRenderer, x, y, width, height, empty);

    if (!value.isBlank()) {
      field.setText(value);
    }

    return field;
  }
}
