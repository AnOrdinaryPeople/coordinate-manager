package com.anordinarypeople.coordinatemanager.cache;

import java.io.File;

import com.anordinarypeople.coordinatemanager.data.CoordinateData;
import com.anordinarypeople.coordinatemanager.data.ListCoordinate;
import com.anordinarypeople.coordinatemanager.utils.Storage;

public class Coordinate {
  private static final int maxStrLength = 250;
  private static Storage<ListCoordinate> storage;
  public static String CURRENT_WORLD;
  public static File JSON_FILE;
  public static ListCoordinate INSTANCE;

  private static String sanitize(String value) {
    String sanitized = value
        .replaceAll("§.", "")
        .replaceAll("[\\\\/:*?\"<>|]", "_")
        .replaceAll("^[ .]+|[ .]+$", "");

    if (sanitized.length() > maxStrLength) {
      sanitized = sanitized.substring(0, maxStrLength);
    }

    return sanitized;
  }

  public static void load(String world) {
    storage = new Storage<>("coordinates/" + sanitize(world), new ListCoordinate(), ListCoordinate.class);
    CURRENT_WORLD = world;
    JSON_FILE = storage.file;
    INSTANCE = storage.getAll();
  }

  public static int set(CoordinateData data, boolean deleteItem) {
    ListCoordinate temp = new ListCoordinate(INSTANCE.size());
    int idx = -1;
    int tempIdx = 0;

    for (CoordinateData coor : INSTANCE) {
      boolean isEqual = coor.uuid.equals(data.uuid);

      if (isEqual && idx == -1) {
        idx = tempIdx;
      }

      if (isEqual && deleteItem) {
        continue;
      }

      temp.add(isEqual ? data : coor);
      tempIdx++;
    }

    INSTANCE = temp;

    return idx;
  }

  public static void save() {
    storage.setAll(INSTANCE);
  }
}
