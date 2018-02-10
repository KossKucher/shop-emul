package com.shop.emul;

import static com.shop.emul.TimeKeeper.Markup.*;

/**
 * Tracks time during work and controls the time dependent markups.
 * Singleton class.
 *
 * @author Ricko
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
  private Markup markup;
  
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
      day++;
      hour = OPEN_TIME;
      //TODO: storage buy update method
    }
    updateMarkup();
  }
  
  public double markup() {
    return markup.getMarkup();
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
        markup = Markup.EVENING;
        break;
      default:
        markup = (isWeekend()) ? Markup.WEEKEND_DAY : Markup.NORMAL_DAY;
    }
  }
  
  /**
   * Represents all types of markup rates.
   *
   * @author Ricko
   * @version 1.0
   */
  public enum Markup {
    NORMAL_DAY(1.1d),
    WEEKEND_DAY(1.15d),
    EVENING(1.08d),
    WHOLESALE(1.07d);
    
    private double markup;
    
    Markup(double markup) {
      this.markup = markup;
    }
    
    public double getMarkup() {
      return markup;
    }
  }
  
}
