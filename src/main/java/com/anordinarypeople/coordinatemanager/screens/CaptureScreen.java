package com.anordinarypeople.coordinatemanager.screens;

import com.anordinarypeople.coordinatemanager.CaptureHandler;
import com.anordinarypeople.coordinatemanager.widgets.Button;
import com.anordinarypeople.coordinatemanager.widgets.TextField;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class CaptureScreen extends Screen {
  private final CaptureHandler handler;
  private final int padding = 5;
  private final int inputHeight = 20;
  private final int buttonHeight = 20;
  private final int footerHeight = 25;
  private final TextField textField = new TextField(null, 0, 0, 0, inputHeight);
  private final Button button = new Button(0, 0, 0, buttonHeight);
  private TextFieldWidget nameField;
  private TextFieldWidget xField;
  private TextFieldWidget yField;
  private TextFieldWidget zField;
  private int fieldWidth;
  private boolean isSaved = false;

  public CaptureScreen(CaptureHandler handler) {
    super(Text.translatable("capture.title"));

    this.handler = handler;
  }

  private void renderSubmitBtn() {
    button.x = padding;

    addDrawableChild(button.widget(
        "capture.submit",
        b -> {
          String name = nameField.getText();
          isSaved = true;

          handler.name = name.isBlank() || name.isEmpty() ? null : name;
          handler.x = getNumber(xField);
          handler.y = getNumber(yField);
          handler.z = getNumber(zField);

          handler.saveCoordinate();
          close();
        }));
  }

  private void renderBackBtn() {
    button.x = padding * 2 + button.width;

    addDrawableChild(button.widget(
        "history.back",
        b -> close()));
  }

  private double getNumber(TextFieldWidget field) {
    double number;

    try {
      number = Double.valueOf(field.getText());
    } catch (Exception e) {
      number = 0;
    }

    return number;
  }

  private TextFieldWidget renderNameField(String translationKey) {
    textField.x = (width / 2) - fieldWidth / 2;
    textField.y = padding;
    addDrawableChild(textField.label(translationKey));

    textField.y = inputHeight + padding;
    TextFieldWidget field = textField.input("");
    addDrawableChild(field);

    return field;
  }

  private TextFieldWidget renderInputField(String translationKey, double value) {
    int originalX = textField.x;

    textField.x = textField.x / 2 - padding * 2;
    textField.y += inputHeight + padding;

    addDrawableChild(textField.label(translationKey));

    textField.x = originalX;
    TextFieldWidget field = textField.input(String.valueOf(value));
    addDrawableChild(field);

    return field;
  }

  @Override
  public void close() {
    if (!isSaved) {
      handler.deleteImage();
    }

    super.close();
  }

  @Override
  protected void init() {
    fieldWidth = width / 3;
    textField.textRenderer = textRenderer;
    textField.width = width / 3;

    nameField = renderNameField("capture.name");
    xField = renderInputField("capture.x", handler.x);
    yField = renderInputField("capture.y", handler.y);
    zField = renderInputField("capture.z", handler.z);

    int footerY = height - footerHeight;
    button.y = footerY + (footerHeight - buttonHeight) / 2;
    button.width = (width - padding * 4) / 2;

    renderSubmitBtn();
    renderBackBtn();
  }
}
