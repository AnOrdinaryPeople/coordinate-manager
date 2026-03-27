package com.anordinarypeople.coordinatemanager.widgets.chooseworld;

import com.anordinarypeople.coordinatemanager.data.Const;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ChooseWorldPanelEntry extends ObjectSelectionList.Entry<ChooseWorldPanelEntry> {
  public final String data;
  private final Minecraft client;

  public ChooseWorldPanelEntry(Minecraft client, String data) {
    this.client = client;
    this.data = data;
  }

  private MutableComponent getTitle() {
    return Component.literal(data);
  }

  @Override
  public Component getNarration() {
    return getTitle();
  }

  @Override
  public void extractContent(
      GuiGraphicsExtractor drawContext,
      int mouseX,
      int mouseY,
      boolean hovered,
      float delta) {
    drawContext.text(client.font, getTitle(), getX() + 3, getY() + 1, Const.WHITE, true);
  }
}
