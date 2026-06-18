package com.anordinarypeople.coordinatemanager.utils;

import com.anordinarypeople.coordinatemanager.widgets.Btn;
import com.anordinarypeople.coordinatemanager.widgets.TextField;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class BaseScreen extends Screen {
  private final Screen parent;
  protected final int baseHeight = 20;
  protected int centerX;
  protected int buttonWidth;
  protected TextField textField;
  protected Btn button;

  protected BaseScreen(Screen parent, String translationKey) {
    super(Component.translatable(translationKey));
    this.parent = parent;
  }

  protected int getWidthText(Component text) {
    return minecraft.font.width(text);
  }

  protected MutableComponent trim(Component text) {
    return TextTrim.trim(minecraft.font, text, width);
  }

  protected void baseInit() {
    centerX = width / 2;
    textField = new TextField(minecraft.font, 0, 0, 0, baseHeight);
    textField.y = height / 2 - 50;
  }

  protected void baseInitButton() {
    buttonWidth = centerX / 2;
    button = new Btn(centerX - buttonWidth, textField.y + baseHeight, buttonWidth, baseHeight);
  }

  @Override
  public void onClose() {
    minecraft.setScreenAndShow(parent);
  }
}
