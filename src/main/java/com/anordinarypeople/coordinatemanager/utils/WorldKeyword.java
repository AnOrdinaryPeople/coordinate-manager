package com.anordinarypeople.coordinatemanager.utils;

import java.util.regex.Pattern;

import com.anordinarypeople.coordinatemanager.data.WorldSearch;
import com.anordinarypeople.coordinatemanager.data.WorldData;

public class WorldKeyword {
  public static int countMatched(WorldData worlds, Pattern pattern) {
    int total = 0;

    for (WorldSearch keyword : worlds.keywords) {
      if (pattern.matcher(keyword.name.toLowerCase()).find() ||
          pattern.matcher(keyword.x).find() ||
          pattern.matcher(keyword.y).find() ||
          pattern.matcher(keyword.z).find()) {
        total++;
      }
    }

    return total;
  }
}
