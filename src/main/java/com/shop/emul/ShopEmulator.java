package com.shop.emul;

/**
 * Drives the shop emulation functionality.
 * Main entry point class.
 *
 * @author KossKucher
 * @version 1.0
 */
public class ShopEmulator {
  
  public static void main(String[] args) {
    DbManager.get();
    System.out.println(Generator.buyersRand());
    Order order = Generator.genOrder();
  }
  
}
