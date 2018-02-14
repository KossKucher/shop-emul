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
   * @param args {@code String[]} array of command line parameter
   */
  public static void main(String[] args) {
    TimeKeeper timeKeeper = TimeKeeper.get();
    DbManager dbManager = DbManager.get();
    Cashbox cashbox = Cashbox.get();
    Order order;
    while (!timeKeeper.isMonthEnded()) {
      for (int i = 0, j = Generator.randBuyers(); i < j; i++) {
        order = Generator.genOrder();
        dbManager.process(order);
        cashbox.process(order);
      }
      timeKeeper.tickTock();
    }
    cashbox.writeReport();
    dbManager.backupBase();
  }
}
