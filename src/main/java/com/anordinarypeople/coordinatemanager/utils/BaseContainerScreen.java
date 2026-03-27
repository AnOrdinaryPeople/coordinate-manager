package com.anordinarypeople.coordinatemanager.utils;

import com.anordinarypeople.coordinatemanager.data.SelectableCoor;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BaseContainerScreen extends Screen {
  public final int padding = 5;
  public final int inputHeight = 20;
  public SelectableCoor currentData;
  protected final int footerHeight = 25;

  public BaseContainerScreen(String translatableKey) {
    super(Component.translatable(translatableKey));
  }

  public void onSelected(SelectableCoor data) {
    currentData = data;
  }
}
