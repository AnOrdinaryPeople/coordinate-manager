package com.anordinarypeople.coordinatemanager.utils;

import com.anordinarypeople.coordinatemanager.data.Const;

public class DimensionColor {
  public static int get(String dimension) {
    return switch (dimension) {
      case Const.OVERWORLD -> Const.LIGHT_BLUE;
      case Const.NETHER -> Const.RED;
      case Const.END -> Const.PURPLE;
      default -> Const.WHITE;
    };
  }
}
