package com.anordinarypeople.coordinatemanager.utils;

import java.io.File;

import net.fabricmc.loader.api.FabricLoader;

public class LoadFileFromLoader {
  public static File fabric(String child) {
    return new File(FabricLoader.getInstance().getConfigDir().toFile(), child);
  }
}
