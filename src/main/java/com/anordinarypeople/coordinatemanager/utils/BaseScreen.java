package com.anordinarypeople.coordinatemanager.utils;

import com.anordinarypeople.coordinatemanager.widgets.Button;
import com.anordinarypeople.coordinatemanager.widgets.TextField;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class BaseScreen extends Screen {
  private final Screen parent;
  protected final int baseHeight = 20;
  protected int centerX;
  protected int buttonWidth;
  protected TextField textField;
  protected Button button;

  protected BaseScreen(Screen parent, String translationKey) {
    super(Text.translatable(translationKey));
    this.parent = parent;
  }

  protected int getWidthText(Text text) {
    return client.textRenderer.getWidth(text);
  }

  protected MutableText trim(Text text) {
    return TextTrim.trim(client.textRenderer, text, width);
  }

  protected void baseInit() {
    centerX = width / 2;
    textField = new TextField(client.textRenderer, 0, 0, 0, baseHeight);
    textField.y = height / 2 - 50;
  }

  protected void baseInitButton() {
    buttonWidth = centerX / 2;
    button = new Button(centerX - buttonWidth, textField.y + baseHeight, buttonWidth, baseHeight);
  }

  @Override
  public void close() {
    client.setScreen(parent);
  }
}
