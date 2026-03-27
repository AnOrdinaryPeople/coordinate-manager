package com.anordinarypeople.coordinatemanager.widgets.world;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

import com.anordinarypeople.coordinatemanager.cache.WorldCache;
import com.anordinarypeople.coordinatemanager.data.Const;
import com.anordinarypeople.coordinatemanager.data.WorldData;
import com.anordinarypeople.coordinatemanager.screens.ManageScreen;
import com.anordinarypeople.coordinatemanager.utils.RenderHelper;
import com.anordinarypeople.coordinatemanager.utils.WorldKeyword;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

public class WorldPanel extends ObjectSelectionList<WorldPanelEntry> implements AutoCloseable {
  private final ManageScreen parent;
  private MutableComponent description;
  private Consumer<String> onSelected;
  private GuiGraphicsExtractor context;

  public WorldPanel(
      Minecraft client,
      int width,
      int height,
      int x,
      int y,
      int itemHeight,
      Consumer<String> onSelected,
      ManageScreen parent) {
    super(client, width, height, y, itemHeight);

    this.setX(x);
    this.onSelected = onSelected;
    this.parent = parent;
  }

  @Nullable
  private WorldPanelEntry getEntry(int index) {
    return children().size() > index ? children().get(index) : null;
  }

  private void drawItem(int entryCount, int index, int entryTop, int mouseX, int mouseY, float delta) {
    final int entryHeight = defaultEntryHeight - 5;
    final int rowWidth = getRowWidth();
    WorldPanelEntry entry = getEntry(index);
    WorldPanelEntry selected = getSelected();

    if (selected != null && selected.data.worldName.equals(entry.data.worldName)) {
      RenderHelper.drawSelected(context, getRowLeft(), rowWidth, getRowLeft() - 2, entryTop, entryHeight);
    }

    entry.extractContent(
        context,
        mouseX,
        mouseY,
        isMouseOver(mouseX, mouseY)
            && Objects.equals(getEntryAtPos(entryCount, mouseX, mouseY), entry),
        delta);
  }

  private WorldPanelEntry getEntryAtPos(int entryCount, double x, double y) {
    final int entryY = Mth.floor(y - ((double) getY()) + (int) scrollAmount() - 4);
    final int index = entryY / defaultEntryHeight;
    final int rowLeft = getRowLeft();

    return x < (double) scrollBarX()
        && x >= (double) rowLeft
        && x <= ((double) rowLeft + getRowRight())
        && index >= 0
        && entryY >= 0
        && index < entryCount
            ? children().get(index)
            : null;
  }

  private void scrollToTop() {
    final int max = Math.max(0, contentHeight() - getBottom() - getY() - 4);

    if (scrollAmount() > max) {
      setScrollAmount(max);
    }
  }

  public void filter(String keyword) {
    if (WorldCache.INSTANCE == null || WorldCache.INSTANCE.size() == 0) {
      description = Component.translatable("container.description");
      return;
    }
    String lowered = keyword.toLowerCase();
    Pattern pattern = Pattern.compile(lowered);
    boolean clearDescription = false;

    clearEntries();

    for (WorldData data : WorldCache.INSTANCE) {
      if (data.keywords.size() == 0) {
        continue;
      }

      if (lowered.isBlank() || ((data.worldName != null &&
          pattern.matcher(data.worldName.toLowerCase()).find()) ||
          WorldKeyword.countMatched(data, pattern) > 0)) {
        clearDescription = true;
        addEntry(new WorldPanelEntry(minecraft, data, lowered.isBlank() ? null : pattern));
      }
    }

    description = clearDescription ? null : Component.translatable("container.description_not_found");
    scrollToTop();
  }

  @Override
  public void close() {
  }

  @Override
  public void setSelected(WorldPanelEntry entry) {
    super.setSelected(entry);
    onSelected.accept(entry.data.worldName);
  }

  @Override
  public int getRowWidth() {
    return width
        - (Math.max(0, contentHeight() - (getBottom() - getY() - 4)) > 0 ? 18 : 12);
  }

  @Override
  public int getRowLeft() {
    return getX() + 6;
  }

  @Override
  protected void extractListItems(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float delta) {
    if (description != null) {
      drawContext.text(
          minecraft.font,
          description,
          parent.padding * 2,
          parent.inputHeight + parent.padding * 3 + 2,
          Const.WHITE,
          true);
      return;
    }

    final int entryCount = getItemCount();
    context = drawContext;

    for (int i = 0; i < entryCount; i++) {
      int entryTop = getRowTop(i) + 2;
      int entryBottom = getRowTop(i) + defaultEntryHeight + 2;

      if (entryBottom >= getY() && entryTop <= getBottom()) {
        drawItem(entryCount, i, entryTop, mouseX, mouseY, delta);
      }
    }
  }
}
