package com.shop.emul;

import static com.shop.emul.Cashbox.Markup.EVENING;
import static com.shop.emul.Cashbox.Markup.NORMAL_DAY;
import static com.shop.emul.Cashbox.Markup.WEEKEND_DAY;

/**
 * Tracks time during work and triggers time dependent events.
 * Singleton class.
 *
 * @author KossKucher
 * @version 1.0
 */
public class TimeKeeper {
  
  /*constants*/
  private static final int MONTH_LENGTH = 30;
  private static final int OPEN_TIME = 8;
  private static final int CLOSE_TIME = 21;
  
  private static TimeKeeper timeKeeper = null;
  
  private int day;
  private int hour;
  private Cashbox.Markup markup;
  
  /**
   * Private constructor, forbids creation of multiple instances.
   */
  private TimeKeeper() {
    day = 1;
    hour = OPEN_TIME;
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
    if (hour == CLOSE_TIME) {
      DbManager.get().refreshStorage();
      day++;
      hour = OPEN_TIME;
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
    return day > MONTH_LENGTH;
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
