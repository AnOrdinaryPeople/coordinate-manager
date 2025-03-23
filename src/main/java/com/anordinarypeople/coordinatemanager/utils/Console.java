package com.anordinarypeople.coordinatemanager.utils;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anordinarypeople.coordinatemanager.data.Const;

public class Console {
  private final String modId = Const.MOD_ID;
  private final String scope;
  private final Logger logger;

  public Console(String scope) {
    this.logger = LoggerFactory.getLogger(this.modId);
    this.scope = scope;
  }

  private String message(String message) {
    return String.format("[%s] %s", scope, message);
  }

  public void info(String msg) {
    logger.info(message(msg));
  }

  public void error(String msg, Throwable throwable) {
    logger.error(message(msg), throwable);
  }

  public void errorFile(String context, File file, Throwable throwable) {
    logger.error(
        String.format("Failed to %s from file: %s", context, file.getAbsolutePath()),
        throwable);
  }
}
