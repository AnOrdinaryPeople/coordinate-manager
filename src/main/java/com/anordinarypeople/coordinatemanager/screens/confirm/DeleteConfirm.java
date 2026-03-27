package com.anordinarypeople.coordinatemanager.screens.confirm;

import com.anordinarypeople.coordinatemanager.data.Const;
import com.anordinarypeople.coordinatemanager.screens.HistoryScreen;
import com.anordinarypeople.coordinatemanager.utils.BaseScreen;
import com.anordinarypeople.coordinatemanager.utils.DimensionColor;
import com.anordinarypeople.coordinatemanager.utils.DynamicImage;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class DeleteConfirm extends BaseScreen {
  private final HistoryScreen history;
  private final int imageSize = 64;
  private int imageY;

  public DeleteConfirm(HistoryScreen parent) {
    super(parent, "delete.title");
    history = parent;
  }

  private void renderImage(GuiGraphicsExtractor context) {
    if (history.currentData.imagePath != null) {
      DynamicImage.render(
          context,
          minecraft,
          history.currentData.imagePath,
          (width - imageSize) / 2,
          imageY,
          imageSize);
    }
  }

  private void renderConfirmation() {
    textField.text = trim(Component.translatable("delete.message"));
    int widthText = getWidthText(textField.text);
    textField.x = centerX - widthText / 2;
    textField.width = widthText;

    addRenderableWidget(textField.label());
  }

  private void renderWorld() {
    textField.text = trim(
        history.currentData.name != null
            ? Component.literal(
                history.currentData.name)
            : Component.translatable("container.unnamed"))
        .setStyle(Style.EMPTY.withBold(true));
    int widthText = getWidthText(textField.text);
    textField.x = centerX - widthText / 2;
    textField.y += baseHeight - history.padding;
    textField.width = widthText;

    addRenderableWidget(textField.label());
  }

  private void renderDetail() {
    textField.text = Component.translatable(
        history.currentData.dimension)
        .setStyle(Style.EMPTY.withColor(DimensionColor.get(
            history.currentData.dimension)))
        .append(Component.literal(" "))
        .append(trim(Component.literal(
            String.format(
                Const.COORDINATE,
                history.currentData.x,
                history.currentData.y,
                history.currentData.z)))
            .setStyle(Style.EMPTY.withColor(Const.WHITE)));
    int widthText = getWidthText(textField.text);
    textField.x = centerX - widthText / 2;
    textField.y += baseHeight - history.padding;
    textField.width = widthText;

    addRenderableWidget(textField.label());
  }

  private void renderConfirm() {
    button.width -= 4;
    addRenderableWidget(button.widget("delete.yes", b -> {
      history.deleteConfirmed();
      onClose();
    }));
  }

  private void renderBack() {
    button.x += buttonWidth + 2;
    addRenderableWidget(button.widget("delete.no", b -> onClose()));
  }

  @Override
  protected void init() {
    baseInit();
    imageY = height / 2 - imageSize;
    textField.y = imageY + 60 + history.padding;
    renderConfirmation();
    renderWorld();
    renderDetail();
    baseInitButton();
    renderConfirm();
    renderBack();
  }

  @Override
  public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
    super.extractRenderState(context, mouseX, mouseY, delta);
    renderImage(context);
  }
}
