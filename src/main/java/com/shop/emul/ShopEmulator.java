package com.shop.emul;

/**
 * Drives the shop emulation routine.
 * Main entry point class.
 *
 * @author KossKucher
 * @version 1.0
 */
public class ShopEmulator {
  
  static {
    new PropertiesReader(Config.PROPERTIES_FILE.getDefault()).pushToSystem();
  }
  
  /**
   * Main entry point method.
   *
   * @param args {@code String[]} array of command line parameters
   */
  public static void main(String[] args) {
    TimeKeeper calendar = TimeKeeper.get();
    OrderProcessor database = DbManager.get();
    OrderProcessor cashbox = Cashbox.get();
    Order order;
    while (calendar.isMonthAlive()) {
      for (int i = 0, j = Generator.randBuyers(); i < j; i++) {
        order = Generator.genOrder();
        database.process(order);
        cashbox.process(order);
      }
      calendar.tickTock();
    }
    cashbox.extractData();
    database.extractData();
  }
}
