package com.anordinarypeople.coordinatemanager.widgets.empty;

import com.anordinarypeople.coordinatemanager.data.Const;
import com.anordinarypeople.coordinatemanager.data.EmptyWorld;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class EmptyWorldPanelEntry extends ObjectSelectionList.Entry<EmptyWorldPanelEntry> {
  public final EmptyWorld data;
  private final int position;
  private final Minecraft client;

  public EmptyWorldPanelEntry(Minecraft client, EmptyWorld data, int position) {
    this.client = client;
    this.data = data;
    this.position = position;
  }

  private MutableComponent getTitle() {
    return Component.translatable("world_name.entry", position);
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
