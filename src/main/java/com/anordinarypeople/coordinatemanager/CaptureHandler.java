package com.anordinarypeople.coordinatemanager;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import com.anordinarypeople.coordinatemanager.cache.Coordinate;
import com.anordinarypeople.coordinatemanager.cache.ModConfig;
import com.anordinarypeople.coordinatemanager.cache.WorldCache;
import com.anordinarypeople.coordinatemanager.data.Const;
import com.anordinarypeople.coordinatemanager.data.CoordinateData;
import com.anordinarypeople.coordinatemanager.enums.CaptureMode;
import com.anordinarypeople.coordinatemanager.screens.CaptureScreen;
import com.anordinarypeople.coordinatemanager.utils.Console;
import com.anordinarypeople.coordinatemanager.utils.CopyXYZ;
import com.anordinarypeople.coordinatemanager.utils.LoadFileFromLoader;
import com.anordinarypeople.coordinatemanager.utils.Messager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class CaptureHandler {
  private final Console logger = new Console("capture-handler");
  private RegistryKey<World> dimension;
  private MinecraftClient client;
  private CaptureScreen screen;
  private String imagePath = null;
  public String name = null;
  public double x;
  public double y;
  public double z;

  private double rounded(double value) {
    return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
  }

  private String getDimension() {
    if (dimension == World.OVERWORLD) {
      return Const.OVERWORLD;
    } else if (dimension == World.NETHER) {
      return Const.NETHER;
    } else if (dimension == World.END) {
      return Const.END;
    }

    return Const.UNKNOWN_WORLD;
  }

  private void createImage() {
    Framebuffer framebuffer = client.getFramebuffer();
    File path = LoadFileFromLoader.fabric(Const.MODID);
    String filename = String.format(
        "%s_%s.png",
        new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()),
        getCoordinate().replaceAll("[XYZ.\\-:, ]+", ""));
    File imageFile = new File(path, "screenshots/" + filename);

    ScreenshotRecorder.saveScreenshot(path, filename, framebuffer, successText -> virtualProcess(imageFile));

    imagePath = imageFile.getAbsolutePath();
  }

  private void virtualProcess(File imageFile) {
    Thread.ofVirtual().start(() -> {
      try {
        BufferedImage original = ImageIO.read(imageFile);
        BufferedImage resized = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resized.createGraphics();

        g.drawImage(original.getScaledInstance(128, 128, Image.SCALE_SMOOTH), 0, 0, null);
        g.dispose();

        ImageIO.write(resized, "png", imageFile);
      } catch (IOException e) {
        logger.errorFile("read/write image", imageFile, e);
      }
    });
  }

  private String getCoordinate() {
    return String.format(Const.COORDINATE, x, y, z);
  }

  private void message() {
    client.player.sendMessage(
        Messager.info(
            ModConfig.INSTANCE.autoCopy ? "capture.success_with_copy" : "capture.success",
            getCoordinate(),
            Text.translatable(
                getDimension())),
        false);
  }

  public void saveCoordinate() {
    CoordinateData data = new CoordinateData(name, getDimension(), x, y, z);

    data.imagePath = imagePath;

    Coordinate.INSTANCE.addFirst(data);
    Coordinate.save();
    WorldCache.save();
    message();
  }

  public void deleteImage() {
    new File(imagePath).delete();
  }

  public void store(MinecraftClient mcClient) {
    client = mcClient;
    dimension = client.world.getRegistryKey();
    x = rounded(client.player.getX());
    y = rounded(client.player.getY());
    z = rounded(client.player.getZ());

    if (!ModConfig.INSTANCE.isPreciseCoordinate) {
      x = (int) x;
      y = (int) y;
      z = (int) z;
    }

    if (ModConfig.INSTANCE.autoCopy) {
      CopyXYZ.copy(client, x, y, z);
    }

    if (ModConfig.INSTANCE.enableImage) {
      createImage();
    }

    if (ModConfig.INSTANCE.captureMode == CaptureMode.INSTANT) {
      name = null;
      saveCoordinate();
    } else {
      screen = new CaptureScreen(this);
      client.setScreen(screen);
    }
  }
}
