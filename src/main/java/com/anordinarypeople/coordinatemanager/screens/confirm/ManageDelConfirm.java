package com.anordinarypeople.coordinatemanager.screens.confirm;

import com.anordinarypeople.coordinatemanager.cache.Coordinate;
import com.anordinarypeople.coordinatemanager.enums.DeleteAllType;
import com.anordinarypeople.coordinatemanager.screens.ManageScreen;
import com.anordinarypeople.coordinatemanager.utils.BaseScreen;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class ManageDelConfirm extends BaseScreen {
  private final ManageScreen manage;
  private final String name;

  public ManageDelConfirm(ManageScreen parent, String worldName) {
    super(parent, "management.delete_confirmation");
    manage = parent;
    name = worldName;
  }

  private void renderTitle() {
    textField.text = Component.translatable(
        "management.delete_confirmation.title",
        trim(Component.literal(name)).setStyle(Style.EMPTY.withBold(true)))
        .setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
    int widthText = getWidthText(textField.text);
    textField.x = centerX - widthText / 2;
    textField.width = widthText;

    addRenderableWidget(textField.label());
  }

  private void renderMessage() {
    textField.text = Component.translatable(
        "management.delete_confirmation.message",
        Component.literal(Integer.toString(Coordinate.INSTANCE.size()))
            .setStyle(Style.EMPTY.withBold(true)));
    int widthText = getWidthText(textField.text);
    textField.x = centerX - widthText / 2;
    textField.y += baseHeight - 5;
    textField.width = widthText;

    addRenderableWidget(textField.label());
  }

  private void renderAllDelete() {
    button.width -= 2;
    addRenderableWidget(button.widget("management.delete_confirmation.all", b -> {
      onClickConfirm(DeleteAllType.ALL);
    }));
  }

  private void renderAllUnfavorite() {
    button.x += buttonWidth + 2;
    addRenderableWidget(button.widget("management.delete_confirmation.all_unfavorite", b -> {
      onClickConfirm(DeleteAllType.UNFAVORITE);
    }));
  }

  private void renderBack() {
    button.x = centerX / 2;
    button.y += baseHeight + 4;
    button.width = buttonWidth * 2;
    addRenderableWidget(button.widget("history.back", b -> onClose()));
  }

  private void onClickConfirm(DeleteAllType confirmType) {
    manage.onDeleteConfirmed(confirmType);
    onClose();
  }

  @Override
  protected void init() {
    baseInit();
    renderTitle();
    renderMessage();
    baseInitButton();
    renderAllDelete();
    renderAllUnfavorite();
    renderBack();
  }
}
