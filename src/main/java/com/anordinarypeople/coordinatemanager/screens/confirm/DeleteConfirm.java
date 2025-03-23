package com.anordinarypeople.coordinatemanager.screens.confirm;

import com.anordinarypeople.coordinatemanager.data.Const;
import com.anordinarypeople.coordinatemanager.screens.HistoryScreen;
import com.anordinarypeople.coordinatemanager.utils.BaseScreen;
import com.anordinarypeople.coordinatemanager.utils.DimensionColor;
import com.anordinarypeople.coordinatemanager.utils.DynamicImage;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class DeleteConfirm extends BaseScreen {
  private final HistoryScreen history;
  private final int imageSize = 64;
  private int imageY;

  public DeleteConfirm(HistoryScreen parent) {
    super(parent, "delete.title");
    history = parent;
  }

  private void renderImage(DrawContext context) {
    if (history.currentData.imagePath != null) {
      DynamicImage.render(
          context,
          client,
          history.currentData.imagePath,
          (width - imageSize) / 2,
          imageY,
          imageSize);
    }
  }

  private void renderConfirmation() {
    textField.text = trim(Text.translatable("delete.message"));
    int widthText = getWidthText(textField.text);
    textField.x = centerX - widthText / 2;
    textField.width = widthText;

    addDrawableChild(textField.label());
  }

  private void renderWorld() {
    textField.text = trim(
        history.currentData.name != null
            ? Text.literal(
                history.currentData.name)
            : Text.translatable("container.unnamed"))
        .setStyle(Style.EMPTY.withBold(true));
    int widthText = getWidthText(textField.text);
    textField.x = centerX - widthText / 2;
    textField.y += baseHeight - history.padding;
    textField.width = widthText;

    addDrawableChild(textField.label());
  }

  private void renderDetail() {
    textField.text = Text.translatable(
        history.currentData.dimension)
        .setStyle(Style.EMPTY.withColor(DimensionColor.get(
            history.currentData.dimension)))
        .append(Text.literal(" "))
        .append(trim(Text.literal(
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

    addDrawableChild(textField.label());
  }

  private void renderConfirm() {
    button.width -= 4;
    addDrawableChild(button.widget("delete.yes", b -> {
      history.deleteConfirmed();
      close();
    }));
  }

  private void renderBack() {
    button.x += buttonWidth + 2;
    addDrawableChild(button.widget("delete.no", b -> close()));
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
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);
    renderImage(context);
  }
}
