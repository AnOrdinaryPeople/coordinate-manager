package com.anordinarypeople.coordinatemanager.widgets.container;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.joml.Matrix4f;

import com.anordinarypeople.coordinatemanager.data.Const;
import com.anordinarypeople.coordinatemanager.data.ListSelectableCoor;
import com.anordinarypeople.coordinatemanager.data.SelectableCoor;
import com.anordinarypeople.coordinatemanager.screens.HistoryScreen;
import com.anordinarypeople.coordinatemanager.utils.BufferHelper;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ContainerPanel extends AlwaysSelectedEntryListWidget<ContainerPanelEntry> implements AutoCloseable {
  private final HistoryScreen parent;
  private final float z = 0.0F;
  private MutableText description;
  private Consumer<SelectableCoor> onSelected;
  private DrawContext context;
  private BufferHelper bufferHelper;
  private BufferBuilder builder;
  public ListSelectableCoor list;

  public ContainerPanel(
      MinecraftClient client,
      int width,
      int height,
      int x,
      int y,
      int itemHeight,
      ListSelectableCoor list,
      Consumer<SelectableCoor> onSelected,
      HistoryScreen parent) {
    super(client, width, height, y, itemHeight);

    this.setX(x);
    this.onSelected = onSelected;
    this.parent = parent;

    if (list != null) {
      this.list = list;
    }
  }

  private void drawItem(int entryCount, int index, int entryTop, int mouseX, int mouseY, float delta) {
    final int entryHeight = itemHeight - 5;
    final int rowWidth = getRowWidth();
    ContainerPanelEntry entry = getEntry(index);
    int entryLeft;

    if (parent.currentData != null && entry.data.uuid.equals(parent.currentData.uuid)) {
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
    int selectionRight = getRowLeft() + rowWidth + 2;
    setupShader(isFocused() ? 1.0F : 0.5F);
    Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
    builder.vertex(matrix, entryLeft, entryTop + entryHeight + 2, z);
    builder.vertex(matrix, selectionRight, entryTop + entryHeight + 2, z);
    builder.vertex(matrix, selectionRight, entryTop - 2, z);
    builder.vertex(matrix, entryLeft, entryTop - 2, z);
    bufferHelper.render();
    setupShader(z);
    builder.vertex(matrix, entryLeft + 1, entryTop + entryHeight + 1, z);
    builder.vertex(matrix, selectionRight - 1, entryTop + entryHeight + 1, z);
    builder.vertex(matrix, selectionRight - 1, entryTop - 1, z);
    builder.vertex(matrix, entryLeft + 1, entryTop - 1, z);
    bufferHelper.render();
  }

  private void setupShader(float shader) {
    RenderSystem.setShader(ShaderProgramKeys.POSITION);
    RenderSystem.setShaderColor(shader, shader, shader, 1.0F);

    builder = bufferHelper.begin();
  }

  private ContainerPanelEntry getEntryAtPos(int entryCount, double x, double y) {
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
    if (list == null || list.size() == 0) {
      description = Text.translatable("container.description");
      return;
    }
    String lowered = keyword.toLowerCase();
    Pattern pattern = Pattern.compile(lowered);
    boolean clearDescription = false;

    clearEntries();

    for (SelectableCoor data : list) {
      if (lowered.isBlank() || ((data.name != null &&
          pattern.matcher(data.name.toLowerCase()).find()) ||
          pattern.matcher(Double.toString(data.x)).find() ||
          pattern.matcher(Double.toString(data.y)).find() ||
          pattern.matcher(Double.toString(data.z)).find())) {
        clearDescription = true;
        addEntry(new ContainerPanelEntry(client, data));
      }
    }

    description = clearDescription ? null : Text.translatable("container.description_not_found");
    scrollToTop();
  }

  @Override
  public void close() {
  }

  @Override
  public void setSelected(ContainerPanelEntry entry) {
    super.setSelected(entry);

    if (parent.currentData == null || !parent.currentData.uuid.equals(entry.data.uuid)) {
      onSelected.accept(entry.data);
    }
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
    bufferHelper = new BufferHelper();

    for (int i = 0; i < entryCount; i++) {
      int entryTop = getRowTop(i) + 2;
      int entryBottom = getRowTop(i) + itemHeight + 2;

      if (entryBottom >= getY() && entryTop <= getBottom()) {
        drawItem(entryCount, i, entryTop, mouseX, mouseY, delta);
      }
    }
  }
}
