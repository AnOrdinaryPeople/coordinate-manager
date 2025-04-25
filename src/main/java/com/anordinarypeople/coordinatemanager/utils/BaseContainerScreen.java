package com.anordinarypeople.coordinatemanager.utils;

import com.anordinarypeople.coordinatemanager.data.SelectableCoor;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class BaseContainerScreen extends Screen {
  public final int padding = 5;
  public final int inputHeight = 20;
  public SelectableCoor currentData;
  protected final int footerHeight = 25;

  public BaseContainerScreen(String translatableKey) {
    super(Text.translatable(translatableKey));
  }
}
