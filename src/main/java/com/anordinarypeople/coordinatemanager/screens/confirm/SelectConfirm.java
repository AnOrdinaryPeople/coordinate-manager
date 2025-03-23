package com.anordinarypeople.coordinatemanager.screens.confirm;

import com.anordinarypeople.coordinatemanager.enums.SelectConfirmType;
import com.anordinarypeople.coordinatemanager.screens.HistoryScreen;
import com.anordinarypeople.coordinatemanager.utils.BaseScreen;

import net.minecraft.text.Text;

public class SelectConfirm extends BaseScreen {
  private final HistoryScreen history;

  public SelectConfirm(HistoryScreen parent) {
    super(parent, "select.title");
    history = parent;
  }

  private void renderConfirmation() {
    textField.text = trim(Text.translatable("select.message", history.totalSelected));
    int widthText = getWidthText(textField.text);
    textField.x = centerX - widthText / 2;
    textField.width = widthText;

    addDrawableChild(textField.label());
  }

  private void renderFavorite() {
    button.x /= 2;
    button.width -= 4;
    addDrawableChild(button.widget("detail.favorite", b -> {
      onClickConfirm(SelectConfirmType.FAVORITE);
    }));
  }

  private void renderUnfavorite() {
    button.x += buttonWidth + 2;
    addDrawableChild(button.widget("detail.unfavorite", b -> {
      onClickConfirm(SelectConfirmType.UNFAVORITE);
    }));
  }

  private void renderDelete() {
    button.x += buttonWidth + 2;
    addDrawableChild(button.widget("detail.delete", b -> {
      onClickConfirm(SelectConfirmType.DELETE);
    }));
  }

  private void renderBack() {
    button.x = (centerX - buttonWidth) / 2;
    button.y += baseHeight + 4;
    button.width = buttonWidth * 3;
    addDrawableChild(button.widget("history.back", b -> close()));
  }

  private void onClickConfirm(SelectConfirmType confirmType) {
    history.onSelectedConfirmed(confirmType);
    close();
  }

  @Override
  protected void init() {
    baseInit();
    renderConfirmation();
    baseInitButton();
    renderFavorite();
    renderUnfavorite();
    renderDelete();
    renderBack();
  }
}
