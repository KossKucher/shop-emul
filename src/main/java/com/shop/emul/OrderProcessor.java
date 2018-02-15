package com.shop.emul;

/**
 * Order processing abstraction interface.
 *
 * @author KossKucher
 * @version 1.0
 */
public interface OrderProcessor {
  
  /**
   * Processes incoming orders.
   *
   * @param order {@link Order} to be processed.
   */
  void process(Order order);
  
  /**
   * Extracts data to files.
   */
  void extractData();
  
}
