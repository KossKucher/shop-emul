package com.shop.emul;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.shop.emul.Cashbox.Markup.EVENING;
import static com.shop.emul.Cashbox.Markup.NORMAL_DAY;
import static com.shop.emul.Cashbox.Markup.WEEKEND_DAY;
import static com.shop.emul.Config.CLOSE_TIME;
import static com.shop.emul.Config.MONTH_LENGTH;
import static com.shop.emul.Config.OPEN_TIME;
import static com.shop.emul.Config.PROPERTIES_FILE;

/**
 * Tracks time during work and triggers time dependent events.
 * Singleton class.
 *
 * @author KossKucher
 * @version 1.0
 */
public class TimeKeeper {
  
  private static TimeKeeper timeKeeper = null;
  
  private final int closeTime;
  private final int openTime;
  private final int monthLength;
  
  private int day;
  private int hour;
  private Cashbox.Markup markup;
  
  /**
   * Private constructor, forbids creation of multiple instances.
   */
  private TimeKeeper() {
    Properties prop = new Properties();
    try (InputStream in = getClass().getResourceAsStream("/" + PROPERTIES_FILE.getDefault())) {
      prop.load(in);
    } catch (IOException e) {
      e.printStackTrace();
    }
    monthLength = Integer.parseInt(prop.getProperty(MONTH_LENGTH.name(),
                                                    MONTH_LENGTH.getDefault()));
    openTime = Integer.parseInt(prop.getProperty(OPEN_TIME.name(), OPEN_TIME.getDefault()));
    closeTime = Integer.parseInt(prop.getProperty(CLOSE_TIME.name(), CLOSE_TIME.getDefault()));
    day = 1;
    hour = openTime;
    markup = NORMAL_DAY;
  }
  
  /**
   * Singleton getter.
   *
   * @return current instance of this class if exists, otherwise initializes a new instance
   */
  public static TimeKeeper get() {
    if (timeKeeper == null) {
      timeKeeper = new TimeKeeper();
    }
    return timeKeeper;
  }
  
  /**
   * Tracks time and triggers time dependent events.
   * Triggers storage buy orders. Triggers markup updates.
   */
  public void tickTock() {
    hour++;
    if (hour == closeTime) {
      DbManager.get().refreshStorage();
      day++;
      hour = openTime;
    }
    updateMarkup();
  }
  
  /**
   * Generated getter.
   */
  public Cashbox.Markup getMarkup() {
    return markup;
  }
  
  /**
   * Checks if month is elapsed.
   *
   * @return {@code boolean} true if more than 30 days are passed, otherwise false
   */
  public boolean isMonthEnded() {
    return day > monthLength;
  }
  
  /**
   * Checks if current day is weekend day.
   *
   * @return {@code boolean} true if the day is 6th or 7th in the sequence, otherwise false
   */
  private boolean isWeekend() {
    return (day % 7 == 0 || (day + 1) % 7 == 0);
  }
  
  /**
   * Updates markup according to time and day.
   */
  private void updateMarkup() {
    switch (hour) {
      case 18:
      case 19:
        markup = EVENING;
        break;
      default:
        markup = (isWeekend()) ? WEEKEND_DAY : NORMAL_DAY;
    }
  }
}
