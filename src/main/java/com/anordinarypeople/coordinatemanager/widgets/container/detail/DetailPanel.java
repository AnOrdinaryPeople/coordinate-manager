package com.anordinarypeople.coordinatemanager.widgets.container.detail;

import com.anordinarypeople.coordinatemanager.data.SelectableCoor;
import com.anordinarypeople.coordinatemanager.screens.HistoryScreen;
import com.anordinarypeople.coordinatemanager.widgets.Button;
import com.anordinarypeople.coordinatemanager.widgets.TextField;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class DetailPanel extends DetailPanelBg {
  private final int inputHeight = 15;
  private final int baseWidth;
  private final HistoryScreen parent;
  private final TextField textField;
  private final Button button;
  public final TextWidget description;
  public final TextFieldWidget nameField;
  public final TextFieldWidget xField;
  public final TextFieldWidget yField;
  public final TextFieldWidget zField;
  public ButtonWidget favoriteButton;
  public ButtonWidget copyButton;
  public ButtonWidget selectButton;
  public ButtonWidget deleteButton;
  public ButtonWidget saveButton;

  public DetailPanel(
      HistoryScreen screen,
      TextRenderer textRenderer,
      MinecraftClient client,
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
    nameField.setPlaceholder(placeholder("detail.name"));
    nameField.visible = false;
    xField = renderInputField("detail.x");
    yField = renderInputField("detail.y");
    zField = renderInputField("detail.z");

    button = new Button(textField.x, textField.y + inputHeight + padding, baseWidth / 2, inputHeight);
    renderButtons();
  }

  private Text placeholder(String translationKey) {
    return Text.translatable(translationKey).setStyle(Style.EMPTY.withItalic(true));
  }

  private TextFieldWidget renderInputField(String translationKey) {
    textField.y += inputHeight + padding;

    TextFieldWidget field = textField.input("");
    field.setPlaceholder(placeholder(translationKey));
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

  private void onClickPin(ButtonWidget b) {
    boolean isPinned = b.getMessage().equals(Text.translatable("detail.favorite"));

    parent.onClickPin();

    b.setMessage(Text.translatable(isPinned ? "detail.unfavorite" : "detail.favorite"));
    deleteButton.active = !isPinned;
  }

  private void onClickSelect(ButtonWidget b) {
    boolean isSelected = b.getMessage().equals(Text.translatable("detail.select"));

    parent.onClickSelect();

    b.setMessage(Text.translatable(isSelected ? "detail.unselect" : "detail.select"));
  }

  public void updateDetail(SelectableCoor data) {
    if (data != null) {
      nameField.setText(data.name != null && !data.name.isBlank() ? data.name : "");
      xField.setText(Double.toString(data.x));
      yField.setText(Double.toString(data.y));
      zField.setText(Double.toString(data.z));
      favoriteButton.setMessage(Text.translatable(data.isPinned ? "detail.unfavorite" : "detail.favorite"));
      selectButton.setMessage(Text.translatable(data.isSelected ? "detail.unselect" : "detail.select"));
      deleteButton.active = !data.isPinned;
      toggleDetail(true);
    } else {
      toggleDetail(false);
    }
  }
}
