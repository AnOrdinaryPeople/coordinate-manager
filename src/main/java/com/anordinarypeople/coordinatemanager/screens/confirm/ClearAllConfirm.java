package com.anordinarypeople.coordinatemanager.screens.confirm;

import com.anordinarypeople.coordinatemanager.screens.HistoryScreen;
import com.anordinarypeople.coordinatemanager.utils.BaseScreen;

import net.minecraft.text.Text;

public class ClearAllConfirm extends BaseScreen {
  private final HistoryScreen history;

  public ClearAllConfirm(HistoryScreen parent) {
    super(parent, "clear_all.title");
    history = parent;
  }

  private void renderConfirmation() {
    textField.text = trim(Text.translatable("clear_all.message"));
    int widthText = getWidthText(textField.text);
    textField.x = centerX - widthText / 2;
    textField.width = widthText;

    addDrawableChild(textField.label());
  }

  private void renderConfirm() {
    button.width -= 4;
    addDrawableChild(button.widget("delete.yes", b -> {
      history.onClearAllConfirmed();
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
    renderConfirmation();
    baseInitButton();
    renderConfirm();
    renderBack();
  }
}
