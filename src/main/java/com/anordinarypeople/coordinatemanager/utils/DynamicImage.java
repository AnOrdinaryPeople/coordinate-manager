package com.anordinarypeople.coordinatemanager.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.Nullable;

import com.anordinarypeople.coordinatemanager.data.Const;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public class DynamicImage {
  private static final Console logger = new Console("dynamic-image");
  private static final float z = 0.0F;
  private static final Map<String, Identifier> cache = new ConcurrentHashMap<>();

  private static Identifier get(MinecraftClient client, @Nullable String path) {
    if (path != null) {
      File file = new File(path);
      String cacheKey = file.getAbsolutePath();

      if (cache.containsKey(cacheKey)) {
        return cache.get(cacheKey);
      }

      if (file.exists()) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
          String name = file.getName();
          NativeImage image = NativeImage.read(inputStream);
          NativeImageBackedTexture texture = new NativeImageBackedTexture(() -> name, image);
          Identifier dynamicId = Identifier.of(Const.MOD_ID, name);

          client.getTextureManager().registerTexture(dynamicId, texture);
          cache.put(cacheKey, dynamicId);

          return dynamicId;
        } catch (IOException e) {
          logger.errorFile("stream image", file, e);
        }
      }
    }

    String fallbackKey = "icon.png";

    if (!cache.containsKey(fallbackKey)) {
      Identifier fallbackId = Identifier.of(Const.MOD_ID, fallbackKey);
      cache.put(fallbackKey, fallbackId);

      return fallbackId;
    }

    return cache.get(fallbackKey);
  }

  public static void render(DrawContext context, MinecraftClient client, String path, int x, int y, int size) {
    context.drawTexture(RenderPipelines.GUI_TEXTURED, get(client, path), x, y, z, z, size, size, size, size);
  }
}
