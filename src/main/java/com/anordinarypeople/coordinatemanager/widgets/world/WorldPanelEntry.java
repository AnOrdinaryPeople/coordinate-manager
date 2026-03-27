package com.anordinarypeople.coordinatemanager.widgets.world;

import java.util.regex.Pattern;

import com.anordinarypeople.coordinatemanager.data.Const;
import com.anordinarypeople.coordinatemanager.data.WorldData;
import com.anordinarypeople.coordinatemanager.utils.TextTrim;
import com.anordinarypeople.coordinatemanager.utils.WorldKeyword;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class WorldPanelEntry extends ObjectSelectionList.Entry<WorldPanelEntry> {
  public final WorldData data;
  private final Minecraft client;
  private final Pattern pattern;
  private GuiGraphicsExtractor context;

  public WorldPanelEntry(Minecraft client, WorldData data, Pattern pattern) {
    this.client = client;
    this.data = data;
    this.pattern = pattern;
  }

  private MutableComponent trim(Component text) {
    return TextTrim.trim(client.font, text, getContentWidth());
  }

  private MutableComponent getTitle() {
    return trim(Component.literal(data.worldName));
  }

  private void renderTitle(int x, int y) {
    context.text(
        client.font,
        getTitle(),
        x + 3,
        y + 1,
        Const.WHITE,
        true);
  }

  private void renderDescription(int x, int y) {
    boolean hasPattern = pattern != null;
    int matched = hasPattern ? WorldKeyword.countMatched(data, pattern) : 0;
    MutableComponent text = Component.translatable(
        hasPattern ? "management.description_matched" : "management.description",
        hasPattern ? matched : data.keywords.size());

    if (!data.isSingleplayer) {
      text.append(Component.literal(" "))
          .append(Component.translatable("management.multiplayer"));
    }

    context.text(
        client.font,
        trim(text),
        x + 3,
        y + client.font.lineHeight + 3,
        Const.GRAY,
        true);
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
    int x = getX();
    int y = getY();
    context = drawContext;

    renderTitle(x, y);
    renderDescription(x, y);
  }
}
