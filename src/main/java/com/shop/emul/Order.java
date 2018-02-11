package com.shop.emul;

import java.util.HashMap;

/**
 * Customized Map, represents an order entity.
 */
public class Order extends HashMap<Integer, Integer> {
  
  private OrderType orderType;
  
  /**
   * Constructor
   */
  public Order(OrderType type) {
    orderType = type;
  }
  
  /**
   * Customized put method to increment mapped values instead of overwrite.
   *
   * @param key   {@code int} to be used as a key
   * @param value {@code int} to be used as a value mapped to key
   */
  @Override
  public Integer put(Integer key, Integer value) {
    if (containsKey(key)) {
      return super.put(key, get(key) + value);
    }
    return super.put(key, value);
  }
  
  /**
   * Generated getter.
   */
  public OrderType getOrderType() {
    return orderType;
  }
  
  /**
   * Order type constants.
   */
  public enum OrderType {
    SELL,
    BUY
  }
}
