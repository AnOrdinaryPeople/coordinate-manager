package com.anordinarypeople.coordinatemanager.widgets.world;

import java.util.regex.Pattern;

import com.anordinarypeople.coordinatemanager.data.Const;
import com.anordinarypeople.coordinatemanager.data.WorldData;
import com.anordinarypeople.coordinatemanager.utils.TextTrim;
import com.anordinarypeople.coordinatemanager.utils.WorldKeyword;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class WorldPanelEntry extends AlwaysSelectedEntryListWidget.Entry<WorldPanelEntry> {
  public final WorldData data;
  private final MinecraftClient client;
  private final Pattern pattern;
  private DrawContext context;
  private int maxWidth;

  public WorldPanelEntry(MinecraftClient client, WorldData data, Pattern pattern) {
    this.client = client;
    this.data = data;
    this.pattern = pattern;
  }

  private MutableText trim(Text text) {
    return TextTrim.trim(client.textRenderer, text, maxWidth);
  }

  private MutableText getTitle() {
    return trim(Text.literal(data.worldName));
  }

  private void renderTitle(int x, int y) {
    context.drawText(
        client.textRenderer,
        getTitle(),
        x + 3,
        y + 1,
        Const.WHITE,
        true);
  }

  private void renderDescription(int x, int y) {
    boolean hasPattern = pattern != null;
    int matched = hasPattern ? WorldKeyword.countMatched(data, pattern) : 0;
    MutableText text = Text.translatable(
        hasPattern ? "management.description_matched" : "management.description",
        hasPattern ? matched : data.keywords.size());

    if (!data.isSingleplayer) {
      text.append(Text.literal(" "))
          .append(Text.translatable("management.multiplayer"));
    }

    context.drawText(
        client.textRenderer,
        trim(text),
        x + 3,
        y + client.textRenderer.fontHeight + 3,
        Const.GRAY,
        true);
  }

  @Override
  public Text getNarration() {
    return getTitle();
  }

  @Override
  public void render(
      DrawContext drawContext,
      int index,
      int y,
      int x,
      int rowWidth,
      int rowHeight,
      int mouseX,
      int mouseY,
      boolean hovered,
      float delta) {
    maxWidth = rowWidth - 3;
    context = drawContext;

    renderTitle(x, y);
    renderDescription(x, y);
  }
}
