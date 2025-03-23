package com.anordinarypeople.coordinatemanager.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Storage<T> {
  private final Console logger = new Console("storage");
  private final Gson gson = new GsonBuilder().serializeNulls().create();
  private final T baseValue;
  private Type generic;
  public File file;

  public Storage(String storageKey, T defaultValue, Type typeOfT) {
    baseValue = defaultValue;
    file = LoadFileFromLoader.fabric(String.format("coordinatemanager/%s.json", storageKey));
    generic = typeOfT;

    if (!file.exists()) {
      createJson();
    }
  }

  private void createJson() {
    try {
      file.getParentFile().mkdirs();
      file.createNewFile();

      try (FileWriter writer = new FileWriter(file)) {
        gson.toJson(baseValue, writer);
      }
    } catch (IOException e) {
      logger.errorFile("write JSON", file, e);
    }
  }

  private T getJson() {
    T data = null;

    try (FileReader reader = new FileReader(file)) {
      data = gson.fromJson(reader, generic);
    } catch (IOException e) {
      logger.errorFile("read JSON", file, e);
    }

    return data;
  }

  public T getAll() {
    return getJson();
  }

  public void setAll(T value) {
    try (FileWriter writer = new FileWriter(file)) {
      gson.toJson(value, writer);
    } catch (IOException e) {
      logger.errorFile("write JSON", file, e);
    }
  }
}
