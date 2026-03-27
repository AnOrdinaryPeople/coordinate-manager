package com.anordinarypeople.coordinatemanager.widgets.container.detail;

import com.anordinarypeople.coordinatemanager.data.SelectableCoor;
import com.anordinarypeople.coordinatemanager.screens.HistoryScreen;
import com.anordinarypeople.coordinatemanager.widgets.Btn;
import com.anordinarypeople.coordinatemanager.widgets.TextField;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class DetailPanel extends DetailPanelBg {
  private final int inputHeight = 15;
  private final int baseWidth;
  private final HistoryScreen parent;
  private final TextField textField;
  private final Btn button;
  public final StringWidget description;
  public final EditBox nameField;
  public final EditBox xField;
  public final EditBox yField;
  public final EditBox zField;
  public Button favoriteButton;
  public Button copyButton;
  public Button selectButton;
  public Button deleteButton;
  public Button saveButton;

  public DetailPanel(
      HistoryScreen screen,
      Font textRenderer,
      Minecraft client,
      int x,
      int y,
      int width,
      int height) {
    super(client, x, y, width, height);

    parent = screen;
    baseWidth = width - padding * 2;
    textField = new TextField(
        textRenderer,
        left - width + padding,
        y + 3,
        baseWidth,
        inputHeight);

    description = textField.label("history.description.detail");
    textField.y = y + imageSize + padding * 2;
    nameField = textField.input("");
    nameField.setHint(placeholder("detail.name"));
    nameField.visible = false;
    xField = renderInputField("detail.x");
    yField = renderInputField("detail.y");
    zField = renderInputField("detail.z");

    button = new Btn(textField.x, textField.y + inputHeight + padding, baseWidth / 2, inputHeight);
    renderButtons();
  }

  private Component placeholder(String translationKey) {
    return Component.translatable(translationKey).setStyle(Style.EMPTY.withItalic(true));
  }

  private EditBox renderInputField(String translationKey) {
    textField.y += inputHeight + padding;

    EditBox field = textField.input("");
    field.setHint(placeholder(translationKey));
    field.visible = false;

    return field;
  }

  private void toggleDetail(boolean bool) {
    description.visible = !bool;
    nameField.visible = bool;
    xField.visible = bool;
    yField.visible = bool;
    zField.visible = bool;
    favoriteButton.visible = bool;
    copyButton.visible = bool;
    selectButton.visible = bool;
    deleteButton.visible = bool;
    saveButton.visible = bool;
  }

  private void renderButtons() {
    int x = baseWidth / 2;
    int y = inputHeight + padding;
    button.width -= 2;
    favoriteButton = button.widget("detail.favorite", b -> onClickPin(b));
    favoriteButton.visible = false;
    button.x += x + 2;
    copyButton = button.widget("history.copy", "history.copy.tooltip", b -> parent.onClickCopy());
    copyButton.visible = false;
    button.x -= x + 2;
    button.y += y;
    selectButton = button.widget("detail.select", b -> onClickSelect(b));
    selectButton.visible = false;
    button.x += x + 2;
    deleteButton = button.widget("detail.delete", "detail.delete.tooltip", b -> {
      parent.onClickDelete();
      toggleDetail(false);
    });
    deleteButton.visible = false;
    button.x -= x + 2;
    button.y += y;
    button.width = baseWidth;
    saveButton = button.widget("detail.save", b -> parent.onClickSave());
    saveButton.visible = false;
  }

  private void onClickPin(Button b) {
    boolean isPinned = b.getMessage().equals(Component.translatable("detail.favorite"));

    parent.onClickPin();

    b.setMessage(Component.translatable(isPinned ? "detail.unfavorite" : "detail.favorite"));
    deleteButton.active = !isPinned;
  }

  private void onClickSelect(Button b) {
    boolean isSelected = b.getMessage().equals(Component.translatable("detail.select"));

    parent.onClickSelect();

    b.setMessage(Component.translatable(isSelected ? "detail.unselect" : "detail.select"));
  }

  public void updateDetail(SelectableCoor data) {
    if (data != null) {
      nameField.setValue(data.name != null && !data.name.isBlank() ? data.name : "");
      xField.setValue(Double.toString(data.x));
      yField.setValue(Double.toString(data.y));
      zField.setValue(Double.toString(data.z));
      favoriteButton.setMessage(Component.translatable(data.isPinned ? "detail.unfavorite" : "detail.favorite"));
      selectButton.setMessage(Component.translatable(data.isSelected ? "detail.unselect" : "detail.select"));
      deleteButton.active = !data.isPinned;
      toggleDetail(true);
    } else {
      toggleDetail(false);
    }
  }
}
