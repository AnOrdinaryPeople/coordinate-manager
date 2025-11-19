package com.anordinarypeople.coordinatemanager.data;

import com.anordinarypeople.coordinatemanager.enums.CaptureMode;

public class Configuration {
  public CaptureMode captureMode = CaptureMode.INSTANT;
  public int captureCooldown = 250;
  public boolean enableImage = false;
  public boolean autoCopy = false;
  public boolean isPreciseCoordinate = false;
  public boolean systemAwareCopy = false;
}
