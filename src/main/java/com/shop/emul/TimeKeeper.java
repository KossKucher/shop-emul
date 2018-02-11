package com.shop.emul;

import static com.shop.emul.Cashbox.Markup.*;

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
  
  private TimeKeeper() {
    day = 1;
    hour = OPEN_TIME;
    markup = NORMAL_DAY;
  }
  
  public static TimeKeeper get() {
    if (timeKeeper == null) {
      timeKeeper = new TimeKeeper();
    }
    return timeKeeper;
  }
  
  public void tickTock() {
    hour++;
    if (hour == CLOSE_TIME) {
      DbManager.get().refreshStorage();
      day++;
      hour = OPEN_TIME;
    }
    updateMarkup();
  }
  
  public Cashbox.Markup getMarkup() {
    return markup;
  }
  
  public boolean isMonthEnded() {
    return day > MONTH_LENGTH;
  }
  
  private boolean isWeekend() {
    return (day % 7 == 0 || (day + 1) % 7 == 0);
  }
  
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
