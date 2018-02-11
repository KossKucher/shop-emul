package com.shop.emul;

/**
 * Drives the shop emulation routine.
 * Main entry point class.
 *
 * @author KossKucher
 * @version 1.0
 */
public class ShopEmulator {
  
  public static void main(String[] args) {
    TimeKeeper timeKeeper = TimeKeeper.get();
    DbManager dbManager = DbManager.get();
    Cashbox cashbox = Cashbox.get();
    Order order;
    while (!timeKeeper.isMonthEnded()) {
      order = Generator.genOrder();
      dbManager.process(order);
      cashbox.process(order);
      timeKeeper.tickTock();
    }
    cashbox.writeReport();
    dbManager.backupBase();
  }
  
}
