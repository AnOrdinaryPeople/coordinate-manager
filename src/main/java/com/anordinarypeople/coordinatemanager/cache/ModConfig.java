package com.anordinarypeople.coordinatemanager.cache;

import com.anordinarypeople.coordinatemanager.data.Configuration;
import com.anordinarypeople.coordinatemanager.utils.Storage;

public class ModConfig {
  private static Storage<Configuration> storage = new Storage<>("config", new Configuration(), Configuration.class);
  public static Configuration INSTANCE;

  public static void load() {
    INSTANCE = storage.getAll();
  }

  public static void save() {
    storage.setAll(INSTANCE);
  }
}
