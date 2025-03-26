package com.anordinarypeople.coordinatemanager.widgets.world;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.joml.Matrix4f;

import com.anordinarypeople.coordinatemanager.cache.WorldCache;
import com.anordinarypeople.coordinatemanager.data.Const;
import com.anordinarypeople.coordinatemanager.data.WorldData;
import com.anordinarypeople.coordinatemanager.screens.ManageScreen;
import com.anordinarypeople.coordinatemanager.utils.BufferHelper;
import com.anordinarypeople.coordinatemanager.utils.WorldKeyword;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

public class WorldPanel extends AlwaysSelectedEntryListWidget<WorldPanelEntry> implements AutoCloseable {
  private final ManageScreen parent;
  private final float z = 0.0F;
  private final int bgColor = ColorHelper.fromFloats(1.0F, z, z, z);
  private MutableText description;
  private Consumer<String> onSelected;
  private DrawContext context;
  private BufferHelper bufferHelper;
  private BufferBuilder builder;

  public WorldPanel(
      MinecraftClient client,
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

  private void drawItem(int entryCount, int index, int entryTop, int mouseX, int mouseY, float delta) {
    final int entryHeight = itemHeight - 5;
    final int rowWidth = getRowWidth();
    WorldPanelEntry entry = getEntry(index);
    int entryLeft;

    if (isSelectedEntry(index)) {
      entryLeft = getRowLeft() - 2;
      drawSelected(entryTop, entryHeight, entryLeft, rowWidth);
    }

    entryLeft = getRowLeft();
    entry.render(
        context,
        index,
        entryTop,
        entryLeft,
        rowWidth,
        entryHeight,
        mouseX,
        mouseY,
        isMouseOver(mouseX, mouseY)
            && Objects.equals(getEntryAtPos(entryCount, mouseX, mouseY), entry),
        delta);
  }

  private void drawSelected(int entryTop, int entryHeight, int entryLeft, int rowWidth) {
    final int selectionRight = getRowLeft() + rowWidth + 2;
    final float opacity = isFocused() ? 1.0F : 0.5F;
    final Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
    final int borderColor = ColorHelper.fromFloats(1.0F, opacity, opacity, opacity);
    builder = bufferHelper.begin();
    builder.vertex(matrix, entryLeft, entryTop + entryHeight + 2, z).color(borderColor);
    builder.vertex(matrix, selectionRight, entryTop + entryHeight + 2, z).color(borderColor);
    builder.vertex(matrix, selectionRight, entryTop - 2, z).color(borderColor);
    builder.vertex(matrix, entryLeft, entryTop - 2, z).color(borderColor);
    builder.vertex(matrix, entryLeft + 1, entryTop + entryHeight + 1, z).color(bgColor);
    builder.vertex(matrix, selectionRight - 1, entryTop + entryHeight + 1, z).color(bgColor);
    builder.vertex(matrix, selectionRight - 1, entryTop - 1, z).color(bgColor);
    builder.vertex(matrix, entryLeft + 1, entryTop - 1, z).color(bgColor);
    bufferHelper.render();
  }

  private WorldPanelEntry getEntryAtPos(int entryCount, double x, double y) {
    final int entryY = MathHelper.floor(y - ((double) getY()) - headerHeight + (int) getScrollY() - 4);
    final int index = entryY / itemHeight;
    final int rowLeft = getRowLeft();

    return x < (double) getScrollbarX()
        && x >= (double) rowLeft
        && x <= ((double) rowLeft + getRowRight())
        && index >= 0
        && entryY >= 0
        && index < entryCount
            ? children().get(index)
            : null;
  }

  private void scrollToTop() {
    final int max = Math.max(0, getContentsHeightWithPadding() - getBottom() - getY() - 4);

    if (getScrollY() > max) {
      setScrollY(max);
    }
  }

  public void filter(String keyword) {
    if (WorldCache.INSTANCE == null || WorldCache.INSTANCE.size() == 0) {
      description = Text.translatable("container.description");
      return;
    }
    String lowered = keyword.toLowerCase();
    Pattern pattern = Pattern.compile(lowered);
    boolean clearDescription = false;

    clearEntries();

    for (WorldData data : WorldCache.INSTANCE) {
      if (lowered.isBlank() || ((data.worldName != null &&
          pattern.matcher(data.worldName.toLowerCase()).find()) ||
          WorldKeyword.countMatched(data, pattern) > 0)) {
        clearDescription = true;
        addEntry(new WorldPanelEntry(client, data, lowered.isBlank() ? null : pattern));
      }
    }

    description = clearDescription ? null : Text.translatable("container.description_not_found");
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
        - (Math.max(0, getContentsHeightWithPadding() - (getBottom() - getY() - 4)) > 0 ? 18 : 12);
  }

  @Override
  public int getRowLeft() {
    return getX() + 6;
  }

  @Override
  protected void renderList(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    if (description != null) {
      drawContext.drawText(
          client.textRenderer,
          description,
          parent.padding * 2,
          parent.inputHeight + parent.padding * 3 + 2,
          Const.WHITE,
          true);
      return;
    }

    final int entryCount = getEntryCount();
    context = drawContext;
    bufferHelper = new BufferHelper(client, "World Panel");

    for (int i = 0; i < entryCount; i++) {
      int entryTop = getRowTop(i) + 2;
      int entryBottom = getRowTop(i) + itemHeight + 2;

      if (entryBottom >= getY() && entryTop <= getBottom()) {
        drawItem(entryCount, i, entryTop, mouseX, mouseY, delta);
      }
    }
  }
}
